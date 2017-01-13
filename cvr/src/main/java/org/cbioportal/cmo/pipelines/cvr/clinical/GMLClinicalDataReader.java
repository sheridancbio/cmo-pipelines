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

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.*;
import org.apache.log4j.Logger;
import org.cbioportal.cmo.pipelines.cvr.CVRUtilities;
import org.cbioportal.cmo.pipelines.cvr.model.CVRClinicalRecord;
import org.cbioportal.cmo.pipelines.cvr.model.GMLData;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemStreamException;
import org.springframework.batch.item.ItemStreamReader;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;

/**
 *
 * @author jake
 */
public class GMLClinicalDataReader implements ItemStreamReader<CVRClinicalRecord> {
    @Value("#{jobParameters[stagingDirectory]}")
    private String stagingDirectory;

    @Autowired
    public CVRUtilities cvrUtilities;

    private String seperator = File.separator;
    private GMLData gmlData;
    private String file;
    private List<CVRClinicalRecord> clinicalRecords = new ArrayList<>();

    Logger log = Logger.getLogger(GMLClinicalDataReader.class);

    @Override
    public void open(ExecutionContext ec) throws ItemStreamException {
        try {
            if (ec.get("gmlData") == null) {
                gmlData = cvrUtilities.readGMLJson(Paths.get(stagingDirectory).resolve(cvrUtilities.GML_FILE).toString());
            } else {
                gmlData = (GMLData)ec.get("gmlData");
            }
        } catch (IOException e) {
            throw new ItemStreamException("Failure to read " + file);
        }
        Path filename = Paths.get(stagingDirectory).resolve(cvrUtilities.CLINICAL_FILE);
        if (new File(filename.toString()).exists()) {
            FlatFileItemReader<CVRClinicalRecord> reader = new FlatFileItemReader<>();
            reader.setResource(new FileSystemResource(stagingDirectory + "/" + cvrUtilities.CLINICAL_FILE));
            DefaultLineMapper<CVRClinicalRecord> mapper = new DefaultLineMapper<>();
            DelimitedLineTokenizer tokenizer = new DelimitedLineTokenizer();
            tokenizer.setDelimiter("\t");
            mapper.setLineTokenizer(tokenizer);
            mapper.setFieldSetMapper(new CVRClinicalFieldSetMapper());
            reader.setLineMapper(mapper);
            reader.setLinesToSkip(1);
            reader.open(ec);

            CVRClinicalRecord to_add;
            Map<String, List<String>> patientSampleMap = new HashMap<>();
            try {
                while ((to_add = reader.read()) != null) {
                    if (patientSampleMap.containsKey(to_add.getPATIENT_ID())) {
                        patientSampleMap.get(to_add.getPATIENT_ID()).add(to_add.getSAMPLE_ID());
                    } else {
                        List<String> sampleList = new ArrayList<>();
                        sampleList.add(to_add.getSAMPLE_ID());
                        patientSampleMap.put(to_add.getPATIENT_ID(), sampleList);
                    }
                    for (String id : cvrUtilities.getNewIds()) {
                        if (id.contains(to_add.getPATIENT_ID())) {
                            to_add.set12_245_PARTC_CONSENTED("YES");
                            break;
                        }
                    }
                    clinicalRecords.add(to_add);
                    cvrUtilities.addAllIds(to_add.getSAMPLE_ID());
                }
            } catch (Exception e) {
                throw new ItemStreamException(e);
            }
            ec.put("patientSampleMap", patientSampleMap);
            reader.close();
        }
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
            return clinicalRecords.remove(0);
        }
        return null;
    }
}
