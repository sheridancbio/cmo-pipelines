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

import java.util.*;
import javax.annotation.Generated;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Generated("org.jsonschema2pojo")
@JsonPropertyOrder({
"alys2sample_id",
"dmp_alys_task_name",
"dmp_patient_id",
"dmp_sample_id",
"gender",
"gene-panel",
"legacy_patient_id",
"legacy_sample_id",
"retrieve_status",
"sample_coverage",
"so_comments",
"so_status_name",
})
public class GMLMetaData {
    @JsonProperty("alys2sample_id")
    private Integer alys2sampleId;
    @JsonProperty("dmp_alys_task_name")
    private String dmpAlysTaskName;
    @JsonProperty("dmp_patient_id")
    private String dmpPatientId;
    @JsonProperty("dmp_sample_id")
    private String dmpSampleId;
    @JsonProperty("gender")
    private Integer gender;
    @JsonProperty("gene-panel")
    private String genePanel;
    @JsonProperty("legacy_patient_id")
    private String legacyPatientId;
    @JsonProperty("legacy_sample_id")
    private String legacySampleId;
    @JsonProperty("retrieve_status")
    private Integer retrieveStatus;
    @JsonProperty("sample_coverage")
    private Integer sampleCoverage;
    @JsonProperty("so_comments")
    private String soComments;
    @JsonProperty("so_status_name")
    private String soStatusName;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<>();
    
    public GMLMetaData(){}
    
    public GMLMetaData(Integer alys2sampleId, String dmpAlysTaskName, String dmpPatientId,
            String dmpSampleId, Integer gender, String genePanel,
            String legacyPatientId, String legacySampleId, Integer retrieveStatus,
            Integer sampleCoverage, String soComments, String soStatusName){
        this.alys2sampleId = alys2sampleId;
        this.dmpAlysTaskName = dmpAlysTaskName;
        this.dmpPatientId = dmpPatientId;
        this.dmpSampleId = dmpSampleId;
        this.gender = gender;
        this.genePanel = genePanel;
        this.legacyPatientId = legacyPatientId;
        this.legacySampleId = legacySampleId;
        this.retrieveStatus = retrieveStatus;
        this.sampleCoverage = sampleCoverage;
        this.soComments = soComments;
        this.soStatusName = soStatusName;
    }
    
    @JsonProperty("alys2sample_id")
    public Integer getAlys2sampleId(){ 
        return alys2sampleId;
    }
    
    @JsonProperty("dmp_alys_task_name")
    public String getDmpAlysTaskName(){
        return dmpAlysTaskName;
    }
    
    @JsonProperty("dmp_patient_id")
    public String getDmpPatientId(){
        return dmpPatientId;
    }
    
    @JsonProperty("dmp_sample_id")
    public String getDmpSampleId(){ 
        return dmpSampleId;
    }
    
    @JsonProperty("gender")
    public Integer getGender(){ 
        return gender;
    }
    
    @JsonProperty("gene-panel")
    public String getGenePanel(){
        return genePanel;
    }
    
    @JsonProperty("legacy_patient_id")
    public String getLegacyPatientId(){ 
        return legacyPatientId;
    }
    
    @JsonProperty("legacy_sample_id")
    public String getLegacySampleId(){ 
        return legacySampleId;
    }
    
    @JsonProperty("retrieve_status")
    public Integer getRetrieveStatus(){ 
        return retrieveStatus;
    }
    
    @JsonProperty("sample_coverage")
    public Integer getSampleCoverage(){ 
        return sampleCoverage;
    }
    
    @JsonProperty("so_comments")
    public String getSoComments(){ 
        return soComments;
    }
    
    @JsonProperty("so_status_name")
    public String getSoStatusName(){ 
        return soStatusName;
    }
    
    @JsonProperty("alys2sample_id")
    public void setAlys2sampleId(Integer alys2sampleId){ 
        this.alys2sampleId = alys2sampleId;
    }
    
    @JsonProperty("dmp_alys_task_name")
    public void setDmpAlysTaskName(String dmpAlysTaskName){
        this.dmpAlysTaskName = dmpAlysTaskName;
    }
    
    @JsonProperty("dmp_patient_id")
    public void setDmpPatientId(String dmpPatientId){
        this.dmpPatientId = dmpPatientId;
    }
    
    @JsonProperty("dmp_sample_id")
    public void setDmpSampleId(String dmpSampleId){ 
        this.dmpSampleId = dmpSampleId;
    }
    
    @JsonProperty("gender")
    public void setGender(Integer gender){ 
        this.gender = gender;
    }
    
    @JsonProperty("gene-panel")
    public void setGenePanel(String genePanel){
        this.genePanel = genePanel;
    }
    
    @JsonProperty("legacy_patient_id")
    public void setLegacyPatientId(String legacyPatientId){ 
        this.legacyPatientId = legacyPatientId;
    }
    
    @JsonProperty("legacy_sample_id")
    public void setLegacySampleId(String legacySampleId){ 
        this.legacySampleId = legacySampleId;
    }
    
    @JsonProperty("retrieve_status")
    public void setRetrieveStatus(Integer retrieveStatus){ 
        this.retrieveStatus = retrieveStatus;
    }
    
    @JsonProperty("sample_coverage")
    public void setSampleCoverage(Integer sampleCoverage){ 
        this.sampleCoverage = sampleCoverage;
    }
    
    @JsonProperty("so_comments")
    public void setSoComments(String soComments){ 
        this.soComments = soComments;
    }
    
    @JsonProperty("so_status_name")
    public void setSoStatusName(String soStatusName){ 
        this.soStatusName = soStatusName;
    }
    
    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }
}
