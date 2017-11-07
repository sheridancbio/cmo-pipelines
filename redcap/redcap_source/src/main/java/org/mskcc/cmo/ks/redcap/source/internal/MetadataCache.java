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
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.RestTemplate;

@Component
public class MetadataCache {

    @Autowired
    private RedcapSessionManager redcapSessionManager;

    private RedcapAttributeMetadata[] metadataArray = null;
    private Map<String, RedcapAttributeMetadata> externalColumnHeaderToMetadata = null;
    private Map<String, RedcapAttributeMetadata> redcapIdToMetadata = null;

    private final Logger log = Logger.getLogger(MetadataCache.class);

    public MetadataCache() {}

    public RedcapAttributeMetadata getMetadataByRedcapId(String redcapId) {
        ensureThatCacheIsInitialized();
        return redcapIdToMetadata.get(redcapId);
    }

    public RedcapAttributeMetadata getMetadataByExternalColumnHeader(String externalColumnHeader) {
        ensureThatCacheIsInitialized();
        return externalColumnHeaderToMetadata.get(externalColumnHeader);
    }

    public List<RedcapAttributeMetadata> getMetadata () {
        ensureThatCacheIsInitialized();
        return Arrays.asList(metadataArray);
    }

    private void ensureThatCacheIsInitialized() {
        if (externalColumnHeaderToMetadata != null && redcapIdToMetadata != null && metadataArray != null) {
            return; // already initialized
        }
        initializeMetadataList();
        initializeMetadataMaps();
    }

    private void initializeMetadataList() {
        String metadataToken = redcapSessionManager.getMetadataToken(); //TODO: get the token from a RedcapTokenCache class
        metadataArray = redcapSessionManager.getRedcapMetadataByToken(metadataToken);
    }

    private void initializeMetadataMaps() {
        if (metadataArray != null) {
            initializeMetadataList();
        }
        externalColumnHeaderToMetadata = new HashMap<String, RedcapAttributeMetadata>(metadataArray.length);
        redcapIdToMetadata = new HashMap<String, RedcapAttributeMetadata>(metadataArray.length);
        for (RedcapAttributeMetadata metadataElement : metadataArray) {
            addToExternalColumnHeaderMap(metadataElement);
            addToRedcapIdMap(metadataElement);
        }
    }

    private void addToExternalColumnHeaderMap(RedcapAttributeMetadata metadataElement) {
        String externalColumnHeader = metadataElement.getExternalColumnHeader();
        if (externalColumnHeader == null) {
            String errorString = "Error : missing value in EXTERNAL_COLUMN_HEADER filed in Redcap Metadata project";
            log.warn(errorString);
            throw new RuntimeException(errorString);
        }
        if (externalColumnHeaderToMetadata.put(externalColumnHeader, metadataElement) != null) {
            log.warn("overwrote externalColumnHeader '" + externalColumnHeader +
                    "' with new value in externalColumnHeaderToMetadata map (externalColumnHeader was duplicated in redcap metadata project)");
        }
    }

    private void addToRedcapIdMap(RedcapAttributeMetadata metadataElement) {
        String redcapId = metadataElement.getRedcapId();
        if (redcapId == null) {
            String errorString = "Error : missing value in redcap_id field in Redcap Metadata project";
            log.warn(errorString);
            throw new RuntimeException(errorString);
        }
        if (redcapIdToMetadata.put(redcapId, metadataElement) != null) {
            log.warn("overwrote redcapId '" + redcapId +
                    "' with new value in redcapIdToMetadata map (redcapId was duplicated in redcap metadata project)");
        }
    }
}
