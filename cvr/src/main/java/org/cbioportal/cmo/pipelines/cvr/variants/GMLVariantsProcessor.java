/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cbioportal.cmo.pipelines.cvr.variants;

import org.cbioportal.cmo.pipelines.cvr.model.*;

import java.util.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.*;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.apache.log4j.Logger;
import org.cbioportal.cmo.pipelines.cvr.CVRUtilities;
/**
 *
 * @author jake
 */
public class GMLVariantsProcessor implements ItemProcessor<GMLVariant, String>{
    
    @Value("${dmp.server_name}")
    private String dmpServerName;
    
    @Value("${dmp.tokens.consume_gml_sample}")
    private String dmpConsumeSample;
    
    @Value("#{jobParameters[sessionId]}")
    private String sessionId;
    
    Logger log = Logger.getLogger(GMLVariantsProcessor.class);
    
    @Autowired
    public CVRUtilities cvrUtilities;
    
    private String gmlSegmentUrl;
    private String gmlConsumeUrl;
    
    private GMLData gmlData;
    
    HttpEntity<LinkedMultiValueMap<String, Object>> requestEntity = getRequestEntity();
    
    @Override
    public String process(GMLVariant g) throws Exception{
        gmlConsumeUrl = dmpServerName = dmpConsumeSample + "/" + sessionId + "/";
        HashMap<String, GMLResult> results = g.getResults();
        gmlData = new GMLData(g.getSampleCount(), g.getDisclaimer(), new ArrayList<GMLResult>());
        ObjectMapper mapper = new ObjectMapper();
        
        for (Map.Entry<String, GMLResult> pair : results.entrySet()) {
            GMLResult result = pair.getValue();
            String patientId = result.getMetaData().getDmpPatientId();
            cvrUtilities.addNewId(patientId);
            String sampleId = pair.getKey();
            //consumeSample(sampleId);
            gmlData.addResult(result);
        }
        
        return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(gmlData);
    }
    
    private HttpEntity getRequestEntity() {  
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        return new HttpEntity<>(headers);
    }
    
    private void consumeSample(String sampleId){
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<CVRConsumeSample> responseEntity;
        try{
            responseEntity = restTemplate.exchange(gmlConsumeUrl + sampleId, HttpMethod.GET, requestEntity, CVRConsumeSample.class);
            if(responseEntity.getBody().getaffectedRows()==0){
                String message = "No consumption for sample " + sampleId;
                log.warn(message);
            }
            if(responseEntity.getBody().getaffectedRows()>1){
                String message = "Multple samples consumed (" + responseEntity.getBody().getaffectedRows() + ") for sample " + sampleId;
                log.warn(message);
            }
            if(responseEntity.getBody().getaffectedRows()==1){
                String message = "Sample "+ sampleId +" consumed succesfully";
                log.info(message);
            }
        }
        catch(org.springframework.web.client.RestClientException e){
            log.error("Error consuming sample " + sampleId + "(" + e + ")");
        }
        
    }
}
