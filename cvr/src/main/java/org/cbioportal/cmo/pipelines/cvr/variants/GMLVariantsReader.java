/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cbioportal.cmo.pipelines.cvr.variants;

import java.util.*;
import org.cbioportal.cmo.pipelines.cvr.model.*;
import org.springframework.batch.item.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.RestTemplate;
/**
 *
 * @author jake
 */
public class GMLVariantsReader implements ItemStreamReader<GMLVariant>{
    
    @Value("#{jobParameters[sessionId]}")
    private String sessionId;
    
    @Value("${dmp.gml_server_name}")
    private String dmpServerName;
    
    @Value("${dmp.tokens.retrieve_gml_variants}")
    private String dmpRetreiveVariants;
    
    private String dmpUrl;
    private List<GMLVariant> gmlVariants = new ArrayList<>();
    
    @Override
    public void open(ExecutionContext ec) throws ItemStreamException{
        dmpUrl = dmpServerName + dmpRetreiveVariants + "/" + sessionId + "/0";
        RestTemplate restTemplate = new RestTemplate();
        HttpEntity<LinkedMultiValueMap<String, Object>> requestEntity = getRequestEntity();
        ResponseEntity<GMLVariant> responseEntity = restTemplate.exchange(dmpUrl, HttpMethod.GET, requestEntity, GMLVariant.class);
        gmlVariants.add(responseEntity.getBody());
    }
    
    @Override
    public void update(ExecutionContext ec) throws ItemStreamException{}
    
    @Override
    public void close() throws ItemStreamException{}
    
    @Override
    public GMLVariant read() throws Exception{
        if(!gmlVariants.isEmpty()){
            return gmlVariants.remove(0);
        }
        return null;
    }
    
    private HttpEntity getRequestEntity(){
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        return new HttpEntity<>(headers);
    }
    
}
