/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cbioportal.cmo.pipelines.cvr.clinical;

import org.apache.log4j.Logger;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;

/**
 *
 * @author heinsz
 */
public class GMLClinicalStepListener implements StepExecutionListener {

    Logger log = Logger.getLogger(GMLClinicalStepListener.class);
    
    @Override
    public void beforeStep(StepExecution se) {}

    @Override
    public ExitStatus afterStep(StepExecution se) {
        se.getJobExecution().getExecutionContext().put("patientSampleMap", se.getExecutionContext().get("patientSampleMap"));
        return ExitStatus.COMPLETED;
    }
    
}
