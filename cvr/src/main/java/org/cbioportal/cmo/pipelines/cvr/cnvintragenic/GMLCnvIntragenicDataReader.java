/*
 * Copyright (c) 2018 - 2022 Memorial Sloan-Kettering Cancer Center.
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

package org.cbioportal.cmo.pipelines.cvr.cnvintragenic;

import com.google.common.base.Strings;
import org.cbioportal.cmo.pipelines.cvr.CVRUtilities;
import org.cbioportal.cmo.pipelines.cvr.CvrSampleListUtil;
import org.cbioportal.cmo.pipelines.cvr.model.staging.CVRSvRecord;
import org.cbioportal.cmo.pipelines.cvr.model.GMLData;
import org.cbioportal.cmo.pipelines.cvr.model.GMLResult;
import org.cbioportal.cmo.pipelines.cvr.model.GMLCnvIntragenicVariant;

import java.io.*;
import java.util.*;
import org.apache.log4j.Logger;


import org.springframework.batch.item.*;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.beans.factory.annotation.*;
import org.springframework.core.io.FileSystemResource;

/**
 *
 * @author ochoaa
 */
public class GMLCnvIntragenicDataReader implements ItemStreamReader<CVRSvRecord> {

    @Autowired
    public CvrSampleListUtil cvrSampleListUtil;

    @Autowired
    public CVRUtilities cvrUtilities;

    @Value("#{jobParameters[stagingDirectory]}")
    private String stagingDirectory;

    private Set<String> gmlCnvIntragenicSeen = new HashSet<>();
    private List<CVRSvRecord> gmlCnvIntragenicRecords = new ArrayList<>();

    private Logger LOG = Logger.getLogger(GMLCnvIntragenicDataReader.class);

    @Override
    public void open(ExecutionContext ec) throws ItemStreamException {
        // load new germline cnv-intragenic from json as SV
        processJsonFile();
        // load existing germline cnv-intragenic
        processGmlCnvIntragenicFile();

        // set cnvintragenic file and header to write to for processor, writer
        ec.put("cnvIntragenicFilename", CVRUtilities.CNV_INTRAGENIC_GML_FILE); // TODO : even if this exists as a separate file in the repository, it will probably need to be merged into the (somatic) data_SV file by the time the fetcher is done with the germline fetch. Otherwise it will not be imported
        ec.put("svHeader", CVRSvRecord.getFieldNames());
    }

    private void processJsonFile() {
        GMLData gmlData = new GMLData();
        // load gml cvr data from cvr_gml_data.json file
        File cvrGmlFile =  new File(stagingDirectory, CVRUtilities.GML_FILE);
        try {
            gmlData = cvrUtilities.readGMLJson(cvrGmlFile);
        } catch (IOException ex) {
            LOG.error("Error reading file: " + cvrGmlFile);
            ex.printStackTrace();
        }
        // load all new cnv-intragenic germline events from json
        for (GMLResult result : gmlData.getResults()) {
            if (result.getCnvIntragenicVariantsGml().isEmpty()) {
                continue;
            }
            // build germline sv records from cnv intragenic events for each patient sample
            String patientId = result.getMetaData().getDmpPatientId();
            List<String> samples = cvrSampleListUtil.getGmlPatientSampleMap().get(patientId);
            if (samples != null) {
                for (GMLCnvIntragenicVariant cnv : result.getCnvIntragenicVariantsGml()) {
                    for (String sampleId : samples) {
                        // TODO: This must be changed to SV records, and be written using the SV writer ... but, is merge needed too?
                        CVRSvRecord record = new CVRSvRecord(cnv, sampleId);
                        String svKey = getSvKey(record);
                        // skip if already added sv event for sample
                        if (!gmlCnvIntragenicSeen.add(svKey)) {
                            continue;
                        }
                        gmlcnvIntragrenicRecords.add(record);
                    }
                }
            }
        }
    }

    private void processGmlCnvIntragenicFile() {
        File gmlCnvIntragenicFile = new File(stagingDirectory, CVRUtilities.CNV_INTRAGENIC_GML_FILE);
        if (!gmlCnvIntragenicFile.exists()) {
            LOG.info("File does not exist - skipping data loading from germline cnv intragenic file: " + gmlCnvIntragenicFile.getName());
            return;
        }
        LOG.info("Loading germline cnv intragenic data from: " + gmlCnvIntragenicFile.getName());

        DelimitedLineTokenizer tokenizer = new DelimitedLineTokenizer(DelimitedLineTokenizer.DELIMITER_TAB);
        DefaultLineMapper<CVRFusionRecord> mapper = new DefaultLineMapper<>();
        mapper.setLineTokenizer(tokenizer);
//        mapper.setFieldSetMapper(new CVRGMLFusionFieldSetMapper()); // This mapper is obsolete .. adapt it or make a new one for saved SV records hold gml cnv intragenic events

        FlatFileItemReader<CVRFusionRecord> reader = new FlatFileItemReader<>();
        reader.setResource(new FileSystemResource(gmlCnvIntragenicFile));
        reader.setLineMapper(mapper);
        reader.setLinesToSkip(1);
        reader.open(new ExecutionContext());

        try {
            CVRSvRecord to_add;
            while ((to_add = reader.read()) != null) {
                String patientId = cvrSampleListUtil.getSamplePatientId(to_add.getSampleId());
                // check that matching patient id can be found from patient-sample mapping
                // and whether patient is in new dmp germline patients (to prevent duplicates)
                if (!Strings.isNullOrEmpty(patientId) && !cvrSampleListUtil.getNewDmpGmlPatients().contains(patientId)) {
                    String svKey = getSvKey(to_add);
                    if (gmlCnvIntragenicSeen.add(svKey)) {
                        gmlCnvIntragenicRecords.add(to_add);
                    }
                }
            }
        } catch (Exception e){
            LOG.info("Error loading data from germline fusions file: " + gmlCnvIntragenicFile.getName());
            throw new ItemStreamException(e);
        }
        reader.close();
    }

    private String getSvKey(CVRSvRecord record) {
        //TODO : here we are using a key to tell duplicate Sv Records, comparing what was downloaded previously, sitting in a data file, versus what came down from the json. We need to figure out what duplication means when SV records can have just a single gene referene. Is an SV with gene G and chrom 2 distinct from an SV with gene G and chrom 3 (both fields for site 1)
        return record.getSampleId() + "|" + record.getSite1_Gene() + "|" + record.getSite2_Gene(); //TODO : is that adequate? Any other distinguising fields? (probably)
    }

    @Override
    public void update(ExecutionContext ec) throws ItemStreamException {
    }

    @Override
    public void close() throws ItemStreamException {
    }

    @Override
    public CVRSvRecord read() throws Exception {
        while (!gmlCnvIntragenicRecords.isEmpty()) {
            CVRSvRecord record = gmlCnvIntragenicRecords.remove(0);
            if (!cvrSampleListUtil.getPortalSamples().contains(record.getSampleId())) {
                cvrSampleListUtil.addSampleRemoved(record.getSampleId());
                continue;
            }
            return record;
        }
        return null;
    }

}
