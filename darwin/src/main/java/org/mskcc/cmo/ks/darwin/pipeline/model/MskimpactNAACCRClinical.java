/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.mskcc.cmo.ks.darwin.pipeline.model;

import java.util.*;
import org.codehaus.plexus.util.StringUtils;

/**
 *
 * @author heinsz
 */
public class MskimpactNAACCRClinical {
    private String DMP_ID_DEMO;
    private String PT_NAACCR_SEX_CODE;
    private String PT_NAACCR_RACE_CODE_PRIMARY;
    private String PT_NAACCR_RACE_CODE_SECONDARY;
    private String PT_NAACCR_RACE_CODE_TERTIARY;
    private String PT_NAACCR_ETHNICITY_CODE;
    private String PT_BIRTH_YEAR;
    
    public MskimpactNAACCRClinical() {}
    
    public MskimpactNAACCRClinical(String DMP_ID_DEMO, String PT_NAACCR_SEX_CODE, String PT_NAACCR_RACE_CODE_PRIMARY,
            String PT_NAACCR_RACE_CODE_SECONDARY, String PT_NAACCR_RACE_CODE_TERTIARY, String PT_NAACCR_ETHNICITY_CODE,
            String PT_BIRTH_YEAR) {
        this.DMP_ID_DEMO = StringUtils.isNotEmpty(DMP_ID_DEMO) ? DMP_ID_DEMO : "NA";
        this.PT_NAACCR_SEX_CODE = StringUtils.isNotEmpty(PT_NAACCR_SEX_CODE) ? PT_NAACCR_SEX_CODE : "NA";
        this.PT_NAACCR_RACE_CODE_PRIMARY = StringUtils.isNotEmpty(PT_NAACCR_RACE_CODE_PRIMARY) ? PT_NAACCR_RACE_CODE_PRIMARY : "NA";
        this.PT_NAACCR_RACE_CODE_SECONDARY = StringUtils.isNotEmpty(PT_NAACCR_RACE_CODE_SECONDARY) ? PT_NAACCR_RACE_CODE_SECONDARY : "NA";
        this.PT_NAACCR_RACE_CODE_TERTIARY = StringUtils.isNotEmpty(PT_NAACCR_RACE_CODE_TERTIARY) ? PT_NAACCR_RACE_CODE_TERTIARY : "NA";
        this.PT_NAACCR_ETHNICITY_CODE = StringUtils.isNotEmpty(PT_NAACCR_ETHNICITY_CODE) ? PT_NAACCR_ETHNICITY_CODE : "NA";
        this.PT_BIRTH_YEAR = StringUtils.isNotEmpty(PT_BIRTH_YEAR) ? PT_BIRTH_YEAR : "NA";
    }
    
    
    public String getDMP_ID_DEMO() {
        return DMP_ID_DEMO;
    }
    
    public void setDMP_ID_DEMO(String DMP_ID_DEMO) {
        this.DMP_ID_DEMO = StringUtils.isNotEmpty(DMP_ID_DEMO) ? DMP_ID_DEMO : "NA";
    }
    
    public String getPT_NAACCR_SEX_CODE() {
        return PT_NAACCR_SEX_CODE;
    }
    
    public void setPT_NAACCR_SEX_CODE(String PT_NAACCR_SEX_CODE) {
        this.PT_NAACCR_SEX_CODE = StringUtils.isNotEmpty(PT_NAACCR_SEX_CODE) ? PT_NAACCR_SEX_CODE : "NA";
    }

    public String getPT_NAACCR_RACE_CODE_PRIMARY() {
        return PT_NAACCR_RACE_CODE_PRIMARY;
    }
    
    public void setPT_NAACCR_RACE_CODE_PRIMARY(String PT_NAACCR_RACE_CODE_PRIMARY) {
        this.PT_NAACCR_RACE_CODE_PRIMARY = StringUtils.isNotEmpty(PT_NAACCR_RACE_CODE_PRIMARY) ? PT_NAACCR_RACE_CODE_PRIMARY : "NA";
    }

    public String getPT_NAACCR_RACE_CODE_SECONDARY() {
        return PT_NAACCR_RACE_CODE_SECONDARY;
    }
    
    public void setPT_NAACCR_RACE_CODE_SECONDARY(String PT_NAACCR_RACE_CODE_SECONDARY) {
        this.PT_NAACCR_RACE_CODE_SECONDARY = StringUtils.isNotEmpty(PT_NAACCR_RACE_CODE_SECONDARY) ? PT_NAACCR_RACE_CODE_SECONDARY : "NA";
    }

    public String getPT_NAACCR_RACE_CODE_TERTIARY() {
        return PT_NAACCR_RACE_CODE_TERTIARY;
    }
    
    public void setPT_NAACCR_RACE_CODE_TERTIARY(String PT_NAACCR_RACE_CODE_TERTIARY) {
        this.PT_NAACCR_RACE_CODE_TERTIARY = StringUtils.isNotEmpty(PT_NAACCR_RACE_CODE_TERTIARY) ? PT_NAACCR_RACE_CODE_TERTIARY : "NA";
    }
    
    public String getPT_NAACCR_ETHNICITY_CODE() {
        return PT_NAACCR_ETHNICITY_CODE;
    }
    
    public void setPT_NAACCR_ETHNICITY_CODE(String PT_NAACCR_ETHNICITY_CODE) {
        this.PT_NAACCR_ETHNICITY_CODE = StringUtils.isNotEmpty(PT_NAACCR_ETHNICITY_CODE) ? PT_NAACCR_ETHNICITY_CODE : "NA";
    }
    
    public String getPT_BIRTH_YEAR() {
        return PT_BIRTH_YEAR;
    }
    
    public void setPT_BIRTH_YEAR(String PT_BIRTH_YEAR) {
        this.PT_BIRTH_YEAR = StringUtils.isNotEmpty(PT_BIRTH_YEAR) ? PT_BIRTH_YEAR : "NA";
    }
    
    public List<String> getFieldNames() {
        List<String> fieldNames = new ArrayList<>();
        fieldNames.add("DMP_ID_DEMO");
        fieldNames.add("PT_NAACCR_SEX_CODE");
        fieldNames.add("PT_NAACCR_RACE_CODE_PRIMARY");
        fieldNames.add("PT_NAACCR_RACE_CODE_SECONDARY");
        fieldNames.add("PT_NAACCR_RACE_CODE_TERTIARY");
        fieldNames.add("PT_NAACCR_ETHNICITY_CODE");
        fieldNames.add("PT_BIRTH_YEAR");
        return fieldNames;
    }
    
    public List<String> getHeaders() {
        List<String> headerNames = new ArrayList<>();
        headerNames.add("PATIENT_ID");
        headerNames.add("NAACCR_SEX_CODE");
        headerNames.add("NAACCR_RACE_CODE_PRIMARY");
        headerNames.add("NAACCR_RACE_CODE_SECONDARY");
        headerNames.add("NAACCR_RACE_CODE_TERTIARY");
        headerNames.add("NAACCR_ETHNICITY_CODE");
        headerNames.add("BIRTH_YEAR");
        return headerNames;
    }
}
