/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cbioportal.cmo.pipelines.darwin.model;

import java.util.*;
import org.apache.commons.lang.builder.ToStringBuilder;
/**
 *
 * @author jake
 */
public class DarwinTimelineBrainSpine {
    private String DMT_PATIENT_ID_BRAINSPINETMLN;
    private String DMP_PATIENT_ID_MIN_BRAINSPINETMLN;
    private String DMP_PATIENT_ID_MAX_BRAINSPINETMLN;
    private String DMP_PATIENT_ID_COUNT_BRAINSPINETMLN;
    private String DMP_PATIENT_ID_ALL_BRAINSPINETMLN;
    private String START_DATE;
    private String STOP_DATE;
    private String EVENT_TYPE;
    private String TREATMENT_TYPE;
    private String SUBTYPE;
    private String AGENT;
    private String SPECIMEN_REFERENCE_NUMBER;
    private String SPECIMEN_SITE;
    private String SPECIMEN_TYPE;
    private String STATUS;
    private String KARNOFSKY_PERFORMANCE_SCORE;
    private String SURGERY_DETAILS;
    private String EVENT_TYPE_DETAILED;
    private String HISTOLOGY;
    private String WHO_GRADE;
    private String MGMT_STATUS;
    private String SOURCE_PATHOLOGY;
    private String NOTE;
    private String DIAGNOSTIC_TYPE;
    private String DIAGNOSTIC_TYPE_DETAILED;
    private String SOURCE;
    private Map<String, Object> additionalProperties = new HashMap<>();
    
    public DarwinTimelineBrainSpine(){}
    
    public DarwinTimelineBrainSpine(String DMT_PATIENT_ID_BRAINSPINETMLN, String DMP_PATIENT_ID_MIN_BRAINSPINETMLN,
        String DMP_PATIENT_ID_MAX_BRAINSPINETMLN,
        String DMP_PATIENT_ID_COUNT_BRAINSPINETMLN,
        String DMP_PATIENT_ID_ALL_BRAINSPINETMLN,
        String START_DATE,
        String STOP_DATE,
        String EVENT_TYPE,
        String TREATMENT_TYPE,
        String SUBTYPE,
        String AGENT,
        String SPECIMEN_REFERENCE_NUMBER,
        String SPECIMEN_SITE,
        String SPECIMEN_TYPE,
        String STATUS,
        String KARNOFSKY_PERFORMANCE_SCORE,
        String SURGERY_DETAILS,
        String EVENT_TYPE_DETAILED,
        String HISTOLOGY,
        String WHO_GRADE,
        String MGMT_STATUS,
        String SOURCE_PATHOLOGY,
        String NOTE,
        String DIAGNOSTIC_TYPE,
        String DIAGNOSTIC_TYPE_DETAILED,
        String SOURCE){
        this.DMT_PATIENT_ID_BRAINSPINETMLN  = DMT_PATIENT_ID_BRAINSPINETMLN != null ? DMT_PATIENT_ID_BRAINSPINETMLN : "";
        this.DMP_PATIENT_ID_MIN_BRAINSPINETMLN  = DMP_PATIENT_ID_MIN_BRAINSPINETMLN != null ? DMP_PATIENT_ID_MIN_BRAINSPINETMLN : "";
        this.DMP_PATIENT_ID_MAX_BRAINSPINETMLN  = DMP_PATIENT_ID_MAX_BRAINSPINETMLN != null ? DMP_PATIENT_ID_MAX_BRAINSPINETMLN : "";
        this.DMP_PATIENT_ID_COUNT_BRAINSPINETMLN  = DMP_PATIENT_ID_COUNT_BRAINSPINETMLN != null ? DMP_PATIENT_ID_COUNT_BRAINSPINETMLN : "";
        this.DMP_PATIENT_ID_ALL_BRAINSPINETMLN  = DMP_PATIENT_ID_ALL_BRAINSPINETMLN != null ? DMP_PATIENT_ID_ALL_BRAINSPINETMLN : "";
        this.START_DATE  = START_DATE != null ? START_DATE : "";
        this.STOP_DATE  = STOP_DATE != null ? STOP_DATE : "";
        this.EVENT_TYPE  = EVENT_TYPE != null ? EVENT_TYPE : "";
        this.TREATMENT_TYPE  = TREATMENT_TYPE != null ? TREATMENT_TYPE : "";
        this.SUBTYPE  = SUBTYPE != null ? SUBTYPE : "";
        this.AGENT  = AGENT != null ? AGENT : "";
        this.SPECIMEN_REFERENCE_NUMBER = SPECIMEN_REFERENCE_NUMBER != null ? SPECIMEN_REFERENCE_NUMBER : "";
        this.SPECIMEN_SITE = SPECIMEN_SITE != null ? SPECIMEN_SITE : "";
        this.SPECIMEN_TYPE = SPECIMEN_TYPE != null ? SPECIMEN_TYPE : "";
        this.STATUS = STATUS != null ? STATUS : "";
        this.KARNOFSKY_PERFORMANCE_SCORE = KARNOFSKY_PERFORMANCE_SCORE != null ? KARNOFSKY_PERFORMANCE_SCORE : "";
        this.SURGERY_DETAILS = SURGERY_DETAILS != null ? SURGERY_DETAILS : "";
        this.EVENT_TYPE_DETAILED = EVENT_TYPE_DETAILED != null ? EVENT_TYPE_DETAILED : "";
        this.HISTOLOGY = HISTOLOGY != null ? HISTOLOGY : "";
        this.WHO_GRADE = WHO_GRADE != null ? WHO_GRADE : "";
        this.MGMT_STATUS = MGMT_STATUS != null ? MGMT_STATUS : "";
        this.SOURCE_PATHOLOGY = SOURCE_PATHOLOGY != null ? SOURCE_PATHOLOGY : "";
        this.NOTE = NOTE != null ? NOTE : "";
        this.DIAGNOSTIC_TYPE = DIAGNOSTIC_TYPE != null ? DIAGNOSTIC_TYPE : "";
        this.DIAGNOSTIC_TYPE_DETAILED = DIAGNOSTIC_TYPE_DETAILED != null ? DIAGNOSTIC_TYPE_DETAILED : "";
        this.SOURCE = SOURCE != null ? SOURCE : "";
    }
    
    public String getDMT_PATIENT_ID_BRAINSPINETMLN(){
        return DMT_PATIENT_ID_BRAINSPINETMLN;
    }
    public void setDMT_PATIENT_ID_BRAINSPINETMLN(String DMT_PATIENT_ID_BRAINSPINETMLN){
        this.DMT_PATIENT_ID_BRAINSPINETMLN = DMT_PATIENT_ID_BRAINSPINETMLN != null ? DMT_PATIENT_ID_BRAINSPINETMLN : "" ;
    }
    
    public String getDMP_PATIENT_ID_MIN_BRAINSPINETMLN(){
        return DMP_PATIENT_ID_MIN_BRAINSPINETMLN;
    }
    public void setDMP_PATIENT_ID_MIN_BRAINSPINETMLN(String DMP_PATIENT_ID_MIN_BRAINSPINETMLN){
        this.DMP_PATIENT_ID_MIN_BRAINSPINETMLN = DMP_PATIENT_ID_MIN_BRAINSPINETMLN != null ? DMP_PATIENT_ID_MIN_BRAINSPINETMLN : "" ;
    }
    
    public String getDMP_PATIENT_ID_MAX_BRAINSPINETMLN(){
        return DMP_PATIENT_ID_MAX_BRAINSPINETMLN;
    }
    public void setDMP_PATIENT_ID_MAX_BRAINSPINETMLN(String DMP_PATIENT_ID_MAX_BRAINSPINETMLN){
        this.DMP_PATIENT_ID_MAX_BRAINSPINETMLN = DMP_PATIENT_ID_MAX_BRAINSPINETMLN != null ? DMP_PATIENT_ID_MAX_BRAINSPINETMLN : "" ;
    }
    
    public String getDMP_PATIENT_ID_COUNT_BRAINSPINETMLN(){
        return DMP_PATIENT_ID_COUNT_BRAINSPINETMLN;
    }
    public void setDMP_PATIENT_ID_COUNT_BRAINSPINETMLN(String DMP_PATIENT_ID_COUNT_BRAINSPINETMLN){
        this.DMP_PATIENT_ID_COUNT_BRAINSPINETMLN = DMP_PATIENT_ID_COUNT_BRAINSPINETMLN != null ? DMP_PATIENT_ID_COUNT_BRAINSPINETMLN : "" ;
    }
    
    public String getDMP_PATIENT_ID_ALL_BRAINSPINETMLN(){
        return DMP_PATIENT_ID_ALL_BRAINSPINETMLN;
    }
    public void setDMP_PATIENT_ID_ALL_BRAINSPINETMLN(String DMP_PATIENT_ID_ALL_BRAINSPINETMLN){
        this.DMP_PATIENT_ID_ALL_BRAINSPINETMLN = DMP_PATIENT_ID_ALL_BRAINSPINETMLN != null ? DMP_PATIENT_ID_ALL_BRAINSPINETMLN : "" ;
    }
    
    public String getSTART_DATE(){
        return START_DATE;
    }
    public void setSTART_DATE(String START_DATE){
        this.START_DATE = START_DATE != null ? START_DATE : "" ;
    }
    
    public String getSTOP_DATE(){
        return STOP_DATE;
    }
    public void setSTOP_DATE(String STOP_DATE){
        this.STOP_DATE = STOP_DATE != null ? STOP_DATE : "" ;
    }
    
    public String getEVENT_TYPE(){
        return EVENT_TYPE;
    }
    public void setEVENT_TYPE(String EVENT_TYPE){
        this.EVENT_TYPE = EVENT_TYPE != null ? EVENT_TYPE : "" ;
    }
    
    public String getTREATMENT_TYPE(){
        return TREATMENT_TYPE;
    }
    public void setTREATMENT_TYPE(String TREATMENT_TYPE){
        this.TREATMENT_TYPE = TREATMENT_TYPE != null ? TREATMENT_TYPE : "" ;
    }
    
    public String getSUBTYPE(){
        return SUBTYPE;
    }
    public void setSUBTYPE(String SUBTYPE){
        this.SUBTYPE = SUBTYPE != null ? SUBTYPE : "" ;
    }
    
    public String getAGENT(){
        return AGENT;
    }
    public void setAGENT(String AGENT){
        this.AGENT = AGENT != null ? AGENT : "" ;
    }
    
    public String getSPECIMEN_REFERENCE_NUMBER(){
        return SPECIMEN_REFERENCE_NUMBER;
    }
    public void setSPECIMEN_REFERENCE_NUMBER(String SPECIMEN_REFERENCE_NUMBER){
        this.SPECIMEN_REFERENCE_NUMBER = SPECIMEN_REFERENCE_NUMBER != null ? SPECIMEN_REFERENCE_NUMBER : "" ;
    }
    
    public String getSPECIMEN_SITE(){
        return SPECIMEN_SITE;
    }
    public void setSPECIMEN_SITE(String SPECIMEN_SITE){
        this.SPECIMEN_SITE = SPECIMEN_SITE != null ? SPECIMEN_SITE : "" ;
    }
    
    public String getSPECIMEN_TYPE(){
        return SPECIMEN_TYPE;
    }
    public void setSPECIMEN_TYPE(String SPECIMEN_TYPE){
        this.SPECIMEN_TYPE = SPECIMEN_TYPE != null ? SPECIMEN_TYPE : "" ;
    }
    
    public String getSTATUS(){
        return STATUS;
    }
    public void setSTATUS(String STATUS){
        this.STATUS = STATUS != null ? STATUS : "" ;
    }
    
    public String getKARNOFSKY_PERFORMANCE_SCORE(){
        return KARNOFSKY_PERFORMANCE_SCORE;
    }
    public void setKARNOFSKY_PERFORMANCE_SCORE(String KARNOFSKY_PERFORMANCE_SCORE){
        this.KARNOFSKY_PERFORMANCE_SCORE = KARNOFSKY_PERFORMANCE_SCORE != null ? KARNOFSKY_PERFORMANCE_SCORE : "" ;
    }
    
    public String getSURGERY_DETAILS(){
        return SURGERY_DETAILS;
    }
    public void setSURGERY_DETAILS(String SURGERY_DETAILS){
        this.SURGERY_DETAILS = SURGERY_DETAILS != null ? SURGERY_DETAILS : "" ;
    }
    
    public String getEVENT_TYPE_DETAILED(){
        return EVENT_TYPE_DETAILED;
    }
    public void setEVENT_TYPE_DETAILED(String EVENT_TYPE_DETAILED){
        this.EVENT_TYPE_DETAILED = EVENT_TYPE_DETAILED != null ? EVENT_TYPE_DETAILED : "" ;
    }
    
    public String getHISTOLOGY(){
        return HISTOLOGY;
    }
    public void setHISTOLOGY(String HISTOLOGY){
        this.HISTOLOGY = HISTOLOGY != null ? HISTOLOGY : "" ;
    }
    
    public String getWHO_GRADE(){
        return WHO_GRADE;
    }
    public void setWHO_GRADE(String WHO_GRADE){
        this.WHO_GRADE = WHO_GRADE != null ? WHO_GRADE : "" ;
    }
    
    public String getMGMT_STATUS(){
        return MGMT_STATUS;
    }
    public void setMGMT_STATUS(String MGMT_STATUS){
        this.MGMT_STATUS = MGMT_STATUS != null ? MGMT_STATUS : "" ;
    }
    
    public String getSOURCE_PATHOLOGY(){
        return SOURCE_PATHOLOGY;
    }
    public void setSOURCE_PATHOLOGY(String SOURCE_PATHOLOGY){
        this.SOURCE_PATHOLOGY = SOURCE_PATHOLOGY != null ? SOURCE_PATHOLOGY : "" ;
    }
    
    public String getNOTE(){
        return NOTE;
    }
    public void setNOTE(String NOTE){
        this.NOTE = NOTE != null ? NOTE : "" ;
    }
    
    public String getDIAGNOSTIC_TYPE(){
        return DIAGNOSTIC_TYPE;
    }
    public void setDIAGNOSTIC_TYPE(String DIAGNOSTIC_TYPE){
        this.DIAGNOSTIC_TYPE = DIAGNOSTIC_TYPE != null ? DIAGNOSTIC_TYPE : "" ;
    }
    
    public String getDIAGNOSTIC_TYPE_DETAILED(){
        return DIAGNOSTIC_TYPE_DETAILED;
    }
    public void setDIAGNOSTIC_TYPE_DETAILED(String DIAGNOSTIC_TYPE_DETAILED){
        this.DIAGNOSTIC_TYPE_DETAILED = DIAGNOSTIC_TYPE_DETAILED != null ? DIAGNOSTIC_TYPE_DETAILED : "" ;
    }
    
    public String getSOURCE(){
        return SOURCE;
    }
    public void setSOURCE(String SOURCE){
        this.SOURCE = SOURCE != null ? SOURCE : "" ;
    }
    
    public Map<String, Object> getAditionalProperties(){
        return this.additionalProperties;
    }
    public void setAdditionalProperty(String name, Object value){
        this.additionalProperties.put(name, value);
    }
    
    @Override
    public String toString(){
        return ToStringBuilder.reflectionToString(this);
    }
    
    public List<String> getFieldNames(){
        List<String> fieldNames = new ArrayList<>();
        fieldNames.add("DMP_PATIENT_ID_MAX_BRAINSPINETMLN");
        fieldNames.add("DMP_PATIENT_ID_COUNT_BRAINSPINETMLN");
        fieldNames.add("DMP_PATIENT_ID_ALL_BRAINSPINETMLN");
        fieldNames.add("START_DATE");
        fieldNames.add("STOP_DATE");
        fieldNames.add("EVENT_TYPE");
        fieldNames.add("TREATMENT_TYPE");
        fieldNames.add("SUBTYPE");
        fieldNames.add("AGENT");
        fieldNames.add("SPECIMEN_REFERENCE_NUMBER");
        fieldNames.add("SPECIMEN_SITE");
        fieldNames.add("SPECIMEN_TYPE");
        fieldNames.add("STATUS");
        fieldNames.add("KARNOFSKY_PERFORMANCE_SCORE");
        fieldNames.add("SURGERY_DETAILS");
        fieldNames.add("EVENT_TYPE_DETAILED");
        fieldNames.add("HISTOLOGY");
        fieldNames.add("WHO_GRADE");
        fieldNames.add("MGMT_STATUS");
        fieldNames.add("SOURCE_PATHOLOGY");
        fieldNames.add("NOTE");
        fieldNames.add("DIAGNOSTIC_TYPE");
        fieldNames.add("DIAGNOSTIC_TYPE_DETAILED");
        fieldNames.add("SOURCE");
                
        return fieldNames;
    }
        
}
