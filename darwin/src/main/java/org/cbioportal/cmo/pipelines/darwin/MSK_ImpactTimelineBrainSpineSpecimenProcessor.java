/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cbioportal.cmo.pipelines.darwin;

import org.cbioportal.cmo.pipelines.darwin.model.MSK_ImpactTimelineBrainSpine;
import org.cbioportal.cmo.pipelines.darwin.model.TimelineBrainSpineComposite;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.*;
import org.apache.commons.lang.StringUtils;
import org.springframework.batch.item.ItemProcessor;
/**
 *
 * @author jake
 */
public class MSK_ImpactTimelineBrainSpineSpecimenProcessor implements ItemProcessor<TimelineBrainSpineComposite, TimelineBrainSpineComposite>{
    ObjectMapper mapper = new ObjectMapper();
    
    @Override
    public TimelineBrainSpineComposite process(final TimelineBrainSpineComposite composite) throws Exception{
        List<String> recordPost = new ArrayList<>();
        for(String field : new MSK_ImpactTimelineBrainSpine().getSpecimenFields()){
            recordPost.add(composite.record.getClass().getMethod("get" + field).invoke(composite.record).toString());
        }
        composite.setResult2(StringUtils.join(recordPost, "\t"));
        
        return composite;
    }
}
