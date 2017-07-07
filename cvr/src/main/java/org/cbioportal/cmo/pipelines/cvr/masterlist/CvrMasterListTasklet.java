/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cbioportal.cmo.pipelines.cvr.masterlist;

import org.cbioportal.cmo.pipelines.cvr.CvrSampleListUtil;
import org.cbioportal.cmo.pipelines.cvr.model.CVRMasterList;

import org.apache.log4j.Logger;
import java.util.*;
import javax.annotation.Resource;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.*;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

/**
 *
 * @author ochoaa
 */
public class CvrMasterListTasklet implements Tasklet {

    @Autowired
    public CvrSampleListUtil cvrSampleListUtil;
    
    @Value("#{jobParameters[sessionId]}")
    private String sessionId;
    
    @Value("${dmp.server_name}")
    private String dmpServerName;
    
    @Value("${dmp.tokens.retrieve_master_list.route}")
    private String dmpRetrieveMasterListRoute;
    
    @Value("#{jobParameters[studyId]}")
    private String studyId;
    
    @Value("#{jobParameters[maxNumSamplesToRemove]}")
    private Integer maxNumSamplesToRemove;
    
    @Resource(name="masterListTokensMap")
    private Map<String, String> masterListTokensMap;

    Logger log = Logger.getLogger(CvrMasterListTasklet.class);
    
    @Override
    public RepeatStatus execute(StepContribution sc, ChunkContext cc) throws Exception {
        log.info("Loading master list from CVR for study: " + studyId);
        Set<String> dmpMasterList = new HashSet<>();
        try {
            dmpMasterList = generateDmpMasterList();
            log.info("DMP master list for " + studyId + " contains " + String.valueOf(dmpMasterList.size()) + " samples");
        }
        catch (HttpClientErrorException e) {
            log.warn("Error occurred while retrieving master list for " + studyId + " - the default master list will be set to samples already in portal.\n" 
                    + e.getLocalizedMessage());
        }
        cvrSampleListUtil.setDmpMasterList(dmpMasterList);
        cvrSampleListUtil.setMaxNumSamplesToRemove(maxNumSamplesToRemove);
        return RepeatStatus.FINISHED;
    }

    private Set<String> generateDmpMasterList() {
        String dmpUrl = dmpServerName + dmpRetrieveMasterListRoute + "/" + sessionId + "/" + masterListTokensMap.get(studyId);
        RestTemplate restTemplate = new RestTemplate();
        HttpEntity<LinkedMultiValueMap<String, Object>> requestEntity = getRequestEntity();
        ResponseEntity<CVRMasterList> responseEntity = restTemplate.exchange(dmpUrl, HttpMethod.GET, requestEntity, CVRMasterList.class);
        
        Set<String> dmpSamples = new HashSet<>();
        for (Map<String, String> samples : responseEntity.getBody().getSamples()) {
            // there is only one pair per Map
            if (samples.entrySet().iterator().hasNext()) {
                Map.Entry pair = (Map.Entry) samples.entrySet().iterator().next();
                String sample = (String) pair.getValue();
                log.debug("open(): sample '" + sample + "' in master list");
                dmpSamples.add(sample);
            }
        }
        return dmpSamples;
    }

    private HttpEntity getRequestEntity() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        return new HttpEntity<Object>(headers);
    }
    
}
