/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cbioportal.cmo.pipelines.cvr.clinical;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.*;
import org.cbioportal.cmo.pipelines.cvr.model.CVRClinicalRecord;
import org.cbioportal.cmo.pipelines.cvr.model.CVRData;
import org.cbioportal.cmo.pipelines.cvr.model.CVRMergedResult;
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
public class CVRClinicalDataReader implements ItemStreamReader<CVRClinicalRecord>{
    @Value("#{jobParameters[stagingDirectory]}")
    private String stagingDirectory;
    
    @Autowired
    public CVRUtilities cvrUtilities;
    
    private CVRData cvrData;
    private List<CVRClinicalRecord> clinicalRecords = new ArrayList<>();

    Logger log = Logger.getLogger(CVRClinicalDataReader.class);
    
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
            try {
                while((to_add = reader.read()) != null) {
                    if (!cvrUtilities.getNewIds().contains(to_add.getSAMPLE_ID()) && to_add.getSAMPLE_ID() != null) {
                        clinicalRecords.add(to_add);
                        cvrUtilities.addAllIds(to_add.getSAMPLE_ID());
                    }
                }
            }
            catch(Exception e) {
                throw new ItemStreamException(e);
            }
            reader.close();
            
        }
        catch(Exception b){
            String message = "File " + cvrUtilities.CLINICAL_FILE + " does not exist";
            log.info(message);
        }
                       
        for (CVRMergedResult result : cvrData.getResults()) {
            CVRClinicalRecord record = new CVRClinicalRecord(result.getMetaData());
            record.setIsNew(cvrUtilities.IS_NEW);            
            clinicalRecords.add(record);
        }
        
    }

    @Override
    public void update(ExecutionContext ec) throws ItemStreamException {}

    @Override
    public void close() throws ItemStreamException {}

    @Override
    public CVRClinicalRecord read() throws Exception {
        if (!clinicalRecords.isEmpty()) {            
            return clinicalRecords.remove(0);         
        }
        return null;
    }   
}
