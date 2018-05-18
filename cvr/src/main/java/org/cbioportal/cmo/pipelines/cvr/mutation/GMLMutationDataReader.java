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

package org.cbioportal.cmo.pipelines.cvr.mutation;

import org.cbioportal.annotator.*;
import org.cbioportal.cmo.pipelines.cvr.*;
import org.cbioportal.cmo.pipelines.cvr.model.*;
import org.cbioportal.models.*;

import java.io.*;
import java.util.*;
import org.apache.log4j.Logger;

import org.springframework.batch.item.*;
import org.springframework.batch.item.file.*;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.beans.factory.annotation.*;
import org.springframework.core.io.FileSystemResource;
import org.springframework.web.client.HttpServerErrorException;

/**
 *
 * @author jake
 */
public class GMLMutationDataReader implements ItemStreamReader<AnnotatedRecord> {

    @Value("#{jobParameters[stagingDirectory]}")
    private String stagingDirectory;

    @Autowired
    public CvrSampleListUtil cvrSampleListUtil;

    @Value("#{jobParameters[forceAnnotation]}")
    private boolean forceAnnotation;

    @Autowired
    public CVRUtilities cvrUtilities;

    @Autowired
    private Annotator annotator;

    private List<AnnotatedRecord> mutationRecords = new ArrayList();
    private Map<String, List<AnnotatedRecord>> mutationMap = new HashMap<>();
    private File mutationFile;
    private Set<String> additionalPropertyKeys = new LinkedHashSet<>();
    private Set<String> header = new LinkedHashSet<>();
    private Set<String> germlineSamples = new HashSet<>();

    Logger log = Logger.getLogger(GMLMutationDataReader.class);

    @Override
    public void open(ExecutionContext ec) throws ItemStreamException {
        GMLData gmlData = new GMLData();
        // load gml cvr data from cvr_gml_data.json file
        File cvrGmlFile =  new File(stagingDirectory, CVRUtilities.GML_FILE);
        try {
            gmlData = cvrUtilities.readGMLJson(cvrGmlFile);
        } catch (IOException e) {
            log.error("Error reading file: " + cvrGmlFile);
            throw new ItemStreamException(e);
        }
        // load mutation records from gml cvr data
        loadMutationRecordsFromJson(gmlData);

        // load mutation records from existing maf
        this.mutationFile = new File(stagingDirectory, CVRUtilities.MUTATION_FILE);
        if (!mutationFile.exists()) {
            log.info("File does not exist - skipping data loading from mutation file: " + mutationFile.getName());
        }
        else {
            try {
                loadExistingMutationRecords();
                // add comment lines to execution context
                ec.put("commentLines", cvrUtilities.processFileComments(mutationFile));
            }
            catch (Exception e) {
                log.error("Error loading data from mutation file: " + mutationFile.getName());
                throw new ItemStreamException(e);
            }
        }
        // add header and filename to write to for writer
        ec.put("mutationHeader", new ArrayList(header));
        ec.put("mafFilename", CVRUtilities.MUTATION_FILE);
    }

    private void loadMutationRecordsFromJson(GMLData gmlData) {
        int snpsToAnnotateCount = 0;
        int annotatedSnpsCount = 0;
        // this loop is just to get the snpsToAnnotateCount
        for (GMLResult result : gmlData.getResults()) {
            String patientId = result.getMetaData().getDmpPatientId();
            List<String> samples = cvrSampleListUtil.getGmlPatientSampleMap().get(patientId);
            List<GMLSnp> snps = result.getAllSignedoutGmlSnps();
            if (samples != null && snps != null) {
                for (GMLSnp snp : snps) {
                    snpsToAnnotateCount += samples.size();
                }
            }
        }
        log.info(String.valueOf(snpsToAnnotateCount) + " germline records to annotate");
        for (GMLResult result : gmlData.getResults()) {
            String patientId = result.getMetaData().getDmpPatientId();
            List<String> samples = cvrSampleListUtil.getGmlPatientSampleMap().get(patientId);
            List<GMLSnp> snps = result.getAllSignedoutGmlSnps();
            if (samples != null && !snps.isEmpty()) {
                for (GMLSnp snp : snps) {
                    for (String sampleId : samples) {
                        annotatedSnpsCount++;
                        if (annotatedSnpsCount % 500 == 0) {
                            log.info("\tOn record " + String.valueOf(annotatedSnpsCount) + " out of " + String.valueOf(snpsToAnnotateCount) + ", annotation " + String.valueOf((int)(((annotatedSnpsCount * 1.0)/snpsToAnnotateCount) * 100)) + "% complete");
                        }
                        MutationRecord record = cvrUtilities.buildGMLMutationRecord(snp, sampleId);
                        AnnotatedRecord annotatedRecord;
                        try {
                            annotatedRecord = annotator.annotateRecord(record, false, "mskcc", true);
                        }
                        catch (HttpServerErrorException e) {
                            log.warn("Failed to annotate a record from json! Sample: " + sampleId + " Variant: " + cvrUtilities.getVariantAsHgvs(record));
                            annotatedRecord = cvrUtilities.buildCVRAnnotatedRecord(record);
                        } catch (GenomeNexusAnnotationFailureException e) {
                            log.warn("Failed to annotate a record from json! Sample: " + sampleId + " Variant: " + cvrUtilities.getVariantAsHgvs(record) + " : " + e.getMessage());
                            annotatedRecord = cvrUtilities.buildCVRAnnotatedRecord(record);
                        }
                        mutationRecords.add(annotatedRecord);
                        header.addAll(record.getHeaderWithAdditionalFields());
                        additionalPropertyKeys.addAll(record.getAdditionalProperties().keySet());
                        mutationMap.getOrDefault(annotatedRecord.getTUMOR_SAMPLE_BARCODE(), new ArrayList()).add(annotatedRecord);
                        germlineSamples.add(sampleId);
                    }
                }
            }
        }
    }

    private void loadExistingMutationRecords() throws Exception {
        log.info("Loading mutation data from: " + mutationFile.getName());
        DelimitedLineTokenizer tokenizer = new DelimitedLineTokenizer(DelimitedLineTokenizer.DELIMITER_TAB);
        DefaultLineMapper<MutationRecord> mapper = new DefaultLineMapper<>();
        mapper.setLineTokenizer(tokenizer);
        mapper.setFieldSetMapper(new CVRMutationFieldSetMapper());

        FlatFileItemReader<MutationRecord> reader = new FlatFileItemReader<>();
        reader.setResource(new FileSystemResource(mutationFile));
        reader.setLineMapper(mapper);
        reader.setLinesToSkip(1);
        reader.setSkippedLinesCallback(new LineCallbackHandler() {
            @Override
            public void handleLine(String line) {
                tokenizer.setNames(line.split("\t"));
            }
        });
        reader.open(new ExecutionContext());
        MutationRecord to_add;
        while ((to_add = reader.read()) != null && to_add.getTUMOR_SAMPLE_BARCODE() != null) {
            // skip if record already seen or if current record is a germline sample and record is a GERMLINE variant
            if (cvrUtilities.isDuplicateRecord(to_add, mutationMap.get(to_add.getTUMOR_SAMPLE_BARCODE())) ||
                    (germlineSamples.contains(to_add.getTUMOR_SAMPLE_BARCODE()) && to_add.getMUTATION_STATUS().equals("GERMLINE"))) {
                continue;
            }
            AnnotatedRecord to_add_annotated;
            try {
                to_add_annotated = annotator.annotateRecord(to_add, false, "mskcc", forceAnnotation);
            } catch (HttpServerErrorException e) {
                log.warn("Failed to annotate a record from existing file! Sample: " + to_add.getTUMOR_SAMPLE_BARCODE() + " Variant: " + cvrUtilities.getVariantAsHgvs(to_add));
                to_add_annotated = cvrUtilities.buildCVRAnnotatedRecord(to_add);
            } catch (GenomeNexusAnnotationFailureException e) {
                log.warn("Failed to annotate a record from existing file! Sample: " + to_add.getTUMOR_SAMPLE_BARCODE() + " Variant: " + cvrUtilities.getVariantAsHgvs(to_add) + " : " + e.getMessage());
                to_add_annotated = cvrUtilities.buildCVRAnnotatedRecord(to_add);
            }
            mutationRecords.add(to_add_annotated);
            header.addAll(to_add_annotated.getHeaderWithAdditionalFields());
            additionalPropertyKeys.addAll(to_add_annotated.getAdditionalProperties().keySet());
            mutationMap.getOrDefault(to_add_annotated.getTUMOR_SAMPLE_BARCODE(), new ArrayList()).add(to_add_annotated);
        }
        reader.close();
    }
    @Override
    public void update(ExecutionContext ec) throws ItemStreamException {
    }

    @Override
    public void close() throws ItemStreamException {
    }

    @Override
    public AnnotatedRecord read() throws Exception {
        while (!mutationRecords.isEmpty()) {
            AnnotatedRecord annotatedRecord = mutationRecords.remove(0);
            if (!cvrSampleListUtil.getPortalSamples().contains(annotatedRecord.getTUMOR_SAMPLE_BARCODE())) {
                cvrSampleListUtil.addSampleRemoved(annotatedRecord.getTUMOR_SAMPLE_BARCODE());
                continue;
            }
            for (String additionalProperty : additionalPropertyKeys) {
                Map<String, String> additionalProperties = annotatedRecord.getAdditionalProperties();
                if (!additionalProperties.keySet().contains(additionalProperty)) {
                    additionalProperties.put(additionalProperty, "");
                }
            }
            return annotatedRecord;
        }
        return null;
    }

}
