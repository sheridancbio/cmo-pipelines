package org.cbioportal.cmo.pipelines.cvr.model;

import java.util.*;

public class CompositeClinicalRecord{
    private String newClinicalRecord;
    private String oldClinicalRecord;
    
    public CompositeClinicalRecord(){
        this.newClinicalRecord = "";
        this.oldClinicalRecord = "";
    }
    
    public CompositeClinicalRecord(String newClinicalRecord, String oldClinicalRecord){
        this.newClinicalRecord = newClinicalRecord;
        this.oldClinicalRecord = oldClinicalRecord;
    }

    public void setNewClinicalRecord(String newClinicalRecord){
        this.newClinicalRecord = newClinicalRecord;
    }

    public String getNewClinicalRecord(){
        return this.newClinicalRecord != ""  ? this.newClinicalRecord : null;
    }

    public void setOldClinicalRecord(String oldClinicalRecord){
        this.oldClinicalRecord = oldClinicalRecord;
    }

    public String getOldClinicalRecord(){
        return this.oldClinicalRecord != ""  ? this.oldClinicalRecord : null;
    }
}
