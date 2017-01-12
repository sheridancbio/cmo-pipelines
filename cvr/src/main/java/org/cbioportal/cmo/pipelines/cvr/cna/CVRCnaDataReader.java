/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cbioportal.cmo.pipelines.cvr.cna;

import java.io.*;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.*;
import org.cbioportal.cmo.pipelines.cvr.model.CVRData;
import org.cbioportal.cmo.pipelines.cvr.model.CVRMergedResult;
import org.cbioportal.cmo.pipelines.cvr.model.CVRCnvVariant;
import org.cbioportal.cmo.pipelines.cvr.model.CVRCnvIntragenicVariant;
import org.cbioportal.cmo.pipelines.cvr.model.CompositeCnaRecord;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemStreamException;
import org.springframework.batch.item.ItemStreamReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.apache.commons.collections.map.MultiKeyMap;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.cbioportal.cmo.pipelines.cvr.CVRUtilities;

/**
 *
 * @author heinsz
 */
public class CVRCnaDataReader implements ItemStreamReader<CompositeCnaRecord>{
    @Value("#{jobParameters[stagingDirectory]}")
    private String stagingDirectory;
    
    @Autowired
    public CVRUtilities cvrUtilities;
    
    Logger log = Logger.getLogger(CVRCnaDataReader.class);
    
    private CVRData cvrData;
    private String genePanel;
    private List<String> geneSymbols;
    private MultiKeyMap newCnaData = new MultiKeyMap();
    private MultiKeyMap cnaMap = new MultiKeyMap();    
    private Set<String> genes = new HashSet<>();
    private Set<String> samples = new HashSet<>();
    private Set<String> newSamples = new HashSet<>();
    
    private List<CompositeCnaRecord> cnaRecords = new ArrayList<>();

    @Override
    public void open(ExecutionContext ec) throws ItemStreamException {
        try {
            if(ec.get("cvrData") == null) {
                cvrData = cvrUtilities.readJson(Paths.get(stagingDirectory).resolve(cvrUtilities.CVR_FILE).toString());
            }
            else {
                cvrData = (CVRData)ec.get("cvrData");
            }
        }
        catch (IOException e)
        {
            throw new ItemStreamException("Failure to read " + stagingDirectory + "/" + cvrUtilities.CVR_FILE);
        }
                                       
        for (CVRMergedResult result : cvrData.getResults()) {
            String sampleId = result.getMetaData().getDmpSampleId();
            samples.add(sampleId);
            newSamples.add(sampleId);
            List<CVRCnvVariant> variants = result.getCnvVariants();
            for (CVRCnvVariant variant : variants) {
                if (variant.getClinicalSignedOut().equals("1")) {
                    genes.add(variant.getGeneId());
                    cnaMap.put(variant.getGeneId(), sampleId, resolveGeneFoldChange(variant.getGeneFoldChange()));
                    newCnaData.put(variant.getGeneId(), sampleId, resolveGeneFoldChange(variant.getGeneFoldChange()));
                }
            }
            List<CVRCnvIntragenicVariant> intragenicVariants = result.getCnvIntragenicVariants();
            for (CVRCnvIntragenicVariant variant : intragenicVariants){
                genes.add(variant.getGeneId());
                cnaMap.put(variant.getGeneId(), sampleId, "-1.5");
                newCnaData.put(variant.getGeneId(), sampleId, "-1.5");
            }
            try{
                cvrUtilities.importGenePanel(stagingDirectory + File.separator + cvrUtilities.GENE_PANEL);
            }
            catch(Exception e){
                throw new ItemStreamException("Failure to read " + stagingDirectory + File.separator + cvrUtilities.GENE_PANEL);
            }
            geneSymbols = cvrUtilities.getGeneSymbols();
            for(String gene : geneSymbols){
                genes.add(gene);
            }
        }
        
        // CNA data is processed on a gene per row basis, making it very different from the other data types.
        // This also means we can't exactly model it with a java class easily. For now, process CNA data as strings.
        processExistingCnaFile();
        makeNewRecordsList();
        makeRecordsList();
        
    }

    @Override
    public void update(ExecutionContext ec) throws ItemStreamException {}

    @Override
    public void close() throws ItemStreamException {}

    @Override
    public CompositeCnaRecord read() throws Exception {
        if (!cnaRecords.isEmpty()) {
            return cnaRecords.remove(0);
        }
        return null;
    }   
    
    private String resolveGeneFoldChange(Double geneFoldChange) {
        if (geneFoldChange > 0) {
            return "2";
        }
        if (geneFoldChange < 0) {
            return "-2";
        }
        return "0";
    }
    
    private void makeRecordsList() {
        cnaRecords.add(new CompositeCnaRecord("",cvrUtilities.CNA_HEADER_HUGO_SYMBOL + "\t" + StringUtils.join(samples,"\t")));
        for (String gene : genes) {
            String line = gene;
            for (String sample : samples) {
                String cnaValue = "0";
                Object value = cnaMap.get(gene, sample);
                if (value != null) {
                    cnaValue = value.toString();
                }
                line = line + "\t" + cnaValue;
            }
            cnaRecords.add(new CompositeCnaRecord("", line));
        }
    }
    
    private void makeNewRecordsList() {
        cnaRecords.add(new CompositeCnaRecord(cvrUtilities.CNA_HEADER_HUGO_SYMBOL + "\t" + StringUtils.join(newSamples,"\t"), ""));
        for (String gene : genes) {
            String line = gene;
            for (String sample : newSamples) {
                String cnaValue = "0";
                Object value = cnaMap.get(gene, sample);
                if (value != null) {
                    cnaValue = value.toString();
                }
                line = line + "\t" + cnaValue;
            }
            cnaRecords.add(new CompositeCnaRecord(line, ""));
        }
    }

    private void processExistingCnaFile() {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(stagingDirectory + "/" + cvrUtilities.CNA_FILE));
            List<String> header = Arrays.asList(reader.readLine().split("\t"));
            String line;
            while((line = reader.readLine()) != null) {
                List<String> data = Arrays.asList(line.split("\t"));
                for(int i = 1; i < header.size(); i++) {
                    if (!cvrUtilities.getNewIds().contains(header.get(i))) {
                        samples.add(header.get(i));
                        genes.add(data.get(0));
                        cnaMap.put(data.get(0), header.get(i), data.get(i));
                    }
                }
            }
        }
        catch (Exception e) {
            log.info("CNA file does not yet exist");
        }
    }
}
