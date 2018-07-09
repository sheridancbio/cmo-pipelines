/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cbioportal.cmo.pipelines.cvr.linkedimpactcase;

import org.cbioportal.cmo.pipelines.cvr.CVRUtilities;
import org.cbioportal.cmo.pipelines.cvr.CvrSampleListUtil;
import org.cbioportal.cmo.pipelines.cvr.model.*;

import java.io.*;
import java.util.*;
import org.apache.log4j.Logger;

import org.springframework.batch.item.*;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;

/**
 *
 * @author heinsz
 */
public class LinkedMskimpactCaseReader implements ItemStreamReader<LinkedMskimpactCaseRecord> {
    @Value("#{jobParameters[stagingDirectory]}")
    private String stagingDirectory;

    @Autowired
    public CVRUtilities cvrUtilities;

    @Autowired
    public CvrSampleListUtil cvrSampleListUtil;

    private List<LinkedMskimpactCaseRecord> linkedIds = new ArrayList<>();

    private static final Logger LOG = Logger.getLogger(LinkedMskimpactCaseReader.class);

    @Override
    public void open(ExecutionContext ec) throws ItemStreamException {
        CVRData cvrData = new CVRData();
        // load cvr data from cvr_data.json file
        File cvrFile = new File(stagingDirectory, cvrUtilities.CVR_FILE);
        try {
            cvrData = cvrUtilities.readJson(cvrFile);
        } catch (IOException e) {
            LOG.error("Error reading file: " + cvrFile.getName());
            throw new ItemStreamException(e);
        }
        for (CVRMergedResult result : cvrData.getResults()) {
            linkedIds.add(new LinkedMskimpactCaseRecord(result.getMetaData().getDmpSampleId(), result.getMetaData().getLinkedMskimpactCase()));
        }
        // load existing linked ARCHER sample data
        loadExistingLinkedIds();
    }

    private void loadExistingLinkedIds() {
        File stagingFile = new File(stagingDirectory, cvrUtilities.CORRESPONDING_ID_FILE);
        if (!stagingFile.exists()) {
            LOG.warn("File does not exist - skipping data loading from linked ARCHER samples file: " + stagingFile.getName());
            return;
        }
        LOG.info("Loading linked ARCHER sample data from: " + stagingFile.getName());
        DelimitedLineTokenizer tokenizer = new DelimitedLineTokenizer(DelimitedLineTokenizer.DELIMITER_TAB);
        DefaultLineMapper<LinkedMskimpactCaseRecord> mapper = new DefaultLineMapper<>();
        mapper.setLineTokenizer(tokenizer);
        mapper.setFieldSetMapper(new LinkedImpactCaseFieldSetMapper());

        FlatFileItemReader<LinkedMskimpactCaseRecord> reader = new FlatFileItemReader<>();
        reader.setResource(new FileSystemResource(stagingFile));
        reader.setLineMapper(mapper);
        reader.setLinesToSkip(1);
        reader.open(new ExecutionContext());

        try {
            LinkedMskimpactCaseRecord to_add;
            while ((to_add = reader.read()) != null) {
                // only add samples that are not in the new dmp sample list
                if (!cvrSampleListUtil.getNewDmpSamples().contains(to_add.getSAMPLE_ID())) {
                    linkedIds.add(to_add);
                }
            }
        }
        catch (Exception e) {
            LOG.error("Error reading linked ARCHER sample data from file: " + stagingFile.getName());
            throw new ItemStreamException(e);
        }
        finally {
            reader.close();
        }
    }

    @Override
    public void update(ExecutionContext ec) throws ItemStreamException {}

    @Override
    public void close() throws ItemStreamException {}

    @Override
    public LinkedMskimpactCaseRecord read() throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException {
        if (!linkedIds.isEmpty()) {
            return linkedIds.remove(0);
        }
        return null;
    }
}
