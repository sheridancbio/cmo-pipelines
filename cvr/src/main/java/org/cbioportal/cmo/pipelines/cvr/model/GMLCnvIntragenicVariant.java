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
import java.util.HashMap;
import java.util.Map;
import javax.annotation.Generated;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import java.util.*;
import org.apache.commons.lang.builder.ToStringBuilder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Generated("org.jsonschema2pojo")
@JsonPropertyOrder({
    "chromosome",
    "clinical_signed_out",
    "cluster",
    "cnv_class_name",
    "cytoband",
    "gene_id",
    "interpretation"
})

public class GMLCnvIntragenicVariant {
    @JsonProperty("chromosome")
    private String chromosome;
    @JsonProperty("clinical_signed_out")
    private String clinicalSignedOut;
    @JsonProperty("cluster")
    private String cluster;
    @JsonProperty("cnv_class_name")
    private String cnvClassName;
    @JsonProperty("cytoband")
    private String cytoband;
    @JsonProperty("gene_id")
    private String geneId;
    @JsonProperty("interpretation")
    private String interpretation;
    
    public GMLCnvIntragenicVariant(){}
    
    public GMLCnvIntragenicVariant(String chromosome, String clinicalSignedOut, String cluster, 
            String cnvClassName, String cytoband, String geneId, String interpretation){
        this.chromosome = chromosome;
        this.clinicalSignedOut = clinicalSignedOut;
        this.cluster = cluster;
        this.cnvClassName = cnvClassName;
        this.cytoband = cytoband;
        this.geneId = geneId;
        this.interpretation = interpretation;
    }
    
    @JsonProperty("chromosome")
    public String getChromosome(){
        return chromosome;
    }
    
    @JsonProperty("clinical_signed_out")
    public String getClinicalSignedOut(){
        return clinicalSignedOut;
    }
    
    @JsonProperty("cluster")
    public String getCluster(){ 
        return cluster;
    }
    
    @JsonProperty("cnv_class_name")
    public String getCnvClassName(){ 
        return cnvClassName;
    }
    
    @JsonProperty("cytoband")
    public String getCytoband(){
        return cytoband;
    }
    
    @JsonProperty("gene_id")
    public String getGeneId(){ 
        return geneId;
    }
    
    @JsonProperty("interpretation")
    public String getInterpretation(){ 
        return interpretation;
    }
    
    @JsonProperty("chromosome")
    public void setChromosome(String chromosome){
        this.chromosome = chromosome;
    }
    
    @JsonProperty("clinical_signed_out")
    public void setClinicalSignedOut(String clinicalSignedOut){
        this.clinicalSignedOut = clinicalSignedOut;
    }
    
    @JsonProperty("cluster")
    public void setCluster(String cluster){ 
        this.cluster = cluster;
    }
    
    @JsonProperty("cnv_class_name")
    public void setCnvClassName(String cnvClassName){ 
        this.cnvClassName = cnvClassName;
    }
    
    @JsonProperty("cytoband")
    public void setCytoband(String cytoband){
        this.cytoband = cytoband;
    }
    
    @JsonProperty("gene_id")
    public void setGeneId(String geneId){ 
        this.geneId = geneId;
    }
    
    @JsonProperty("interpretation")
    public void setInterpretation(String interpretation){ 
        this.interpretation = interpretation;
    }
    
}
