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
import java.util.*;
import javax.annotation.Generated;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import org.apache.commons.lang.builder.ToStringBuilder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Generated("org.jsonschema2pojo")
@JsonPropertyOrder({
    "cnv-intragenic-variants-gml",
    "cnv-variants-gml",
    "meta-data",
    "snp-indel-gml"
})
public class GMLResult {
    @JsonProperty("cnv-intragenic-variants-gml")
    private List<GMLCnvIntragenicVariant> cnvIntragenicVariantsGml;
    @JsonProperty("cnv-variants-gml")
    private List<CVRCnvVariant> cnvVariantsGml;
    @JsonProperty("meta-data")
    private GMLMetaData metaData;
    @JsonProperty("snp-indel-gml")
    private List<GMLSnp> snpIndelGml;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();
    
    public GMLResult() {}
    
    public GMLResult(List<GMLCnvIntragenicVariant> cnvIntragenicVariantsGml,
            List<CVRCnvVariant> cnvVariantsGml,
            GMLMetaData metaData,
            List<GMLSnp> snpIndelGml){
        this.cnvIntragenicVariantsGml = cnvIntragenicVariantsGml;
        this.cnvVariantsGml = cnvVariantsGml;
        this.metaData = metaData;
        this.snpIndelGml = snpIndelGml;
    }
    
    @JsonProperty("cnv-intragenic-variants-gml")
    public List<GMLCnvIntragenicVariant> getCnvIntragenicVariantsGml(){
        return cnvIntragenicVariantsGml;
    }
    
    @JsonProperty("cnv-variants-gml")
    public List<CVRCnvVariant> getCnvVariantsGml(){ 
        return cnvVariantsGml;
    }
    
    @JsonProperty("meta-data")
    public GMLMetaData getMetaData(){ 
        return metaData;
    }
    
    @JsonProperty("snp-indel-gml")
    public List<GMLSnp> getSnpIndelGml(){ 
        return snpIndelGml;
    }
    
    @JsonProperty("cnv-intragenic-variants-gml")
    public void setCnvIntragenicVariantsGml(List<GMLCnvIntragenicVariant> cnvIntragenicVariantsGml){
        this.cnvIntragenicVariantsGml = cnvIntragenicVariantsGml;
    }
    
    @JsonProperty("cnv-variants-gml")
    public void setCnvVariantsGml(List<CVRCnvVariant> cnvVariantsGml){ 
        this.cnvVariantsGml = cnvVariantsGml;
    }
    
    @JsonProperty("meta-data")
    public void setMetaData(GMLMetaData metaData){ 
        this.metaData = metaData;
    }
    
    @JsonProperty("snp-indel-gml")
    public void setSnpIndelGml(List<GMLSnp> snpIndelGml){ 
        this.snpIndelGml = snpIndelGml;
    }
    
    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
    
    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }
    
    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }
    
    public GMLResult withAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
        return this;
    }  
    
}
