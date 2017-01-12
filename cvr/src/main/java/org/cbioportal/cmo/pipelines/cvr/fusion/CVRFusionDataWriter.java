/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cbioportal.cmo.pipelines.cvr.fusion;

import java.io.IOException;
import java.io.Writer;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang.StringUtils;
import org.cbioportal.cmo.pipelines.cvr.CVRUtilities;
import org.cbioportal.cmo.pipelines.cvr.model.CVRFusionRecord;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemStreamException;
import org.springframework.batch.item.ItemStreamWriter;
import org.springframework.batch.item.file.FlatFileHeaderCallback;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.transform.PassThroughLineAggregator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;


/**
 *
 * @author heinsz
 */
public class CVRFusionDataWriter implements ItemStreamWriter<String> {
    @Value("#{jobParameters[stagingDirectory]}")
    private String stagingDirectory;
    
    private String stagingFile;
    
    @Autowired
    public CVRUtilities cvrUtilities;
    
    private FlatFileItemWriter<String> flatFileItemWriter = new FlatFileItemWriter<>();
    
    @Override
    public void open(ExecutionContext ec) throws ItemStreamException{
        stagingFile = Paths.get(stagingDirectory).resolve(cvrUtilities.FUSION_FILE).toString();
        
        PassThroughLineAggregator aggr = new PassThroughLineAggregator();
        flatFileItemWriter.setLineAggregator(aggr);
        flatFileItemWriter.setHeaderCallback(new FlatFileHeaderCallback(){
           @Override
           public void writeHeader(Writer writer) throws IOException{
               writer.write(StringUtils.join(CVRFusionRecord.getFieldNames(),"\t"));
           }
        });
        flatFileItemWriter.setResource( new FileSystemResource(stagingFile));
        flatFileItemWriter.open(ec);
    }
    
    @Override
    public void update(ExecutionContext ec) throws ItemStreamException{}
    
    @Override
    public void close() throws ItemStreamException{
        flatFileItemWriter.close();
    }
    
    @Override
    public void write(List<? extends String> items) throws Exception {
        List<String> writeList = new ArrayList<>();
        for(String item : items){
            writeList.add(item);
        }
        flatFileItemWriter.write(writeList);
    }
}
