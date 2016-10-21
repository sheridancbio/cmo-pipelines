/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.mskcc.cmo.ks.redcap.models;

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
    "study_id",
    "token",
    "my_first_instrument_complete"
})
public class RedcapToken {

    @JsonProperty("study_id")
    private String studyId;
    @JsonProperty("token")
    private String token;
    @JsonProperty("my_first_instrument_complete")
    private String myFirstInstrumentComplete;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    /**
    * No args constructor for use in serialization
    * 
    */
    public RedcapToken() {
    }

    /**
    * 
    * @param myFirstInstrumentComplete
    * @param token
    * @param studyId
    */
    public RedcapToken(String studyId, String token, String myFirstInstrumentComplete) {
        this.studyId = studyId;
        this.token = token;
        this.myFirstInstrumentComplete = myFirstInstrumentComplete;
    }

    /**
    * 
    * @return
    * The studyId
    */
    @JsonProperty("study_id")
    public String getStudyId() {
        return studyId;
    }

    /**
    * 
    * @param studyId
    * The study_id
    */
    @JsonProperty("study_id")
    public void setStudyId(String studyId) {
        this.studyId = studyId;
    }

    /**
    * 
    * @return
    * The token
    */
    @JsonProperty("token")
    public String getToken() {
        return token;
    }

    /**
    * 
    * @param token
    * The token
    */
    @JsonProperty("token")
    public void setToken(String token) {
        this.token = token;
    }

    /**
    * 
    * @return
    * The myFirstInstrumentComplete
    */
    @JsonProperty("my_first_instrument_complete")
    public String getMyFirstInstrumentComplete() {
        return myFirstInstrumentComplete;
    }

    /**
    * 
    * @param myFirstInstrumentComplete
    * The my_first_instrument_complete
    */
    @JsonProperty("my_first_instrument_complete")
    public void setMyFirstInstrumentComplete(String myFirstInstrumentComplete) {
        this.myFirstInstrumentComplete = myFirstInstrumentComplete;
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
