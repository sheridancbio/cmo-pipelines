package org.cbioportal.cmo.pipelines.cvr.model;

import java.util.*;

public class CompositeSegRecord {
    private String newSegRecord;
    private String oldSegRecord;
    
    public CompositeSegRecord(){
        this.newSegRecord = "";
        this.oldSegRecord = "";
    }
    
    public CompositeSegRecord(String newSegRecord, String oldSegRecord){
        this.newSegRecord = newSegRecord;
        this.oldSegRecord = oldSegRecord;
    }

    public void setNewSegRecord(String newSegRecord){
        this.newSegRecord = newSegRecord;
    }

    public String getNewSegRecord(){
        return this.newSegRecord != ""  ? this.newSegRecord : null;
    }

    public void setOldSegRecord(String oldSegRecord){
        this.oldSegRecord = oldSegRecord;
    }

    public String getOldSegRecord(){
        return this.oldSegRecord != ""  ? this.oldSegRecord : null;
    }
}
