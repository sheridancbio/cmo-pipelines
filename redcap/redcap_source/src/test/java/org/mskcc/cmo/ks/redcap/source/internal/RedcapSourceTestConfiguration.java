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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
*/

package org.mskcc.cmo.ks.redcap.source.internal;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.*;
import java.net.*;
import java.util.*;
import org.apache.commons.text.StringEscapeUtils;
import org.apache.log4j.Logger;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.Matchers;
import org.mskcc.cmo.ks.redcap.models.RedcapAttributeMetadata;
import org.mskcc.cmo.ks.redcap.models.RedcapProjectAttribute;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.mskcc.cmo.ks.redcap.source.ClinicalDataSource;
import org.mskcc.cmo.ks.redcap.source.internal.ClinicalDataSourceRedcapImpl;
import org.mskcc.cmo.ks.redcap.source.internal.RedcapSourceTestConfiguration;
import org.springframework.beans.factory.annotation.*;
import org.springframework.http.*;
import org.springframework.stereotype.Repository;
import org.springframework.util.LinkedMultiValueMap;
import org.junit.Assert;
import org.springframework.context.annotation.*;

@Configuration
//@ComponentScan("org.mskcc.cmo.ks.redcap.source.internal")
public class RedcapSourceTestConfiguration {
    public static final String ONE_DIGIT_PROJECT_ID_TOKEN = "OneDigitProjectIdToken";
    public static final String METADATA_TOKEN = "MetadataToken";

    @Bean
    public RedcapRepository redcapRepository() {
        return new RedcapRepository();
    }

    @Bean
    public MetadataCache metadataCache() {
        return new MetadataCache();
    }

    @Bean
    public RedcapSessionManager redcapSessionManager() {
        RedcapSessionManager redcapSessionManager = Mockito.mock(RedcapSessionManager.class);
        RedcapAttributeMetadata[] mockReturnForGetMetadata = makeMockRedcapIdToMetadataList();
        Mockito.when(redcapSessionManager.getRedcapMetadataByToken(Matchers.eq(METADATA_TOKEN))).thenReturn(mockReturnForGetMetadata);
        JsonNode[] mockReturnForGetData = makeMockReturnForGetData();
        Mockito.when(redcapSessionManager.getRedcapDataForProjectByToken(Matchers.eq(ONE_DIGIT_PROJECT_ID_TOKEN))).thenReturn(mockReturnForGetData);
        String tokenToReturn = ONE_DIGIT_PROJECT_ID_TOKEN;
        Mockito.when(redcapSessionManager.getTokenByProjectTitle("oneDigitProjectId")).thenReturn(tokenToReturn);
        Mockito.when(redcapSessionManager.getMetadataToken()).thenReturn(METADATA_TOKEN);
        return redcapSessionManager;
    }

    @Bean
    public RedcapRepositoryTest redcapRepositoryTest() {
        return new RedcapRepositoryTest();
    }

    private RedcapAttributeMetadata[] makeMockRedcapIdToMetadataList() {
        RedcapAttributeMetadata[] metadata = new RedcapAttributeMetadata[3];
        metadata[0] = new RedcapAttributeMetadata();
        metadata[0].setRecordId(1L);
        metadata[0].setNormalizedColumnHeader("PATIENT_ID");
        metadata[0].setExternalColumnHeader("PATIENT_ID");
        metadata[0].setRedcapId("patient_id");
        metadata[0].setAttributeType("PATIENT");
        metadata[0].setPriority("1");
        metadata[0].setDisplayName("Patient Id");
        metadata[0].setDatatype("STRING");
        metadata[0].setDescriptions("This identifies a patient");
        metadata[1] = new RedcapAttributeMetadata();
        metadata[1].setRecordId(2L);
        metadata[1].setNormalizedColumnHeader("CRDB_CONSENT_DATE_DAYS");
        metadata[1].setExternalColumnHeader("CRDB_CONSENT_DATE_DAYS");
        metadata[1].setRedcapId("crdb_consent_date_days");
        metadata[1].setAttributeType("PATIENT");
        metadata[1].setPriority("1");
        metadata[1].setDisplayName("crdb consent date days");
        metadata[1].setDatatype("STRING");
        metadata[1].setDescriptions("days since consent");
        metadata[2] = new RedcapAttributeMetadata();
        metadata[2].setRecordId(3L);
        metadata[2].setNormalizedColumnHeader("12_245_PARTA_CONSENTED");
        metadata[2].setExternalColumnHeader("12_245_PARTA_CONSENTED");
        metadata[2].setRedcapId("parta_consented_12_245");
        metadata[2].setAttributeType("PATIENT");
        metadata[2].setPriority("1");
        metadata[2].setDisplayName("12 245 PARTA CONSENTED");
        metadata[2].setDatatype("STRING");
        metadata[2].setDescriptions("Flag showing if patient has consented to 12 245 part a");
        return metadata;
    }

    private JsonNode[] makeMockReturnForGetData() {
        String mockReturnJsonString =
                "[" + 
                    "{\"patient_id\":\"P-0000004\",\"crdb_consent_date_days\":\"14484\",\"parta_consented_12_245\":\"YES\"}," +
                    "{\"patient_id\":\"P-0000012\",\"crdb_consent_date_days\":\"21192\",\"parta_consented_12_245\":\"YES\"}," +
                    "{\"patient_id\":\"P-9999999\",\"crdb_consent_date_days\":\"99999\",\"parta_consented_12_245\":\"\"}" +
                "]";
        ObjectMapper mapper = new ObjectMapper();
        List<JsonNode> jsonNodeList = new ArrayList<JsonNode>();
        try {
            Iterator<JsonNode> nodeIterator = mapper.readTree(mockReturnJsonString).elements();
            while (nodeIterator.hasNext()) {
                jsonNodeList.add(nodeIterator.next());
            }
            return jsonNodeList.toArray(new JsonNode[jsonNodeList.size()]);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
