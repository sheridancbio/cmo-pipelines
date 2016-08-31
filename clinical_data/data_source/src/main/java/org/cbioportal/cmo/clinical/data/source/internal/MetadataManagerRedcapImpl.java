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
package org.cbioportal.cmo.clinical.data.source.internal;

import org.cbioportal.cmo.clinical.data.models.*;
import org.cbioportal.cmo.clinical.data.source.MetadataManager;

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
public class MetadataManagerRedcapImpl implements MetadataManager {
    
    @Value("${redcap_url}")
    private String redcapUrl;        
    
    @Value("${mapping_token}")
    private String mappingToken;        
    
    @Value("${metadata_project}")
    private String metadataProject;
          
    private Map<String, String> tokens = new HashMap<>();
    
     private final Logger log = Logger.getLogger(MetadataManagerRedcapImpl.class);
    
     @Bean
     @StepScope
     @Override
    public MetadataManagerRedcapImpl metadataManager() {
        return this;
    }
    
    @Override
    public Map<String, List<String>> getFullHeader(List<String> header) {
        if (tokens.isEmpty()) {
            fillTokens();
        }
        List<RedcapAttributeMetadata> metadata = getMetadata();     
        Map<String, RedcapAttributeMetadata> combinedAttributeMap = new LinkedHashMap<>();
        for (String attribute : header) {
            for (RedcapAttributeMetadata meta : metadata) {
                if (attribute.toUpperCase().equals(meta.getRedcapId().toUpperCase())) {
                    combinedAttributeMap.put(attribute, meta);
                }
            }
        }
        return makeHeader(combinedAttributeMap);         
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
     
    private Map<String,List<String>> makeHeader(Map<String, RedcapAttributeMetadata> attributeMap) {
        Map<String, List<String>> headerMap = new LinkedHashMap<>();
        List<String> displayNames = new ArrayList<>();
        List<String> descriptions = new ArrayList<>();
        List<String> datatypes = new ArrayList<>();
        List<String> priorities = new ArrayList<>();
        List<String> header = new ArrayList<>();
        
        for (Map.Entry<String, RedcapAttributeMetadata> entry : attributeMap.entrySet()) {
            displayNames.add(entry.getValue().getDisplayName());
            descriptions.add(entry.getValue().getDescriptions());
            datatypes.add(entry.getValue().getDatatype());
            priorities.add(entry.getValue().getPriority());
            header.add(entry.getValue().getNormalizedColumnHeader());
        }
        
        headerMap.put("display_names", displayNames);
        headerMap.put("descriptions", descriptions);
        headerMap.put("datatypes", datatypes);
        headerMap.put("priorities", priorities);
        headerMap.put("header", header);
        
        return headerMap;
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
        }
    }        
    
    private HttpEntity getRequestEntity(LinkedMultiValueMap<String, String> uriVariables)
    {  
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);        
        return new HttpEntity<LinkedMultiValueMap<String, String>>(uriVariables, headers);
    }
}
