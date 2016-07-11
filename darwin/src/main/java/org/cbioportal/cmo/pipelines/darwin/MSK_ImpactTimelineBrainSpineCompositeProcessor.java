/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cbioportal.cmo.pipelines.darwin;

import org.cbioportal.cmo.pipelines.darwin.model.MSK_ImpactTimelineBrainSpine;
import org.cbioportal.cmo.pipelines.darwin.model.TimelineBrainSpineComposite;

import java.util.*;
import org.apache.commons.lang.StringUtils;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.support.CompositeItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;
/**
 *
 * @author jake
 */
public class MSK_ImpactTimelineBrainSpineCompositeProcessor extends CompositeItemProcessor<MSK_ImpactTimelineBrainSpine, String>{
    List<ItemProcessor> delegates = new ArrayList<>();
    List<String> record = new ArrayList<>();
    
    @Autowired
    private MSK_ImpactTimelineBrainSpineStatusProcessor statusProcessor;
    
    @Autowired
    private MSK_ImpactTimelineBrainSpineSpecimenProcessor specimenProcessor;
    
    @Autowired
    private MSK_ImpactTimelineBrainSpineTreatmentProcessor treatmentProcessor;
    
    @Autowired
    MSK_ImpactTimelineBrainSpineImagingProcessor imagingProcessor;
    
    @Autowired
    MSK_ImpactTimelineBrainSpineSurgeryProcessor surgeryProcessor;
    
    CompositeItemProcessor compProcessor = new CompositeItemProcessor();
    
    @Override
    public String process(MSK_ImpactTimelineBrainSpine darwinTimelineBrainSpine) throws Exception{
        delegates.clear();
        delegates.add(statusProcessor);
        delegates.add(specimenProcessor);
        delegates.add(treatmentProcessor);
        delegates.add(imagingProcessor);
        delegates.add(surgeryProcessor);
        TimelineBrainSpineComposite composite = new TimelineBrainSpineComposite(darwinTimelineBrainSpine);
        compProcessor.setDelegates(delegates);
        compProcessor.process(composite);
        return StringUtils.join(composite.getJointRecord(), "\n");
    }
}
