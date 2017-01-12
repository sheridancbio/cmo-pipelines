/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cbioportal.cmo.pipelines.cvr.seg;

/**
 *
 * @author jake
 */

import java.util.List;
import org.cbioportal.cmo.pipelines.cvr.model.CVRSegRecord;
import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.transform.FieldSet;
import org.springframework.validation.BindException;
import org.apache.log4j.Logger;

public class CVRSegFieldSetMapper implements FieldSetMapper<CVRSegRecord>{
    Logger log = Logger.getLogger(CVRSegFieldSetMapper.class);
    
    @Override
    public CVRSegRecord mapFieldSet(FieldSet fs) throws BindException {
        CVRSegRecord record = new CVRSegRecord();
        List<String> fields = CVRSegRecord.getFieldNames();
        
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
