/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cbioportal.cmo.pipelines.cvr.linkedimpactcase;

import java.util.*;
import org.apache.commons.lang.StringUtils;
import org.cbioportal.cmo.pipelines.cvr.model.*;
import org.springframework.batch.item.ItemProcessor;

/**
 *
 * @author heinsz
 */
public class LinkedMskimpactCaseProcessor implements ItemProcessor<LinkedMskimpactCaseRecord, String> {

    @Override
    public String process(LinkedMskimpactCaseRecord i) throws Exception {
        List<String> record = new ArrayList<>();
        for (String field : LinkedMskimpactCaseRecord.getFieldNames()) {
            String value = i.getClass().getMethod("get" + field).invoke(i).toString().replaceAll("[\\t\\n\\r]+"," ");
            if (value.equals("NA") || value.isEmpty()) {
                return null;
            }
            record.add(value);
        }
        return StringUtils.join(record, "\t");
    }        
}
