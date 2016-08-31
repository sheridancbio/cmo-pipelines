package org.cbioportal.cmo.pipelines.cvr.model;

import java.util.*;

public class CompositeSvRecord{
    private String newSvRecord;
    private String oldSvRecord;
    
    public CompositeSvRecord(){
        this.newSvRecord = "";
        this.oldSvRecord = "";
    }
    
    public CompositeSvRecord(String newSvRecord, String oldSvRecord){
        this.newSvRecord = newSvRecord;
        this.oldSvRecord = oldSvRecord;
    }

    public void setNewSvRecord(String newSvRecord){
        this.newSvRecord = newSvRecord;
    }

    public String getNewSvRecord(){
        return this.newSvRecord != ""  ? this.newSvRecord : null;
    }

    public void setOldSvRecord(String oldSvRecord){
        this.oldSvRecord = oldSvRecord;
    }

    public String getOldSvRecord(){
        return this.oldSvRecord != ""  ? this.oldSvRecord : null;
    }
}
