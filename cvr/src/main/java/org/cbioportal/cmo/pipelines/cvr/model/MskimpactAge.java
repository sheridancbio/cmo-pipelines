/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cbioportal.cmo.pipelines.cvr.model;

import java.util.*;

/**
 *
 * @author heinsz
 */
public class MskimpactAge {
    private String PATIENT_ID;
    private String AGE;
    
    public MskimpactAge() {}

    /**
     * @return the PATIENT_ID
     */
    public String getPATIENT_ID() {
        return PATIENT_ID;
    }

    /**
     * @param PATIENT_ID the PATIENT_ID to set
     */
    public void setPATIENT_ID(String PATIENT_ID) {
        this.PATIENT_ID = PATIENT_ID;
    }

    /**
     * @return the AGE
     */
    public String getAGE() {
        return AGE;
    }

    /**
     * @param AGE the AGE to set
     */
    public void setAGE(String AGE) {
        this.AGE = AGE;
    }
    
    public static List<String> getFieldNames() {
        List<String> fieldNames = new ArrayList<>();
        fieldNames.add("PATIENT_ID");
        fieldNames.add("AGE");
        return fieldNames;
    }
}
