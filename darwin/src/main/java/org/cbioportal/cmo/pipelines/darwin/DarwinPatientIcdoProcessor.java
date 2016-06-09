/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cbioportal.cmo.pipelines.darwin;

import org.cbioportal.cmo.pipelines.darwin.model.DarwinPatientIcdoRecord;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.*;
import org.apache.commons.lang.StringUtils;
import org.springframework.batch.item.ItemProcessor;
/**
 *
 * @author jake
 */
public class DarwinPatientIcdoProcessor implements ItemProcessor<DarwinPatientIcdoRecord, String>{
    ObjectMapper mapper = new ObjectMapper();
    
    @Override
    public String process(final DarwinPatientIcdoRecord darwinPatientIcdoRecord) throws Exception{
        List<String> record = new ArrayList<>();
        for(String field : new DarwinPatientIcdoRecord().getFieldNames()){
            record.add(darwinPatientIcdoRecord.getClass().getMethod("get"+field).invoke(darwinPatientIcdoRecord, null).toString());
        }
        
        return StringUtils.join(record, "\t");
    }
}
