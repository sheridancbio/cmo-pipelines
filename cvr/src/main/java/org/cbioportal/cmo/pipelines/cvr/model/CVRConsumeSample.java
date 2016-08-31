/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cbioportal.cmo.pipelines.cvr.model;


/**
 *
 * @author jake
 */
public class CVRConsumeSample {
    private Integer affectedRows;
    private String dmp_sample_id;
    private String aly2sample_id;
    private String disclaimer;
    
    public CVRConsumeSample(){}
    
    public CVRConsumeSample(Integer affectedRows, String dmp_sample_id, String aly2sample_id, String disclaimer){
        this.affectedRows = affectedRows;
        this.dmp_sample_id = dmp_sample_id;
        this.aly2sample_id = aly2sample_id;
        this.disclaimer = disclaimer;
    }
    
    public Integer getaffectedRows(){
        return affectedRows;
    }
    
    public void setaffectedRows(Integer affectedRows){
        this.affectedRows = affectedRows;
    }
    
    public String getdmp_sample_id(){
        return dmp_sample_id;
    }
    
    public void setdmp_sample_id(String dmp_sample_id){
        this.dmp_sample_id = dmp_sample_id;
    }
    
    public String getaly2sample_id(){
        return aly2sample_id;
    }
    
    public void setaly2sample_id(String aly2sample_id){
        this.aly2sample_id = aly2sample_id;
    }
    
    public String getdisclaimer(){
        return disclaimer;
    }
    
    public void setdisclaimer(String disclaimer){
        this.disclaimer = disclaimer;
    }
    
}




