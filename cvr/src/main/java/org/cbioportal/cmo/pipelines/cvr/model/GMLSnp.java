/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cbioportal.cmo.pipelines.cvr.model;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.Generated;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Generated("org.jsonschema2pojo")
@JsonPropertyOrder({
    "aa_change",
    "acmg_criteria",
    "allele_depth",
    "alt_allele",
    "cDNA_change",
    "chromosome",
    "clinical-signed-out",
    "clinvar",
    "dbSNP_id",
    "depth",
    "exon_num",
    "gene_id",
    "interpretation",
    "mafreq_1000g",
    "path_score",
    "ref_allele",
    "start_position",
    "transcript_id",
    "variant_freq"
})
/**
 *
 * @author jake
 */
public class GMLSnp {
    @JsonProperty("aa_change")
    private String aaChange;
    @JsonProperty("acmg_criteria")
    private String acmgCriteria;
    @JsonProperty("allele_depth")
    private Integer alleleDepth;
    @JsonProperty("alt_allele")
    private String altAllele;
    @JsonProperty("cDNA_change")
    private String cDNAChange;
    @JsonProperty("chromosome")
    private String chromosome;
    @JsonProperty("clinical-signed-out")
    private String clinicalSignedOut;
    @JsonProperty("clinvar")
    private String clinvar;
    @JsonProperty("dbSNP_id")
    private String dbSNPId;
    @JsonProperty("depth")
    private Integer depth;
    @JsonProperty("exon_num")
    private String exonNum;
    @JsonProperty("gene_id")
    private String geneId;
    @JsonProperty("interpretation")
    private String interpretation;
    @JsonProperty("mafreq_1000g")
    private String mafreq1000g;
    @JsonProperty("path_score")
    private String pathScore;
    @JsonProperty("ref_allele")
    private String refAllele;
    @JsonProperty("start_position")
    private Integer startPosition;
    @JsonProperty("transcript_id")
    private String transcriptId;
    @JsonProperty("variant_freq")
    private String variantFreq;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();
    
    public GMLSnp(){}
    
    public GMLSnp(String aaChange, String acmgCriteria, Integer alleleDepth, String altAllele,
            String cDNAChange, String chromosome, String clinicalSignedOut, String clinvar, String dbSNPId,
            Integer depth, String exonNum, String geneId, String interpretation, String mafreq1000g,
            String pathScore, String refAllele, Integer startPosition, String transcriptId, String variantFreq){
        this.aaChange = aaChange;
        this.acmgCriteria = acmgCriteria;
        this.alleleDepth = alleleDepth;
        this.altAllele = altAllele;
        this.cDNAChange = cDNAChange;
        this.chromosome = chromosome;
        this.clinicalSignedOut = clinicalSignedOut;
        this.clinvar = clinvar;
        this.dbSNPId = dbSNPId;
        this.depth = depth;
        this.exonNum = exonNum;
        this.geneId = geneId;
        this.interpretation = interpretation;
        this.mafreq1000g = mafreq1000g;
        this.pathScore = pathScore;
        this.refAllele = refAllele;
        this.startPosition = startPosition;
        this.transcriptId = transcriptId;
        this.variantFreq = variantFreq;
    }
    
    @JsonProperty("aa_change")
    public String getAaChange(){
        return aaChange;
    }
    
    @JsonProperty("acmg_criteria")
    public String getAcmgCriteria(){
        return acmgCriteria;
    }
    
    @JsonProperty("allele_depth")
    public Integer getAlleleDepth(){
        return alleleDepth;
    }
    
    @JsonProperty("alt_allele")
    public String getAltAllele(){
        return altAllele;
    }
    
    @JsonProperty("cDNA_change")
    public String getCDNAChange(){
        return cDNAChange;
    }
    
    @JsonProperty("chromosome")
    public String getChromosome(){
        return chromosome;
    }
    
    @JsonProperty("clinical-signed-out")
    public String getClinicalSignedOut(){
        return clinicalSignedOut;
    }
    
    @JsonProperty("clinvar")
    public String getClinvar(){
        return clinvar;
    }
    
    @JsonProperty("dbSNP_id")
    public String getDbSNPId(){
        return dbSNPId;
    }
    
    @JsonProperty("depth")
    public Integer getDepth(){
        return depth;
    }
    
    @JsonProperty("exon_num")
    public String getExonNum(){
        return exonNum;
    }
    
    @JsonProperty("gene_id")
    public String getGeneId(){
        return geneId;
    }
    
    @JsonProperty("interpretation")
    public String getInterpretation(){
        return interpretation;
    }
    
    @JsonProperty("mafreq_1000g")
    public String getMafreq1000g(){
        return mafreq1000g;
    }
    
    @JsonProperty("path_score")
    public String getPathScore(){
        return pathScore;
    }
    
    @JsonProperty("ref_allele")
    public String getRefAllele(){
        return refAllele;
    }
    
    @JsonProperty("start_position")
    public Integer getStartPosition(){
        return startPosition;
    }
    
    @JsonProperty("transcript_id")
    public String getTranscriptId(){
        return transcriptId;
    }
    
    @JsonProperty("variant_freq")
    public String getVariantFreq(){
        return variantFreq;
    }
    
    @JsonProperty("aa_change")
    public void setAaChange(String aaChange){
        this.aaChange = aaChange;
    }
    
    @JsonProperty("acmg_criteria")
    public void setAcmgCriteria(String acmgCriteria){
        this.acmgCriteria = acmgCriteria;
    }
    
    @JsonProperty("allele_depth")
    public void setAlleleDepth(Integer alleleDepth){
        this.alleleDepth = alleleDepth;
    }
    
    @JsonProperty("alt_allele")
    public void setAltAllele(String altAllele){
        this.altAllele = altAllele;
    }
    
    @JsonProperty("cDNA_change")
    public void setCDNAChange(String cDNAChange){
        this.cDNAChange = cDNAChange;
    }
    
    @JsonProperty("chromosome")
    public void setChromosome(String chromosome){
        this.chromosome = chromosome;
    }
    
    @JsonProperty("clinical-signed-out")
    public void setClinicalSignedOut(String clinicalSignedOut){
        this.clinicalSignedOut = clinicalSignedOut;
    }
    
    @JsonProperty("clinvar")
    public void setClinvar(String clinvar){
        this.clinvar = clinvar;
    }
    
    @JsonProperty("dbSNP_id")
    public void setDbSNPId(String dbSNPId){
        this.dbSNPId = dbSNPId;
    }
    
    @JsonProperty("depth")
    public void setDepth(Integer depth){
        this.depth = depth;
    }
    
    @JsonProperty("exon_num")
    public void setExonNum(String exonNum){
        this.exonNum = exonNum;
    }
    
    @JsonProperty("gene_id")
    public void setGeneId(String geneId){
        this.geneId = geneId;
    }
    
    @JsonProperty("interpretation")
    public void setInterpretation(String interpretation){
        this.interpretation = interpretation;
    }
    
    @JsonProperty("mafreq_1000g")
    public void setMafreq1000g(String mafreq1000g){
        this.mafreq1000g = mafreq1000g;
    }
    
    @JsonProperty("path_score")
    public void setPathScore(String pathScore){
        this.pathScore = pathScore;
    }
    
    @JsonProperty("ref_allele")
    public void setRefAllele(String refAllele){
        this.refAllele = refAllele;
    }
    
    @JsonProperty("start_position")
    public void setStartPosition(Integer startPosition){
        this.startPosition = startPosition;
    }
    
    @JsonProperty("transcript_id")
    public void setTranscriptId(String transcriptId){
        this.transcriptId = transcriptId;
    }
    
    @JsonProperty("variant_freq")
    public void setVariantFreq(String variantFreq){
        this.variantFreq = variantFreq;
    }
    
    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }
    
}




