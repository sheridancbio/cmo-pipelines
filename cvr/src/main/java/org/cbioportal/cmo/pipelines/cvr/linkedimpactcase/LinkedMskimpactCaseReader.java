/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cbioportal.cmo.pipelines.cvr.linkedimpactcase;

import java.io.*;
import java.util.*;
import org.apache.log4j.Logger;
import org.cbioportal.cmo.pipelines.cvr.CVRUtilities;
import org.cbioportal.cmo.pipelines.cvr.model.*;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemStreamException;
import org.springframework.batch.item.ItemStreamReader;
import org.springframework.batch.item.NonTransientResourceException;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

/**
 *
 * @author heinsz
 */
public class LinkedMskimpactCaseReader implements ItemStreamReader<LinkedMskimpactCaseRecord> {
    @Value("#{jobParameters[stagingDirectory]}")
    private String stagingDirectory;

    @Autowired
    public CVRUtilities cvrUtilities;
    
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