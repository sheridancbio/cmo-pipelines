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

package org.mskcc.cmo.ks.redcap.source.internal;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.io.*;
import java.net.*;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import org.apache.log4j.Logger;
import org.mskcc.cmo.ks.redcap.models.ProjectInfoResponse;
import org.mskcc.cmo.ks.redcap.models.RedcapAttributeMetadata;
import org.mskcc.cmo.ks.redcap.models.RedcapProjectAttribute;
import org.mskcc.cmo.ks.redcap.models.RedcapToken;
import org.mskcc.cmo.ks.redcap.source.ClinicalDataSource;
import org.springframework.beans.factory.annotation.*;
import org.springframework.http.*;
import org.springframework.stereotype.Repository;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.RestTemplate;

@Repository
public class RedcapRepository {

    @Autowired
    private MetadataCache metadataCache;

    @Autowired
    private RedcapSessionManager redcapSessionManager;

    private final Logger log = Logger.getLogger(RedcapRepository.class);

/*
    public List<RedcapAttributeMetadata> getMetadata() {
        String metadataToken = redcapSessionManager.getMetadataToken();
        return Arrays.asList(redcapSessionManager.getRedcapMetadataByToken(metadataToken));
    }
*/

    public List<RedcapProjectAttribute> getAttributesByToken(String projectToken) {
        return Arrays.asList(redcapSessionManager.getRedcapAttribteByToken(projectToken));
    }

    public List<Map<String, String>> getRedcapDataForProject(String projectToken) {
        JsonNode[] redcapDataRecords = redcapSessionManager.getRedcapDataForProjectByToken(projectToken);
        List<Map<String, String>> redcapDataForProject = new ArrayList<>();
        for (JsonNode redcapResponse : redcapDataRecords) {
            Map<String, String> redcapDataRecord = new HashMap<>();
            Iterator<Map.Entry<String, JsonNode>> redcapNodeIterator = redcapResponse.fields();
            while (redcapNodeIterator.hasNext()) {
                Map.Entry<String, JsonNode> entry = (Map.Entry<String, JsonNode>)redcapNodeIterator.next();
                String redcapId = entry.getKey();
                RedcapAttributeMetadata metadata = metadataCache.getMetadataByRedcapId(redcapId);
                if (metadata == null) {
                    continue;
/*
                    //TODO: add a properties file property for "redcap-fields-to-be-ignored", and throw an error when a field is not on the ignore list and has no metadata
                    //example field to ignore : my_first_instrument_complete
                    String errorString = "Error : attempt to export data from RedCap failed due to redcap_id " +
                            redcapId + " not having metadata defined in the RedCap Metadata Project";
                    log.warn(errorString);
                    throw new RuntimeException(errorString);
*/
                }
                redcapDataRecord.put(metadata.getNormalizedColumnHeader(), entry.getValue().asText());
            }
            redcapDataForProject.add(redcapDataRecord);
        }
        return redcapDataForProject;
    }

}
