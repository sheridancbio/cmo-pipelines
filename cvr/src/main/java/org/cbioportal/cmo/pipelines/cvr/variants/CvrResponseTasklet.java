/*
 * Copyright (c) 2017 Memorial Sloan-Kettering Cancer Center.
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

package org.cbioportal.cmo.pipelines.cvr.variants;

import org.cbioportal.cmo.pipelines.cvr.model.CvrResponse;

import java.util.Map;
import javax.annotation.Resource;
import org.cbioportal.cmo.pipelines.cvr.CvrSampleListUtil;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.RestTemplate;

/**
 *
 * @author ochoaa
 */
public class CvrResponseTasklet implements Tasklet {
    
    @Value("#{jobParameters[sessionId]}")
    private String sessionId;
    
    @Value("#{jobParameters[studyId]}")
    private String studyId;
    
    @Value("${dmp.server_name}")
    private String dmpServerName;
    
    @Autowired
    public CvrSampleListUtil cvrSampleListUtil;
    
    @Resource(name="retrieveVariantTokensMap")
    private Map<String, String> retrieveVariantTokensMap;
    
    @Override
    public RepeatStatus execute(StepContribution sc, ChunkContext cc) throws Exception {
        // get retrieve variants token by study id
        String dmpUrl = dmpServerName + retrieveVariantTokensMap.get(studyId) + "/" + sessionId + "/0";
        RestTemplate restTemplate = new RestTemplate();
        HttpEntity<LinkedMultiValueMap<String, Object>> requestEntity = getRequestEntity();
        ResponseEntity<CvrResponse> responseEntity = restTemplate.exchange(dmpUrl, HttpMethod.GET, requestEntity, CvrResponse.class);
        CvrResponse cvrResponse = responseEntity.getBody();
        // save the CVR response in the sample util and add the sample count to the execution context
        // for the CVR response job execution decider 
        cvrSampleListUtil.setCvrResponse(cvrResponse);
        cc.getStepContext().getStepExecution().getJobExecution().getExecutionContext().put("sampleCount", cvrResponse.getSampleCount());
        return RepeatStatus.FINISHED;
    }
    
    private HttpEntity getRequestEntity() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        return new HttpEntity<Object>(headers);
    }
    
}
