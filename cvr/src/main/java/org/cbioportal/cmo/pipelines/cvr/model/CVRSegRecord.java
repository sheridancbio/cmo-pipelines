/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cbioportal.cmo.pipelines.cvr.model;

import java.util.*;
import org.springframework.util.StringUtils;
/**
 *
 * @author jake
 */
public class CVRSegRecord {
    private String chromosome;
    private String start;
    private String end;
    private String num_mark;
    private String seg_mean;
    private String id;
    private String isNew;
    
    public CVRSegRecord(){}
    
    public void setIsNew(String isNew){
        this.isNew = isNew;
    }

    public String getIsNew(){
        return this.isNew != null ? this.isNew : "";
    }

    public String getchrom(){
        return this.chromosome != null ? this.chromosome : "";
    }
    public void setchrom(String chromosome){
        this.chromosome = chromosome;
    }
    
    
    public void setloc_start(String start){
        this.start = start;
    }
    public String getloc_start(){
        return this.start != null ? this.start : "";
    }
    
    
    public void setloc_end(String end){
        this.end = end;
    }
    public String getloc_end(){
        return this.end != null ? this.end : "";
    }
    
    
    public void setnum_mark(String num_mark){
        this.num_mark = num_mark;
    }
    public String getnum_mark(){
        return this.num_mark != null ? this.num_mark : "";
    }
    
    
    public void setseg_mean(String seg_mean){
        this.seg_mean = seg_mean;
    }
    public String getseg_mean(){
        return this.seg_mean != null ? this.seg_mean : "";
    }
    
    public void setID(String id){
        this.id = id;
    }
    public String getID(){
        return this.id != null ? this.id : "";
    }
    
    
    public static List<String> getFieldNames(){
        List<String> fieldNames = new ArrayList<>();
        
        fieldNames.add("ID");
        fieldNames.add("chrom");
        fieldNames.add("loc_start");
        fieldNames.add("loc_end");
        fieldNames.add("num_mark");
        fieldNames.add("seg_mean");
        
        return fieldNames;
    }
    //reformatted field names as original fields contained "."
    //getHeaderNames reverts change to original format
    public static List<String> getHeaderNames(){
        List<String> headerNames = getFieldNames();
        
        for(int i=0; i<headerNames.size();i++){
            headerNames.set(i, headerNames.get(i).replace("_", "."));
        }
        
        return headerNames;
    }
    
}
