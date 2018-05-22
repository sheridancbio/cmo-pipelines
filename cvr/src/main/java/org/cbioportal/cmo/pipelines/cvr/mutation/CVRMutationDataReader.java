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
 * @author heinsz
 */
public class CVRMutationDataReader implements ItemStreamReader<AnnotatedRecord> {
    @Value("#{jobParameters[stagingDirectory]}")
    private String stagingDirectory;

    @Value("#{jobParameters[forceAnnotation]}")
    private boolean forceAnnotation;

    @Value("#{jobParameters[stopZeroVariantWarnings]}")
    private boolean stopZeroVariantWarnings;

    @Autowired
    public CVRUtilities cvrUtilities;

    @Autowired
    public CvrSampleListUtil cvrSampleListUtil;

    @Autowired
    private Annotator annotator;

    private List<AnnotatedRecord> mutationRecords = new ArrayList<>();
    private Map<String, List<AnnotatedRecord>> mutationMap = new HashMap<>();

    private File mutationFile;
    Set<String> header = new LinkedHashSet<>();
    private Set<String> additionalPropertyKeys = new LinkedHashSet<>();

    Logger log = Logger.getLogger(CVRMutationDataReader.class);

    @Override
    public void open(ExecutionContext ec) throws ItemStreamException {
        CVRData cvrData = new CVRData();
        // load cvr data from cvr_data.json file
        File cvrFile = new File(stagingDirectory, CVRUtilities.CVR_FILE);
        try {
            cvrData = cvrUtilities.readJson(cvrFile);
        } catch (IOException e) {
            log.error("Error reading file: " + cvrFile.getName());
            throw new ItemStreamException(e);
        }
        // load mutation records from cvr data
        loadMutationRecordsFromJson(cvrData);

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
            } catch (Exception e) {
                log.error("Error loading data from mutation file: " + mutationFile.getName());
                throw new ItemStreamException(e);
            }
        }
        // add header and filename to write to for writer
        ec.put("mutationHeader", new ArrayList(header));
        ec.put("mafFilename", CVRUtilities.MUTATION_FILE);
    }

    private void loadMutationRecordsFromJson(CVRData cvrData) {
        int snpsToAnnotateCount = 0;
        int annotatedSnpsCount = 0;
        // this loop is just to get the snpsToAnnotateCount
        for (CVRMergedResult result : cvrData.getResults()) {
            snpsToAnnotateCount += result.getAllSignedoutCvrSnps().size();
        }
        log.info(String.valueOf(snpsToAnnotateCount) + " records to annotate");
        for (CVRMergedResult result : cvrData.getResults()) {
            String sampleId = result.getMetaData().getDmpSampleId();
            String somaticStatus = result.getMetaData().getSomaticStatus() != null ? result.getMetaData().getSomaticStatus() : "N/A";
            int countSignedOutSnps = result.getAllSignedoutCvrSnps().size();
            for (CVRSnp snp : result.getAllSignedoutCvrSnps()) {
                annotatedSnpsCount++; // increment on the fly to keep track of progress
                if (annotatedSnpsCount % 500 == 0) {
                    log.info("\tOn record " + String.valueOf(annotatedSnpsCount) + " out of " + String.valueOf(snpsToAnnotateCount) + ", annotation " + String.valueOf((int)(((annotatedSnpsCount * 1.0)/snpsToAnnotateCount) * 100)) + "% complete");
                }
                MutationRecord record = cvrUtilities.buildCVRMutationRecord(snp, sampleId, somaticStatus);
                AnnotatedRecord annotatedRecord;
                try {
                    annotatedRecord = annotator.annotateRecord(record, false, "mskcc", true);
                } catch (HttpServerErrorException e) {
                    log.warn("Failed to annotate a record from json! Sample: " + sampleId + " Variant: " + cvrUtilities.getVariantAsHgvs(record));
                    annotatedRecord = cvrUtilities.buildCVRAnnotatedRecord(record);
                } catch (GenomeNexusAnnotationFailureException e) {
                    log.warn("Failed to annotate a record from json! Sample: " + sampleId + " Variant: " + cvrUtilities.getVariantAsHgvs(record) + " : " + e.getMessage());
                    annotatedRecord = cvrUtilities.buildCVRAnnotatedRecord(record);
                }
                mutationRecords.add(annotatedRecord);
                header.addAll(annotatedRecord.getHeaderWithAdditionalFields());
                additionalPropertyKeys.addAll(annotatedRecord.getAdditionalProperties().keySet());
                mutationMap.getOrDefault(annotatedRecord.getTUMOR_SAMPLE_BARCODE(), new ArrayList()).add(annotatedRecord);
            }
            cvrSampleListUtil.updateSignedoutSampleSnpCounts(sampleId, countSignedOutSnps);
            if (!stopZeroVariantWarnings && countSignedOutSnps == 0) {
                log.warn(sampleId + " has no snps (might be whitelisted)");
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
            // skip if new sample or if mutation record for sample seen already
            if (cvrSampleListUtil.getNewDmpSamples().contains(to_add.getTUMOR_SAMPLE_BARCODE()) ||
                    cvrUtilities.isDuplicateRecord(to_add, mutationMap.get(to_add.getTUMOR_SAMPLE_BARCODE()))) {
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
            cvrSampleListUtil.updateSignedoutSampleSnpCounts(to_add.getTUMOR_SAMPLE_BARCODE(), 1);
            mutationRecords.add(to_add_annotated);
            mutationMap.getOrDefault(to_add_annotated.getTUMOR_SAMPLE_BARCODE(), new ArrayList()).add(to_add_annotated);
            header.addAll(to_add_annotated.getHeaderWithAdditionalFields());
            additionalPropertyKeys.addAll(to_add_annotated.getAdditionalProperties().keySet());
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
