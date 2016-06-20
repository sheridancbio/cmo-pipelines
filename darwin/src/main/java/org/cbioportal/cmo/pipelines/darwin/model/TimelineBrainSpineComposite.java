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
    
    private String result1;
    private String result2;
    public MSK_ImpactTimelineBrainSpine record;
    
    public TimelineBrainSpineComposite(MSK_ImpactTimelineBrainSpine record){
        this.result1 = null;
        this.result2 = null;
        this.record = record;
    }
    
    public String getStatusResult(){
        return result1;
    }
    public void setResult1(String s){
        this.result1 = s;
    }
    
    public String getSpecimenResult(){
        return result2;
    }
    public void setResult2(String s){
        this.result2 = s;
    }
    
    
    
    
}
