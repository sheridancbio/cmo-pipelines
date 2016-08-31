/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cbioportal.cmo.pipelines.cvr.sv;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.*;
import org.cbioportal.cmo.pipelines.cvr.model.CVRSvRecord;
import org.cbioportal.cmo.pipelines.cvr.model.CVRData;
import org.cbioportal.cmo.pipelines.cvr.model.CVRMergedResult;
import org.cbioportal.cmo.pipelines.cvr.model.CVRSvVariant;
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
/**
 *
 * @author heinsz
 */
public class CVRSvDataReader implements ItemStreamReader<CVRSvRecord>{
    @Value("#{jobParameters[stagingDirectory]}")
    private String stagingDirectory;
    
    @Autowired
    public CVRUtilities cvrUtilities;
    
    private CVRData cvrData;
    private List<CVRSvRecord> svRecords = new ArrayList<>();

    Logger log = Logger.getLogger(CVRSvDataReader.class);
    
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
        try{//Catches exception when file does not exist (e.g. first run, file not yet written)
            FlatFileItemReader<CVRSvRecord> reader = new FlatFileItemReader<>();
            reader.setResource(new FileSystemResource(stagingDirectory + "/" + cvrUtilities.SV_FILE));
            DefaultLineMapper<CVRSvRecord> mapper = new DefaultLineMapper<>();
            DelimitedLineTokenizer tokenizer = new DelimitedLineTokenizer();
            tokenizer.setDelimiter("\t");
            mapper.setLineTokenizer(tokenizer);
            mapper.setFieldSetMapper(new CVRSvFieldSetMapper());
            reader.setLineMapper(mapper);
            reader.setLinesToSkip(1);
            reader.open(ec);
        
            CVRSvRecord to_add;
            try {
                while((to_add = reader.read()) != null) {
                    if (!cvrUtilities.getNewIds().contains(to_add.getSampleId()) && to_add.getSampleId()!= null) {
                        svRecords.add(to_add);
                    }
                }
            }
            catch(Exception e) {
                throw new ItemStreamException(e);
            }
            reader.close();
        }
        catch(Exception a){
            String message = "File " + cvrUtilities.SV_FILE + " does not exist";
            log.info(message);
        }
                       
        for (CVRMergedResult result : cvrData.getResults()) {
            String sampleId = result.getMetaData().getDmpSampleId();
            List<CVRSvVariant> variants = result.getSvVariants();
            for (CVRSvVariant variant : variants) {
                    CVRSvRecord record = new CVRSvRecord(variant, sampleId);
                    record.setIsNew(cvrUtilities.IS_NEW);
                    svRecords.add(record);             
            }
        }
    }

    @Override
    public void update(ExecutionContext ec) throws ItemStreamException {}

    @Override
    public void close() throws ItemStreamException {}

    @Override
    public CVRSvRecord read() throws Exception {
        if (!svRecords.isEmpty()) {            
            return svRecords.remove(0);         
        }
        return null;
    }   
}
