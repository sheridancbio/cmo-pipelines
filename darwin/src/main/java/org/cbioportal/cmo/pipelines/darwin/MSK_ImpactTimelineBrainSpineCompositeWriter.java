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

import java.util.*;
import org.springframework.batch.item.support.CompositeItemWriter;
/**
 *
 * @author jake
 */
public class MSK_ImpactTimelineBrainSpineCompositeWriter implements ItemStreamWriter<String>{
    
    List<ItemStreamWriter> delegates = new ArrayList<>();
    ItemStreamWriter writer1 = new MSK_ImpactTimelineBrainSpineStatusWriter();
    ItemStreamWriter writer2 = new MSK_ImpactTimelineBrainSpineSpecimenWriter();
    
    @Override
    public void close() throws ItemStreamException{}
        
    @Override
    public void open(ExecutionContext executionContext) throws ItemStreamException{}
    
    @Override
    public void update(ExecutionContext executionContext) throws ItemStreamException{}
    
    @Override
    public void write(List<? extends String> items) throws Exception{
        CompositeItemWriter compWriter = new CompositeItemWriter();
        delegates.add(writer1);
        delegates.add(writer2);
        compWriter.setDelegates(delegates);
        compWriter.write(items);
        compWriter.close();
    }
    
}
