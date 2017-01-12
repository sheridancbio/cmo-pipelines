/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cbioportal.cmo.pipelines.cvr.model;

/**
 *
 * @author heinsz
 */
import java.util.HashMap;
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
    "alys2sample_id",
    "disclaimer",
    "dmp_sample_id",
    "seg-data"
})
public class CVRSegData {

    @JsonProperty("alys2sample_id")
    private String alys2sampleId;
    @JsonProperty("disclaimer")
    private String disclaimer;
    @JsonProperty("dmp_sample_id")
    private String dmpSampleId;
    @JsonProperty("seg-data")
    private List<List<String>> segData;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    /**
     * No args constructor for use in serialization
     *
     */
    public CVRSegData() {
    }

    /**
     *
     * @param segData
     * @param disclaimer
     * @param dmpSampleId
     * @param alys2sampleId
     */
    public CVRSegData(String alys2sampleId, String disclaimer, String dmpSampleId, List<List<String>> segData) {
        this.alys2sampleId = alys2sampleId;
        this.disclaimer = disclaimer;
        this.dmpSampleId = dmpSampleId;
        this.segData = segData;
    }

    /**
     *
     * @return The alys2sampleId
     */
    @JsonProperty("alys2sample_id")
    public String getAlys2sampleId() {
        return alys2sampleId;
    }

    /**
     *
     * @param alys2sampleId The alys2sample_id
     */
    @JsonProperty("alys2sample_id")
    public void setAlys2sampleId(String alys2sampleId) {
        this.alys2sampleId = alys2sampleId;
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
     * @return The dmpSampleId
     */
    @JsonProperty("dmp_sample_id")
    public String getDmpSampleId() {
        return dmpSampleId;
    }

    /**
     *
     * @param dmpSampleId The dmp_sample_id
     */
    @JsonProperty("dmp_sample_id")
    public void setDmpSampleId(String dmpSampleId) {
        this.dmpSampleId = dmpSampleId;
    }

    /**
     *
     * @return The segData
     */
    @JsonProperty("seg-data")
    public List<List<String>> getSegData() {
        return segData;
    }

    /**
     *
     * @param segData The seg-data
     */
    @JsonProperty("seg-data")
    public void setSegData(List<List<String>> segData) {
        this.segData = segData;
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
