/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cbioportal.cmo.pipelines.cvr.model;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author heinsz
 */
public class CVRFusionRecord {
    private String hugoSymbol;
    private String entrezGeneId;
    private String center;
    private String tumorSampleBarcode;
    private String fusion;
    private String dnaSupport;
    private String rnaSupport;
    private String method;
    private String frame;
    private String comments;
    
    public CVRFusionRecord() {}
    
    public CVRFusionRecord(CVRSvVariant variant, String sampleId, boolean reversed) {
        this.hugoSymbol = reversed ? variant.getSite2_Gene() : variant.getSite1_Gene();
        this.entrezGeneId = "0";
        this.center = "MSKCC-DMP";
        this.tumorSampleBarcode = sampleId;
        this.fusion = variant.getSite1_Gene().equals(variant.getSite2_Gene()) ? variant.getSite1_Gene() + "-intragenic" : variant.getSite2_Gene() + "-" + variant.getSite1_Gene() + " fusion";
        this.dnaSupport = "yes";
        this.rnaSupport = "unknown";
        this.method = "NA";
        if (variant.getEvent_Info().contains("in frame")) {
            this.frame = "in frame";
        }
        else if (variant.getEvent_Info().contains("out of frame")) {
            this.frame = "out of frame";
        }
        else {
            this.frame = "unknown";
        }
        this.comments = variant.getAnnotation() + " " + variant.getComments();
        this.comments = this.comments.replace("\r\n", "").replace("\r", "").replace("\n", "").replace("\t", "");
    }
    
    public String getHugo_Symbol() {
        return hugoSymbol != null ? hugoSymbol : "";
    }
    
    public void setHugo_Symbol(String hugoSymbol) {
        this.hugoSymbol = hugoSymbol;
    }
    
    public String getEntrez_Gene_Id() {
        return entrezGeneId != null ? entrezGeneId : "";
    }
    
    public void setEntrez_Gene_Id(String entrezGeneId) {
        this.entrezGeneId = entrezGeneId;
    }    
    
    public String getCenter() {
        return center != null ? center : "";
    }
    
    public void setCenter(String center) {
        this.center = center;
    }    
    
    public String getTumor_Sample_Barcode() {
        return tumorSampleBarcode != null ? tumorSampleBarcode : "";
    }
    
    public void setTumor_Sample_Barcode(String tumorSampleBarcode) {
        this.tumorSampleBarcode = tumorSampleBarcode;
    }    
    
    public String getFusion() {
        return fusion != null ? fusion : "";
    }
    
    public void setFusion(String fusion) {
        this.fusion = fusion;
    }    
    
    public String getDNA_support() {
        return dnaSupport != null ? dnaSupport : "";
    }
    
    public void setDNA_support(String dnaSupport) {
        this.dnaSupport = dnaSupport;
    }    
    
    public String getRNA_support() {
        return rnaSupport != null ? rnaSupport : "";
    }
    
    public void setRNA_support(String rnaSupport) {
        this.rnaSupport = rnaSupport;
    }
    
    public String getMethod() {
        return method != null ? method : "";
    }
    
    public void setMethod(String method) {
        this.method = method;
    }    

    public String getFrame() {
        return frame != null ? frame : "";
    }
    
    public void setFrame(String frame) {
        this.frame = frame;
    }

    public String getComments() {
        return comments != null ? comments : "";
    }
    
    public void setComments(String comments) {
        this.comments = comments;
    }    
    public static List<String> getFieldNames() {
        List<String> fieldNames = new ArrayList<String>();
        fieldNames.add("Hugo_Symbol");
        fieldNames.add("Entrez_Gene_Id");
        fieldNames.add("Center");
        fieldNames.add("Tumor_Sample_Barcode");
        fieldNames.add("Fusion");
        fieldNames.add("DNA_support");
        fieldNames.add("RNA_support");
        fieldNames.add("Method");
        fieldNames.add("Frame");
        fieldNames.add("Comments");
        return fieldNames;
    }    
}
