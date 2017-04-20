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

package org.cbioportal.cmo.pipelines.cvr.masterlist;

import java.util.*;
import org.apache.log4j.Logger;
import org.cbioportal.cmo.pipelines.cvr.CVRUtilities;
import org.cbioportal.cmo.pipelines.cvr.model.CVRMasterList;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemStreamException;
import org.springframework.batch.item.ItemStreamReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.RestTemplate;

/**
 *
 * @author Manda Wilson
 */

public class CVRMasterListReader implements ItemStreamReader<String> {

    @Value("#{jobParameters[sessionId]}")
    private String sessionId;
    
    @Value("${dmp.server_name}")
    private String dmpServerName;
    
    @Value("${dmp.tokens.retrieve_master_list.impact}")
    private String dmpMasterList;

    @Autowired
    private CVRUtilities cvrUtilities;

    Logger log = Logger.getLogger(CVRMasterListReader.class);

    private List<String> dmpSamplesNotInPortal = null;
    private List<String> portalSamplesNotInDmp = null;

    // Calls get_cbio_signedout_samples against CVR web service
    @Override
    public void open(ExecutionContext ec) throws ItemStreamException {
        // get master list of samples
        String dmpUrl = dmpServerName + dmpMasterList + "/" + sessionId + "/";
        RestTemplate restTemplate = new RestTemplate();
        HttpEntity<LinkedMultiValueMap<String, Object>> requestEntity = getRequestEntity();
        ResponseEntity<CVRMasterList> responseEntity = restTemplate.exchange(dmpUrl, HttpMethod.GET, requestEntity, CVRMasterList.class);
        Set<String> dmpSamples = new HashSet<String>();
        for (Map<String, String> samples : responseEntity.getBody().getSamples()) {
            // there is only one pair per Map
            if (samples.entrySet().iterator().hasNext()) {
                Map.Entry pair = (Map.Entry) samples.entrySet().iterator().next();
                String sample = (String) pair.getValue();
                log.debug("open(): sample '" + sample + "' in master list");
                dmpSamples.add(sample);
            }
        }

        Set<String> portalSamples = cvrUtilities.getAllIds();

        // get dmp samples not in portal
        // these we will process
        Set<String> dmpSamplesNotInPortalSet = new HashSet<String>(dmpSamples);
        dmpSamplesNotInPortalSet.removeAll(portalSamples);
        dmpSamplesNotInPortal = new ArrayList(dmpSamplesNotInPortalSet);

        // get portal samples not in dmp master list
        // these we will save to execution context
        Set<String> portalSamplesNotInDmpSet = new HashSet<String>(portalSamples); 
        portalSamplesNotInDmpSet.removeAll(dmpSamples);
        portalSamplesNotInDmp = new ArrayList(portalSamplesNotInDmpSet);

    }

    @Override
    public void update(ExecutionContext ec) throws ItemStreamException {
        ec.put("portalSamplesNotInDmp", portalSamplesNotInDmp); 
    }

    @Override
    public void close() throws ItemStreamException {
    }

    @Override
    public String read() throws Exception {
        if (!dmpSamplesNotInPortal.isEmpty()) {
            return dmpSamplesNotInPortal.remove(0);
        }
        return null;
    }

    private HttpEntity getRequestEntity() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        return new HttpEntity<Object>(headers);
    }
}
