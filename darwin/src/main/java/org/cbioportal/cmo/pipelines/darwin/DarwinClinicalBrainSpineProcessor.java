/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cbioportal.cmo.pipelines.darwin;

import org.cbioportal.cmo.pipelines.darwin.model.DarwinClinicalBrainSpine;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.*;
import org.apache.commons.lang.StringUtils;
import org.springframework.batch.item.ItemProcessor;
/**
 *
 * @author jake
 */
public class DarwinClinicalBrainSpineProcessor implements ItemProcessor<DarwinClinicalBrainSpine, String>{
    ObjectMapper mapper = new ObjectMapper();
    
    @Override
    public String process(final DarwinClinicalBrainSpine darwinClinicalBrainSpine) throws Exception{
        List<String> record = new ArrayList<>();
        for(String field : new DarwinClinicalBrainSpine().getFieldNames()){
            record.add(darwinClinicalBrainSpine.getClass().getMethod("get"+field).invoke(darwinClinicalBrainSpine, null).toString());
        }
        
        return StringUtils.join(record, "\t");
    }
    
}
