package org.cbioportal.cmo.pipelines.cvr.model;

import java.util.*;

public class CompositeCnaRecord{
    private String newCnaRecord;
    private String allCnaRecord;
    
    public CompositeCnaRecord(){}
    
    public CompositeCnaRecord(String newCnaRecord, String allCnaRecord){
        this.newCnaRecord = newCnaRecord;
        this.allCnaRecord = allCnaRecord;
    }

    public void setNewCnaRecord(String newCnaRecord){
        this.newCnaRecord = newCnaRecord;
    }

    public String getNewCnaRecord(){
        return this.newCnaRecord != ""  ? this.newCnaRecord : null;
    }

    public void setAllCnaRecord(String allCnaRecord){
        this.allCnaRecord = allCnaRecord;
    }

    public String getAllCnaRecord(){
        return this.allCnaRecord != ""  ? this.allCnaRecord : null;
    }
}
