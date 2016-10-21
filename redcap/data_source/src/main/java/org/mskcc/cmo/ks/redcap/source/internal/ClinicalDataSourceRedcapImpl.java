/*
 * Copyright (c) 2016 Memorial Sloan-Kettering Cancer Center.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY, WITHOUT EVEN THE IMPLIED WARRANTY OF MERCHANTABILITY OR FITNESS
 * FOR A PARTICULAR PURPOSE. The software and documentation provided hereunder
 * is on an "as is" basis, and Memorial Sloan-Kettering Cancer Center has no
 * obligations to provide maintenance, support, updates, enhancements or
 * modifications. In no event shall Memorial Sloan-Kettering Cancer Center be
 * liable to any party for direct, indirect, special, incidental or
 * consequential damages, including lost profits, arising out of the use of this
 * software and its documentation, even if Memorial Sloan-Kettering Cancer
 * Center has been advised of the possibility of such damage.
 */

/*
 * This file is part of cBioPortal CMO-Pipelines.
 *
 * cBioPortal is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/
package org.mskcc.cmo.ks.redcap.source.internal;

import org.mskcc.cmo.ks.redcap.models.RedcapProjectAttribute;
import org.mskcc.cmo.ks.redcap.models.RedcapToken;
import org.mskcc.cmo.ks.redcap.models.RedcapAttributeMetadata;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.mskcc.cmo.ks.redcap.source.ClinicalDataSource;

import java.util.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.*;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.apache.log4j.Logger;
import org.springframework.batch.core.configuration.annotation.StepScope;

/**
 *
 * @author Zachary Heins
 * 
 * Use Redcap to fetch clinical metadata and data
 * 
 */

@Configuration
@StepScope
public class ClinicalDataSourceRedcapImpl implements ClinicalDataSource {
    
    @Value("${redcap_url}")
    private String redcapUrl;        
    
    @Value("${mapping_token}")
    private String mappingToken;        
    
    @Value("#{jobParameters[redcap_project]}")
    private String project;
    
    @Value("${metadata_project}")
    private String metadataProject;
          
    private Map<String, String> tokens = new HashMap<>();
    private String timeline;
    private List<Map<String, String>> records;
    private List<Map<String, String>> timelineRecords;
    
    private List<String> sampleHeader;
    private List<String> patientHeader;
    private List<String> combinedHeader;    
    
     private final Logger log = Logger.getLogger(ClinicalDataSourceRedcapImpl.class);
    
    @Override
    public List<String> getSampleHeader() {
        if (tokens.isEmpty()) {
            fillTokens();
        }
        if(sampleHeader == null) {
            getClinicalHeaderData();
        }
        return sampleHeader;
    }
    
    @Override
    public List<String> getPatientHeader() {
        if (tokens.isEmpty()) {
            fillTokens();
        }        
        if(patientHeader == null) {
            getClinicalHeaderData();
        }
        return patientHeader;    
    }

    @Override
    public List<String> getTimelineHeader() {
        if (tokens.isEmpty()) {
            fillTokens();
        }        
        if(combinedHeader == null) {
            getTimelineHeaderData();
        }
        return combinedHeader;            
    }
    
    @Override
    public List<Map<String, String>> getClinicalData() {
        if (tokens.isEmpty()) {
            fillTokens();
        }        
        if(records == null) {
            records = getClinicalData(false);
        }
        return records;            
    }     
    
    @Override
    public List<Map<String, String>> getTimelineData() {
        if (tokens.isEmpty()) {
            fillTokens();
        }        
        if(timelineRecords == null) {
            timelineRecords = getClinicalData(true);
        }
        return timelineRecords;            
    }   
    
    @Override
    public boolean timelineDataExists() {
        if (tokens.isEmpty()) {
            fillTokens();
        }     
        return timeline != null;
    }
    
    private void getClinicalHeaderData() {
        List<RedcapAttributeMetadata> metadata = getMetadata();     
        List<RedcapProjectAttribute> attributes = getAttributes(false); 
        
        Map<RedcapProjectAttribute, RedcapAttributeMetadata> sampleAttributeMap = new LinkedHashMap<>();
        Map<RedcapProjectAttribute, RedcapAttributeMetadata> patientAttributeMap = new LinkedHashMap<>();
        
        for (RedcapProjectAttribute attribute : attributes) {
            for (RedcapAttributeMetadata meta : metadata) {
                if (attribute.getFieldName().toUpperCase().equals(meta.getNormalizedColumnHeader().toUpperCase())) {
                    if(meta.getAttributeType().equals("SAMPLE")) {
                        sampleAttributeMap.put(attribute, meta);
                        break;
                    }                    
                    else {
                        patientAttributeMap.put(attribute, meta);
                        break;
                    }
                }
            }
        }     
        sampleHeader = makeHeader(sampleAttributeMap);
        patientHeader = makeHeader(patientAttributeMap);
    }
    
    private void getTimelineHeaderData() {
        List<RedcapAttributeMetadata> metadata = getMetadata();     
        List<RedcapProjectAttribute> attributes = getAttributes(true); 
        Map<RedcapProjectAttribute, RedcapAttributeMetadata> combinedAttributeMap = new LinkedHashMap<>();
         for (RedcapProjectAttribute attribute : attributes) {
            for (RedcapAttributeMetadata meta : metadata) {
                if (attribute.getFieldName().toUpperCase().equals(meta.getRedcapId().toUpperCase())) {
                    combinedAttributeMap.put(attribute, meta);
                }
            }
        }    
         
         combinedHeader = makeHeader(combinedAttributeMap);
    }
    
    private List<RedcapAttributeMetadata> getMetadata() {
        String metaToken = tokens.get(metadataProject);
        
        RestTemplate restTemplate = new RestTemplate();
        
        log.info("Getting attribute metadatas...");
        
        LinkedMultiValueMap<String, String> uriVariables = new LinkedMultiValueMap<>();
        uriVariables.add("token", metaToken);
        uriVariables.add("content", "record");
        uriVariables.add("format", "json");
        uriVariables.add("type", "flat");
        
        HttpEntity<LinkedMultiValueMap<String, Object>> requestEntity = getRequestEntity(uriVariables);
        ResponseEntity<RedcapAttributeMetadata[]> responseEntity = restTemplate.exchange(redcapUrl, HttpMethod.POST, requestEntity, RedcapAttributeMetadata[].class);
        return Arrays.asList(responseEntity.getBody());        
    }
    
    private List<RedcapProjectAttribute> getAttributes(boolean timelineData) {
        String projectToken;
        if(timelineData) {
            projectToken = tokens.get(timeline);
        }
        else {
            projectToken = tokens.get(project);
        }  
        
        LinkedMultiValueMap<String, String> uriVariables = new LinkedMultiValueMap<>();
        uriVariables.add("token", projectToken);
        uriVariables.add("content", "metadata");
        uriVariables.add("format", "json");
        uriVariables.add("type", "flat");
        
        RestTemplate restTemplate = new RestTemplate();
        HttpEntity<LinkedMultiValueMap<String, Object>> requestEntity = getRequestEntity(uriVariables);
        log.info("Getting attributes for project...");
        ResponseEntity<RedcapProjectAttribute[]> responseEntity = restTemplate.exchange(redcapUrl, HttpMethod.POST, requestEntity, RedcapProjectAttribute[].class);
        return Arrays.asList(responseEntity.getBody());
    }
    
    private  List<Map<String, String>> getClinicalData(boolean timelineData) {        
        String projectToken;
        if(timelineData) {
            projectToken = tokens.get(timeline);
        }
        else {
            projectToken = tokens.get(project);
        }
        
        LinkedMultiValueMap<String, String> uriVariables = new LinkedMultiValueMap<>();
        uriVariables.add("token", projectToken);
        uriVariables.add("content", "record");
        uriVariables.add("format", "json");
        uriVariables.add("type", "flat");
        
        RestTemplate restTemplate = new RestTemplate();
        HttpEntity<LinkedMultiValueMap<String, Object>> requestEntity = getRequestEntity(uriVariables);
        log.info("Getting data for project...");
        ResponseEntity<ObjectNode[]> responseEntity = restTemplate.exchange(redcapUrl, HttpMethod.POST, requestEntity, ObjectNode[].class);    
        List<Map<String, String>> responses = new ArrayList<>();
        
        for(ObjectNode response : responseEntity.getBody())
        {
            Map<String, String> map = new HashMap<>();
            Iterator<Map.Entry<String, JsonNode>> nodeIterator = response.fields();
            while (nodeIterator.hasNext()) {
                Map.Entry<String, JsonNode> entry = (Map.Entry<String, JsonNode>) nodeIterator.next();               
                map.put(entry.getKey().toUpperCase(), entry.getValue().asText());
            }
            responses.add(map);
        }                    
        return responses;
    }   

    private List<String> makeHeader(Map<RedcapProjectAttribute, RedcapAttributeMetadata> attributeMap) {
        List<String> header = new ArrayList<>();        
        for (Map.Entry<RedcapProjectAttribute, RedcapAttributeMetadata> entry : attributeMap.entrySet()) {
            header.add(entry.getValue().getNormalizedColumnHeader());
        }        
        
        return header;
    }    
    
    private void fillTokens() {
        RestTemplate restTemplate = new RestTemplate();
        
        log.info("Getting tokens...");
        
        LinkedMultiValueMap<String, String> uriVariables = new LinkedMultiValueMap<>();
        uriVariables.add("token", mappingToken);
        uriVariables.add("content", "record");
        uriVariables.add("format", "json");
        uriVariables.add("type", "flat");
        
        HttpEntity<LinkedMultiValueMap<String, Object>> requestEntity = getRequestEntity(uriVariables);
        ResponseEntity<RedcapToken[]> responseEntity = restTemplate.exchange(redcapUrl, HttpMethod.POST, requestEntity, RedcapToken[].class);
        
        for (RedcapToken token : responseEntity.getBody()) {
            tokens.put(token.getStudyId(), token.getToken());
            if (token.getStudyId().contains(project) && token.getStudyId().contains("TIMELINE")) {
                timeline = token.getStudyId();
            }
        }
    }        
    
    private HttpEntity getRequestEntity(LinkedMultiValueMap<String, String> uriVariables)
    {  
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);        
        return new HttpEntity<LinkedMultiValueMap<String, String>>(uriVariables, headers);
    }  
    
    public static void main(String[] args) {}

}
