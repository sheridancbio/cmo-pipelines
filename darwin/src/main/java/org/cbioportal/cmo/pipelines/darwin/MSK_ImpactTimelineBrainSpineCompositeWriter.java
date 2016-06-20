/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cbioportal.cmo.pipelines.darwin;

import org.cbioportal.cmo.pipelines.darwin.model.TimelineBrainSpineComposite;
//import org.cbioportal.cmo.pipelines.darwin.model.MSK_ImpactTimelineBrainSpine;
//import org.cbioportal.cmo.pipelines.darwin.MSK_ImpactTimelineBrainSpineSpecimenWriter;

import org.springframework.batch.item.*;
import org.springframework.batch.item.file.*;
import org.springframework.core.io.*;
import org.springframework.beans.factory.annotation.Value;

import java.util.*;
import org.springframework.batch.item.support.CompositeItemWriter;
/**
 *
 * @author jake
 */
public class MSK_ImpactTimelineBrainSpineCompositeWriter implements ItemStreamWriter<String>{
    
    private final FlatFileItemWriter<String> flatFileItemWriter = new FlatFileItemWriter<>();
    
    @Value("#{jobParameters[stagingDirectory]}")
    private String stagingDirectory;
    
    @Value("${darwin.timeline_bs_status}")
    private String statusFilename;
    
    @Value("${darwin.timeline_bs_specimen}")
    private String specimenFilename;
    
    private String statusFile;
    private String specimenFile;
    
    List<ItemStreamWriter> delegates = new ArrayList<>();
    MSK_ImpactTimelineBrainSpineStatusWriter writer1 = new MSK_ImpactTimelineBrainSpineStatusWriter();
    MSK_ImpactTimelineBrainSpineSpecimenWriter writer2 = new MSK_ImpactTimelineBrainSpineSpecimenWriter();
    
    @Override
    public void close() throws ItemStreamException{}
        
    @Override
    public void open(ExecutionContext executionContext) throws ItemStreamException{
        if(stagingDirectory.endsWith("/")){
            statusFile = stagingDirectory + statusFilename;
        }
        else{
            statusFile = stagingDirectory + "/" + statusFilename;
        }
        if(stagingDirectory.endsWith("/")){
            specimenFile = stagingDirectory + specimenFilename;
        }
        else{
            specimenFile = stagingDirectory + "/" + specimenFilename;
        }
        
        try{
            writer1.getClass().getMethod("setStagingFile").invoke(writer1, statusFilename);
        }
        catch(Exception e){}
        
        try{
            writer2.getClass().getMethod("setStagingFile").invoke(writer1, specimenFilename);
        }
        catch(Exception e){}
        
        writer1.open(executionContext);
        writer2.open(executionContext);
    }
    
    @Override
    public void update(ExecutionContext executionContext) throws ItemStreamException{}
    
    @Override
    public void write(List<? extends String> items) throws Exception{
        CompositeItemWriter compWriter = new CompositeItemWriter();
        delegates.add(writer1);
        delegates.add(writer2);
        compWriter.setDelegates(delegates);
        compWriter.setIgnoreItemStream(true);
        compWriter.write(items);
        compWriter.close();
    }
    
}
