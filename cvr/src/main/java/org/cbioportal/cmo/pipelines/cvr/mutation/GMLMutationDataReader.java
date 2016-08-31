/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cbioportal.cmo.pipelines.cvr.mutation;

import org.cbioportal.cmo.pipelines.cvr.mutation.CVRMutationFieldSetMapper;
import java.io.BufferedReader;
import org.cbioportal.models.*;
import org.cbioportal.annotator.*;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Paths;
import java.nio.file.Path;
import java.util.*;
import org.cbioportal.cmo.pipelines.cvr.model.GMLData;
import org.cbioportal.cmo.pipelines.cvr.model.GMLResult;
import org.cbioportal.cmo.pipelines.cvr.model.*;

import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemStreamException;
import org.springframework.batch.item.ItemStreamReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.core.io.FileSystemResource;
import org.apache.log4j.Logger;
import org.cbioportal.cmo.pipelines.cvr.CVRUtilities;
import org.springframework.batch.item.file.LineCallbackHandler;
/**
 *
 * @author jake
 */
public class GMLMutationDataReader implements ItemStreamReader<AnnotatedRecord>{
    @Value("#{jobParameters[stagingDirectory]}")
    private String stagingDirectory;
    
    @Value("#{jobExecutionContext['patientSampleMap']}")
    private Map<String, List<String>> patientSampleMap;
    
    @Autowired
    public CVRUtilities cvrUtilities;

    @Autowired
    private Annotator annotator;
    
    private GMLData gmlData;
    private List<AnnotatedRecord> mutationRecords = new ArrayList<>();
    private Map<String, List<AnnotatedRecord>> mutationMap = new HashMap<>();        
    private Path filename;
    private Set<String> header = new LinkedHashSet<>();
    private Set<String> additionalPropertyKeys = new LinkedHashSet<>();
    
    Logger log = Logger.getLogger(GMLMutationDataReader.class);
    
    @Override
    public void open(ExecutionContext ec) throws ItemStreamException{
        try{
            if(ec.get("gmlData") == null) {
                gmlData = cvrUtilities.readGMLJson(Paths.get(stagingDirectory).resolve(cvrUtilities.GML_FILE).toString());
            }
            else {
                gmlData = (GMLData)ec.get("gmlData");
            }
        }
        catch(IOException e)
        {
            throw new ItemStreamException("Failure to read " + stagingDirectory + File.separator + cvrUtilities.GML_FILE);
        }

        for(GMLResult result : gmlData.getResults()){
            String patientId = result.getMetaData().getDmpPatientId();
            List<String> samples = patientSampleMap.get(patientId);            
            List<GMLSnp> snps = result.getSnpIndelGml();
            if (samples != null && snps != null) {
                for(GMLSnp snp : snps){
                    for (String sampleId : samples) {
                        MutationRecord record = cvrUtilities.buildGMLMutationRecord(snp, sampleId);
                        AnnotatedRecord annotatedRecord = annotator.annotateRecord(record, false, "mskcc", false);
                        mutationRecords.add(annotatedRecord);
                        header.addAll(record.getHeaderWithAdditionalFields());
                        additionalPropertyKeys.addAll(record.getAdditionalProperties().keySet());
                        addRecordToMap(annotatedRecord);                
                    }             
                }            
            }
        }
        filename = Paths.get(stagingDirectory).resolve(cvrUtilities.MUTATION_FILE);
        if (new File(filename.toString()).exists()) {
            FlatFileItemReader<MutationRecord> reader = new FlatFileItemReader<>();
            reader.setResource(new FileSystemResource(filename.toString()));
            processComments(ec);
            DefaultLineMapper<MutationRecord> mapper = new DefaultLineMapper<>();
            final DelimitedLineTokenizer tokenizer = new DelimitedLineTokenizer();
            tokenizer.setDelimiter("\t");
            mapper.setLineTokenizer(tokenizer);
            mapper.setFieldSetMapper(new CVRMutationFieldSetMapper());
            reader.setLineMapper(mapper);
            reader.setLinesToSkip(1);
            reader.setSkippedLinesCallback(new LineCallbackHandler() {
                @Override
                public void handleLine(String line) {
                    tokenizer.setNames(line.split("\t"));
                }
            });
            reader.open(ec);
            
            MutationRecord to_add;
            try{
                while((to_add = reader.read()) != null && to_add.getTumor_Sample_Barcode() != null) {
                    AnnotatedRecord to_add_annotated = annotator.annotateRecord(to_add, false, "mskcc", false);
                    if(!cvrUtilities.getNewIds().contains(to_add.getTumor_Sample_Barcode()) && !isDuplicate(to_add_annotated)){
                        mutationRecords.add(to_add_annotated);
                        header.addAll(to_add_annotated.getHeaderWithAdditionalFields());
                        cvrUtilities.addAllIds(to_add.getTumor_Sample_Barcode());
                        additionalPropertyKeys.addAll(to_add_annotated.getAdditionalProperties().keySet());
                        addRecordToMap(to_add_annotated);                        
                    }
                }
            }
            catch(Exception e){
                throw new ItemStreamException(e);
            }
            reader.close();        
        }
        List<String> full_header = new ArrayList(header);        
        ec.put("mutation_header", full_header);        
    }
    
    @Override
    public void update(ExecutionContext ec) throws ItemStreamException{}
    
    @Override
    public void close() throws ItemStreamException{}
    
    @Override
    public AnnotatedRecord read() throws Exception{
        if(!mutationRecords.isEmpty()){
            AnnotatedRecord annotatedRecord = mutationRecords.remove(0);
            for (String additionalProperty : additionalPropertyKeys) {
                Map<String, String> additionalProperties = annotatedRecord.getAdditionalProperties();
                if(!additionalProperties.keySet().contains(additionalProperty)) {
                    additionalProperties.put(additionalProperty, "");
                }
            }
            return annotatedRecord;
        }
        return null;
    }
    
    private boolean isDuplicate(MutationRecord snp) {
        String sampleId = snp.getTumor_Sample_Barcode();
        if (mutationMap.containsKey(sampleId)) {
            String chrom = snp.getChromosome();
            String start = snp.getStart_Position();
            String end = snp.getEnd_Position();
            String ref = snp.getReference_Allele();
            String alt = snp.getTumor_Seq_Allele2();
            String gene = snp.getHugo_Symbol();
            List<AnnotatedRecord> records = mutationMap.get(sampleId);
            for (AnnotatedRecord record: records) {
                if(chrom.equals(record.getChromosome()) &&
                        start.equals(record.getStart_Position()) &&
                        end.equals(record.getStart_Position()) &&
                        ref.equals(record.getReference_Allele()) &&
                        alt.equals(record.getTumor_Seq_Allele2()) &&
                        gene.equals(record.getHugo_Symbol())) {
                    return true;
                }            
            }
        }
        return false;
    }
    
    
    private void processComments(ExecutionContext ec) {
        List<String> comments = new ArrayList<>();
        BufferedReader reader = null;
        try {            
            reader = new BufferedReader(new FileReader(filename.toString()));
            String line;
            while((line = reader.readLine()) != null) {     
                if (line.startsWith("#")) {
                    comments.add(line);
                    if (line.startsWith("#sequenced_samples")) {
                        for (String sample : line.split(":")[1].split(" ")) {
                            if (!sample.trim().equals("")) {
                                cvrUtilities.addAllIds(sample);
                            }
                        }
                    }
                }
                else {
                    // no more comments, go on processing
                    break;
                }
            }            
            reader.close();
        }        
        catch (Exception e) {
            throw new ItemStreamException(e);
        }
        
        // Add comments to the config for the writer to access later
        ec.put("commentLines", comments);
    }

    private void addRecordToMap(AnnotatedRecord record) { 
        String sampleId = record.getTumor_Sample_Barcode();
        List<AnnotatedRecord> recordList = mutationMap.get(sampleId);
        if (recordList == null) {
            recordList = new ArrayList<>();
            recordList.add(record);
            mutationMap.put(sampleId, recordList);
        }
        else {
            recordList.add(record);
        }
    }           
}
