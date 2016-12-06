/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.mskcc.cmo.ks.redcap.pipeline;

import java.io.IOException;
import java.io.Writer;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang.StringUtils;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemStreamException;
import org.springframework.batch.item.ItemStreamWriter;
import org.springframework.batch.item.file.FlatFileHeaderCallback;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.transform.PassThroughLineAggregator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;

/**
 *
 * @author heinsz
 */
public class TimelineWriter  implements ItemStreamWriter<String> {    
    @Value("#{jobParameters[directory]}")
    private String directory;

    @Value("#{jobParameters[redcap_project]}")
    private String project;    
    
    private String outputFilename = "data_timeline_";
    
    private Path stagingFile;
    
    List<String> combinedHeader;
    
    private FlatFileItemWriter<String> flatFileItemWriter = new FlatFileItemWriter<String>();   
    
    @Override
    public void open(ExecutionContext ec) throws ItemStreamException {
        stagingFile = Paths.get(directory).resolve(outputFilename + (String)ec.get("studyId") + ".txt");        
        combinedHeader = (List<String>) ec.get("combinedHeader");
        
        PassThroughLineAggregator aggr = new PassThroughLineAggregator();
        flatFileItemWriter.setLineAggregator(aggr);
        flatFileItemWriter.setResource( new FileSystemResource(stagingFile.toString()));        
        flatFileItemWriter.setHeaderCallback(new FlatFileHeaderCallback() {
            @Override
            public void writeHeader(Writer writer) throws IOException {                
                writer.write(getHeaderLine(combinedHeader));
            }                
        });  
        flatFileItemWriter.open(ec);        
    }
    
    @Override
    public void update(ExecutionContext ec) throws ItemStreamException {}

    @Override
    public void close() throws ItemStreamException {
        flatFileItemWriter.close();
    }

    @Override
    public void write(List<? extends String> items) throws Exception {
         flatFileItemWriter.write(items);
    }
    
    private String getHeaderLine(List<String> metaData) {
        String to_return = "";
        Integer sidIndex = metaData.indexOf("SAMPLE_ID");
        Integer pidIndex = metaData.indexOf("PATIENT_ID");
        if (sidIndex >= 0 && pidIndex >= 0) {
            to_return += metaData.get(sidIndex) + "\t" + metaData.get(pidIndex);
        }
        else if (pidIndex >= 0) {
            to_return += metaData.get(pidIndex);
        }
        for (String column : metaData) {
            if (!column.equals("SAMPLE_ID") && !column.equals("PATIENT_ID") && !column.equals("RECORD_ID")) {
                to_return += "\t" + column;
            }
        }
        return to_return;
    }       
    
}