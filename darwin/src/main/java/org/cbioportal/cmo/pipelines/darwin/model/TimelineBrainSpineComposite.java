/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cbioportal.cmo.pipelines.darwin.model;

//import org.cbioportal.cmo.pipelines.darwin.model.MSK_ImpactTimelineBrainSpine;

/**
 *
 * @author jake
 */

public class TimelineBrainSpineComposite {
    
    private String statusResult;
    private String specimenResult;
    private String treatmentResult;
    private String imagingResult;
    private MSK_ImpactTimelineBrainSpine record;
    
    public TimelineBrainSpineComposite(MSK_ImpactTimelineBrainSpine record){
        this.statusResult = "";
        this.specimenResult = "";
        this.treatmentResult = "";
        this.imagingResult = "";
        this.record = record;
    }
    
    public String getImagingResult(){
        return imagingResult;
    }
    public void setImagingResult(String s){
        this.imagingResult = s;
    }
    
    public String getStatusResult(){
        return statusResult;
    }
    public void setStatusResult(String s){
        this.statusResult = s;
    }
    
    public String getTreatmentResult(){
        return treatmentResult;
    }
    public void setTreatmentResult(String s){
        this.treatmentResult = s;
    }
    
    public String getSpecimenResult(){
        return specimenResult;
    }
    public void setSpecimenResult(String s){
        this.specimenResult = s;
    }
    
    public MSK_ImpactTimelineBrainSpine getRecord(){
        return this.record;
    }
    public void setRecord(MSK_ImpactTimelineBrainSpine record){
        this.record = record;
    }
    
    
    
    
}
