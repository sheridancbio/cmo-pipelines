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

@JsonInclude(JsonInclude.Include.NON_NULL)
@Generated("org.jsonschema2pojo")
@JsonPropertyOrder({
"sample-count",
"disclaimer",
"results"
})

public class GMLData {

    @JsonProperty("sample-count")
    private Integer sampleCount;
    @JsonProperty("disclaimer")
    private String disclaimer;
    @JsonProperty("results")
    private List<GMLResult> results;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();
    
    public GMLData(){}
    
    public GMLData(Integer sampleCount, String disclaimer, List<GMLResult> results){
        this.sampleCount = sampleCount;
        this.disclaimer = disclaimer;
        this.results = results;
    }
    
    @JsonIgnore
    public void addResult(GMLResult result) {
        this.results.add(result);
    }

    /**
     *
     * @return The sampleCount
     */
    @JsonProperty("sample-count")
    public Integer getSampleCount() {
        return sampleCount;
    }

    /**
     *
     * @param sampleCount The sample-count
     */
    @JsonProperty("sample-count")
    public void setSampleCount(Integer sampleCount) {
        this.sampleCount = sampleCount;
    }

    /**
     *
     * @return The disclaimer
     */
    @JsonProperty("disclaimer")
    public String getDisclaimer() {
        return disclaimer;
    }

    /**
     *
     * @param disclaimer The disclaimer
     */
    @JsonProperty("disclaimer")
    public void setDisclaimer(String disclaimer) {
        this.disclaimer = disclaimer;
    }

    /**
     *
     * @return The results
     */
    @JsonProperty("results")
    public List<GMLResult> getResults() {
        return results;
    }

    /**
     *
     * @param results The results
     */
    @JsonProperty("results")
    public void setResults(List<GMLResult> results) {
        this.results = results;
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
