/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cbioportal.cmo.pipelines.cvr.fusion;

import org.cbioportal.cmo.pipelines.cvr.model.CVRFusionRecord;
import org.springframework.batch.item.ItemProcessor;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang.StringUtils;

/**
 *
 * @author heinsz
 */
public class CVRFusionDataProcessor implements ItemProcessor<CVRFusionRecord, String>{
    @Override
    public String process(CVRFusionRecord i) throws Exception {      
        List<String> record = new ArrayList<String>();
        for(String field : i.getFieldNames()) {
            record.add(i.getClass().getMethod("get" + field).invoke(i, null).toString().replace("\r\n", " ").replace("\r", " ").replace("\n", " ").replace("\t", " "));
        }        
        return StringUtils.join(record, "\t");
    }           
}
