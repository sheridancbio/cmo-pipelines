/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cbioportal.cmo.pipelines.cvr.variants;

import java.io.File;
import java.util.List;
import org.cbioportal.cmo.pipelines.cvr.CVRUtilities;
import org.springframework.core.io.*;
import org.springframework.batch.item.*;
import org.springframework.batch.item.file.*;
import org.springframework.batch.item.file.transform.PassThroughLineAggregator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
/**
 *
 * @author jake
 */
public class GMLVariantsWriter implements ItemStreamWriter<String>{
    
    @Value("#{jobParameters[stagingDirectory]}")
    private String stagingDirectory;
    
    @Autowired
    public CVRUtilities cvrUtilities;
    
    private String stagingFile;
    private final String seperator = File.separator;
    private FlatFileItemWriter<String> flatFileItemWriter = new FlatFileItemWriter<>();
    
    @Override
    public void open(ExecutionContext ec) throws ItemStreamException{
        
        stagingFile = stagingDirectory + seperator + cvrUtilities.GML_FILE;
        
        PassThroughLineAggregator aggr = new PassThroughLineAggregator();
        flatFileItemWriter.setLineAggregator(aggr);
        flatFileItemWriter.setResource(new FileSystemResource(stagingFile));
        flatFileItemWriter.open(ec);
    }
    
    @Override
    public void update(ExecutionContext ec) throws ItemStreamException{}
    
    @Override
    public void close() throws ItemStreamException{
        flatFileItemWriter.close();
    }
    
    @Override
    public void write(List<? extends String> items) throws Exception{
        flatFileItemWriter.write(items);
    }
    
}
