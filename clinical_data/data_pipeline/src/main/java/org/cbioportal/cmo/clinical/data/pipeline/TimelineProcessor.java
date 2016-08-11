/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cbioportal.cmo.clinical.data.pipeline;

import java.util.List;
import java.util.Map;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

/**
 *
 * @author heinsz
 */
public class TimelineProcessor implements ItemProcessor<Map<String, String>, String> {
    
    @Value("#{stepExecutionContext['combinedHeader']}")
    Map<String, List<String>> total_header;
    
    @Override
    public String process(Map<String, String> i) throws Exception {
        String to_write = "";      
        List<String> header = total_header.get("header");
        
        if(header.contains("SAMPLE_ID")) {
            to_write= i.get("PATIENT_ID") + "\t";    
        }
        else {
                to_write= i.get("SAMPLE_ID") + "\t" + i.get("PATIENT_ID") + "\t";    
        }
        
        // get the sample and patient ids first before processing the other columns
                   
        
        for(String column : header) {
            if(!column.equals("PATIENT_ID") && !column.equals("SAMPLE_ID")) {
                to_write += i.get(column) + "\t";
            }
        }        
        return to_write;
    }
}
