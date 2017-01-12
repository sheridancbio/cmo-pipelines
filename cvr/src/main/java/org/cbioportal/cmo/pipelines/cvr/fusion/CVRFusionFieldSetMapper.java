/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cbioportal.cmo.pipelines.cvr.fusion;

import java.util.List;
import org.apache.log4j.Logger;
import org.cbioportal.cmo.pipelines.cvr.model.CVRFusionRecord;
import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.transform.FieldSet;
import org.springframework.validation.BindException;

/**
 *
 * @author heinsz
 */
public class CVRFusionFieldSetMapper implements  FieldSetMapper<CVRFusionRecord> {
    
    Logger log = Logger.getLogger(CVRFusionFieldSetMapper.class);
 
    @Override
    public CVRFusionRecord mapFieldSet(FieldSet fs) throws BindException {
        CVRFusionRecord record = new CVRFusionRecord();
        List<String> fields = CVRFusionRecord.getFieldNames();
        
        for (int i = 0; i < fields.size(); i++)
        {
            String field = fields.get(i);
            try {
                record.getClass().getMethod("set" + field, String.class).invoke(record, fs.readString(i));
            }
            catch(Exception e) {
                if (e.getClass().equals(NoSuchMethodException.class)) {
                    String message = "No set method exists for " + field;
                    log.info(message);
                }
            }
        }        
        return record;
    }    
}
