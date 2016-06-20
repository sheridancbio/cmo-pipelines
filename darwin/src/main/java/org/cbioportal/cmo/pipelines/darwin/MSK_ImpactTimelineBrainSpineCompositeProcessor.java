/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cbioportal.cmo.pipelines.darwin;

import org.cbioportal.cmo.pipelines.darwin.model.MSK_ImpactTimelineBrainSpine;
import org.cbioportal.cmo.pipelines.darwin.model.TimelineBrainSpineComposite;

import java.util.*;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.support.CompositeItemProcessor;
/**
 *
 * @author jake
 */
public class MSK_ImpactTimelineBrainSpineCompositeProcessor implements ItemProcessor<MSK_ImpactTimelineBrainSpine, TimelineBrainSpineComposite>{
    List<ItemProcessor> delegates = new ArrayList<>();
    List<String> record = new ArrayList<>();
    
    MSK_ImpactTimelineBrainSpineStatusProcessor processor1 = new MSK_ImpactTimelineBrainSpineStatusProcessor();
    //private MSK_ImpactTimelineBrainSpineSpecimenProcessor processor2 = new MSK_ImpactTimelineBrainSpineSpecimenProcessor();
        
    @Override
    public TimelineBrainSpineComposite process(MSK_ImpactTimelineBrainSpine darwinTimelineBrainSpine) throws Exception{
        MSK_ImpactTimelineBrainSpineSpecimenProcessor processor2 = new MSK_ImpactTimelineBrainSpineSpecimenProcessor();
        delegates.add(processor1);
        delegates.add(processor2);
        TimelineBrainSpineComposite composite = new TimelineBrainSpineComposite(darwinTimelineBrainSpine);
        CompositeItemProcessor compProcessor = new CompositeItemProcessor();
        compProcessor.setDelegates(delegates);
        compProcessor.process(darwinTimelineBrainSpine);
        return composite;
    }
    
    
    
    /*
    @Override
    public String process(MSK_ImpactTimelineBrainSpine msk_timelineBrainSpine) throws Exception{
        List record = new ArrayList();
        MSK_ImpactTimelineBrainSpineProcessor processor1 = new MSK_ImpactTimelineBrainSpineProcessor();
        processor1.process(msk_timelineBrainSpine);
        
        return StringUtils.join(record, "\t");
    }
    */
    
    
}
