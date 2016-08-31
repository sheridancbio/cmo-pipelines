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
public class CVRData {

@JsonProperty("sample-count")
private Integer sampleCount;
@JsonProperty("disclaimer")
private String disclaimer;
@JsonProperty("results")
private List<CVRMergedResult> results;
@JsonIgnore
private Map<String, Object> additionalProperties = new HashMap<String, Object>();

/**
* No args constructor for use in serialization
* 
*/
public CVRData() {
}

/**
* 
* @param sampleCount
* @param results
* @param disclaimer
*/
public CVRData(Integer sampleCount, String disclaimer, List<CVRMergedResult> results) {
this.sampleCount = sampleCount;
this.disclaimer = disclaimer;
this.results = results;
}


@JsonIgnore
public void addResult(CVRMergedResult result) {
    this.results.add(result);
}

/**
* 
* @return
* The sampleCount
*/
@JsonProperty("sample-count")
public Integer getSampleCount() {
return sampleCount;
}

/**
* 
* @param sampleCount
* The sample-count
*/
@JsonProperty("sample-count")
public void setSampleCount(Integer sampleCount) {
this.sampleCount = sampleCount;
}

/**
* 
* @return
* The disclaimer
*/
@JsonProperty("disclaimer")
public String getDisclaimer() {
return disclaimer;
}

/**
* 
* @param disclaimer
* The disclaimer
*/
@JsonProperty("disclaimer")
public void setDisclaimer(String disclaimer) {
this.disclaimer = disclaimer;
}

/**
* 
* @return
* The results
*/
@JsonProperty("results")
public List<CVRMergedResult> getResults() {
return results;
}

/**
* 
* @param results
* The results
*/
@JsonProperty("results")
public void setResults(List<CVRMergedResult> results) {
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