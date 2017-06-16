/*
 * Copyright (c) 2016 - 2017 Memorial Sloan-Kettering Cancer Center.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY, WITHOUT EVEN THE IMPLIED WARRANTY OF MERCHANTABILITY OR FITNESS
 * FOR A PARTICULAR PURPOSE. The software and documentation provided hereunder
 * is on an "as is" basis, and Memorial Sloan-Kettering Cancer Center has no
 * obligations to provide maintenance, support, updates, enhancements or
 * modifications. In no event shall Memorial Sloan-Kettering Cancer Center be
 * liable to any party for direct, indirect, special, incidental or
 * consequential damages, including lost profits, arising out of the use of this
 * software and its documentation, even if Memorial Sloan-Kettering Cancer
 * Center has been advised of the possibility of such damage.
 */

/*
 * This file is part of cBioPortal CMO-Pipelines.
 *
 * cBioPortal is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/

package org.cbioportal.cmo.pipelines.cvr.clinical;

import org.cbioportal.cmo.pipelines.cvr.*;
import org.cbioportal.cmo.pipelines.cvr.model.*;

import java.io.*;
import java.util.*;
import java.text.SimpleDateFormat;
import org.apache.log4j.Logger;
import org.springframework.batch.item.*;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.beans.factory.annotation.*;
import org.springframework.core.io.FileSystemResource;

/**
 *
 * @author heinsz
 */
public class CVRClinicalDataReader implements ItemStreamReader<CVRClinicalRecord> {

    @Value("#{jobParameters[stagingDirectory]}")
    private String stagingDirectory;
    
    @Value("#{jobParameters[studyId]}")
    private String studyId;

    @Autowired
    public CVRUtilities cvrUtilities;
    
    @Autowired
    public CvrSampleListUtil cvrSampleListUtil;

    private List<CVRClinicalRecord> clinicalRecords = new ArrayList();
    private Map<String, List<CVRClinicalRecord>> patientToRecordMap = new HashMap();
    private SimpleDateFormat cvrDateFormat = new SimpleDateFormat("EEE, dd MMM yyyy kk:mm:ss zzz");

    Logger log = Logger.getLogger(CVRClinicalDataReader.class);

    @Override
    public void open(ExecutionContext ec) throws ItemStreamException {
        processClinicalFile(ec);      
        processJsonFile();
        if (studyId.equals("mskimpact")) {
            processAgeFile(ec);            
        }
        // updates portalSamplesNotInDmpList and dmpSamplesNotInPortal sample lists
        // portalSamples list is only updated if threshold check for max num samples to remove passes
        cvrSampleListUtil.updateSampleLists();
    }

    @Override
    public void update(ExecutionContext ec) throws ItemStreamException {
    }

    @Override
    public void close() throws ItemStreamException {
    }

    @Override
    public CVRClinicalRecord read() throws Exception {
        if (!clinicalRecords.isEmpty()) {
            CVRClinicalRecord record = clinicalRecords.remove(0);
            // portal samples may or may not be filtered by 'portalSamplesNotInDmp' is threshold check above
            // so we want to skip samples that aren't in this list
            if (!cvrSampleListUtil.getPortalSamples().contains(record.getSAMPLE_ID())) {
                cvrSampleListUtil.addSampleRemoved(record.getSAMPLE_ID());
                return read();
            }
            return record;
        }
        return null;
    }
    
    private void processClinicalFile(ExecutionContext ec) {
        File mskimpactClinicalFile = new File(stagingDirectory, cvrUtilities.CLINICAL_FILE);
        if (!mskimpactClinicalFile.exists()) {
            log.error("File does not exist - skipping data loading from clinical file: " + mskimpactClinicalFile.getName());
            return;
        }
        log.info("Loading clinical data from: " + mskimpactClinicalFile.getName());
        DelimitedLineTokenizer tokenizer = new DelimitedLineTokenizer(DelimitedLineTokenizer.DELIMITER_TAB);
        DefaultLineMapper<CVRClinicalRecord> mapper = new DefaultLineMapper<>();
        mapper.setLineTokenizer(tokenizer);
        mapper.setFieldSetMapper(new CVRClinicalFieldSetMapper());

        FlatFileItemReader<CVRClinicalRecord> reader = new FlatFileItemReader<>();
        reader.setResource(new FileSystemResource(mskimpactClinicalFile));
        reader.setLineMapper(mapper);
        reader.setLinesToSkip(1);
        reader.open(ec);

        try {
            CVRClinicalRecord to_add;
            while ((to_add = reader.read()) != null) {
                if (!cvrSampleListUtil.getNewDmpSamples().contains(to_add.getSAMPLE_ID()) && to_add.getSAMPLE_ID() != null) {
                    clinicalRecords.add(to_add);
                    cvrSampleListUtil.addPortalSample(to_add.getSAMPLE_ID());
                    List<CVRClinicalRecord> records = patientToRecordMap.getOrDefault(to_add.getPATIENT_ID(), new ArrayList<CVRClinicalRecord>());
                    records.add(to_add);
                    patientToRecordMap.put(to_add.getPATIENT_ID(), records);
                }
            }
        }
        catch (Exception e) {
            log.error("Error reading data from clinical file: " + mskimpactClinicalFile.getName());
            throw new ItemStreamException(e);
        }
        finally {
            reader.close();            
        }    
    }
    
    private void processJsonFile() {
        CVRData cvrData = new CVRData();
        // load cvr data from cvr_data.json file
        File cvrFile = new File(stagingDirectory, cvrUtilities.CVR_FILE);
        try {
            cvrData = cvrUtilities.readJson(cvrFile);
        } catch (IOException e) {
            log.error("Error reading file: " + cvrFile.getName());
            throw new ItemStreamException(e);
        }
        for (CVRMergedResult result : cvrData.getResults()) {
            CVRClinicalRecord record = new CVRClinicalRecord(result.getMetaData());
            List<CVRClinicalRecord> records = patientToRecordMap.getOrDefault(record.getPATIENT_ID(), new ArrayList<CVRClinicalRecord>());
            records.add(record);
            patientToRecordMap.put(record.getPATIENT_ID(), records);          
            clinicalRecords.add(record);
        }        
    }
    
    private void processAgeFile(ExecutionContext ec) {
        File mskimpactAgeFile = new File(stagingDirectory, cvrUtilities.DARWIN_AGE_FILE);
        if (!mskimpactAgeFile.exists()) {
            log.error("File does not exist - skipping data loading from age file: " + mskimpactAgeFile.getName());
            return;
        }
        log.info("Loading age data from : " + mskimpactAgeFile.getName());
        DelimitedLineTokenizer tokenizer = new DelimitedLineTokenizer(DelimitedLineTokenizer.DELIMITER_TAB);
        DefaultLineMapper<MskimpactAge> mapper = new DefaultLineMapper<>();
        mapper.setLineTokenizer(tokenizer);
        mapper.setFieldSetMapper(new MskimpactAgeFieldSetMapper());
        FlatFileItemReader<MskimpactAge> reader = new FlatFileItemReader<>();
        reader.setResource(new FileSystemResource(mskimpactAgeFile));
        reader.setLineMapper(mapper);
        reader.setLinesToSkip(1);
        reader.open(ec);

        try {
            MskimpactAge mskimpactAge;
            while ((mskimpactAge = reader.read()) != null) {
                if (patientToRecordMap.keySet().contains(mskimpactAge.getPATIENT_ID())) {
                    for (CVRClinicalRecord record : patientToRecordMap.get(mskimpactAge.getPATIENT_ID())) {
                        if (record.getSEQ_DATE() != null && !record.getSEQ_DATE().isEmpty() && !record.equals("NA")) {
                            Date now = new Date();
                            Date cvrDateSequenced = cvrDateFormat.parse(record.getSEQ_DATE());
                            // We know age of patient now from darwin, and the time at which the patient was sequenced.
                            // The age of the patient when sequenced is therefore AGE_NOW - YEARS_SINCE_SEQUENCING
                            // This converts the date arithmetic from miliseconds to years.
                            // 1000ms -> 1s, 60s -> 1m, 60m -> 1h, 24h -> 1d, 365.2422d -> 1y
                            Double diffYears = (now.getTime() - cvrDateSequenced.getTime()) / 1000L / 60L / 60L / 24L / 365.2422;
                            Double ageAtSeqReport = Math.ceil(Integer.parseInt(mskimpactAge.getAGE()) - diffYears);
                            if (ageAtSeqReport > 90) {
                                ageAtSeqReport = 90D;
                            }
                            if (ageAtSeqReport < 15) {
                                ageAtSeqReport = 15D;
                            }
                            record.setAGE_AT_SEQ_REPORT(String.valueOf(ageAtSeqReport.intValue()));
                        }
                        else {
                            record.setAGE_AT_SEQ_REPORT("NA");
                        }
                    }                        
                }
            }
        }
        catch (Exception e) {
            log.error("Error reading data from age file: " + mskimpactAgeFile.getName());
            throw new ItemStreamException(e);
        }
        finally {
            reader.close();
        }
    }
}
