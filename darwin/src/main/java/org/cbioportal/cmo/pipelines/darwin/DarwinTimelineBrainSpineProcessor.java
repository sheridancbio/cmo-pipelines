/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cbioportal.cmo.pipelines.darwin;

import org.cbioportal.cmo.pipelines.darwin.model.DarwinTimelineBrainSpine;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.*;
import org.apache.commons.lang.StringUtils;
import org.springframework.batch.item.ItemProcessor;
/**
 *
 * @author jake
 */
public class DarwinTimelineBrainSpineProcessor implements ItemProcessor<DarwinTimelineBrainSpine, String>{
    ObjectMapper mapper = new ObjectMapper();
    
    @Override
    public String process(final DarwinTimelineBrainSpine darwinTimelineBrainSpine) throws Exception{
        List<String> record = new ArrayList<>();
        for(String field : new DarwinTimelineBrainSpine().getFieldNames()){
            record.add(darwinTimelineBrainSpine.getClass().getMethod("get"+field).invoke(darwinTimelineBrainSpine, null).toString());
        }
        
        return StringUtils.join(record, "\t");
    }
}
