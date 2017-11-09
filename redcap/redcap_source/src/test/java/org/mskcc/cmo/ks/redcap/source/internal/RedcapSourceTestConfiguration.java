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
import java.util.*;
import org.mockito.Matchers;
import org.mockito.Mockito;
import org.mskcc.cmo.ks.redcap.models.RedcapAttributeMetadata;
import org.mskcc.cmo.ks.redcap.models.RedcapProjectAttribute;
import org.mskcc.cmo.ks.redcap.source.ClinicalDataSource;
import org.mskcc.cmo.ks.redcap.source.internal.ClinicalDataSourceRedcapImpl;
import org.mskcc.cmo.ks.redcap.source.internal.RedcapSourceTestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RedcapSourceTestConfiguration {
    public static final String ONE_DIGIT_PROJECT_TITLE = "OneDigitProjectTitle";
    public static final String ONE_DIGIT_PROJECT_TOKEN = "OneDigitProjectToken";
    public static final String METADATA_TOKEN = "MetadataToken";
    public static final String SIMPLE_MIXED_TYPE_CLINICAL_PROJECT_TITLE = "MixedClinicalProjectTitle";
    public static final String SIMPLE_MIXED_TYPE_CLINICAL_PROJECT_TOKEN = "MixedClinicalProjectToken";
    public static final String SIMPLE_MIXED_TYPE_CLINICAL_PROJECT_INSTRUMENT_NAME = "my_first_instrument";
    public static final String SIMPLE_MIXED_TYPE_CLINICAL_STABLE_ID = "MixedClinicalProjectStableId";

    @Bean
    public ClinicalDataSource clinicalDataSource() {
        return new ClinicalDataSourceRedcapImpl();
    }

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
        //configure token requests
        Mockito.when(redcapSessionManager.getTokenByProjectTitle(ONE_DIGIT_PROJECT_TITLE)).thenReturn(ONE_DIGIT_PROJECT_TOKEN);
        Mockito.when(redcapSessionManager.getTokenByProjectTitle(SIMPLE_MIXED_TYPE_CLINICAL_PROJECT_TITLE)).thenReturn(SIMPLE_MIXED_TYPE_CLINICAL_PROJECT_TOKEN);
        Map<String, String> mockReturnTimelineTokenMap = makeMockTimelineTokenMap();
        Map<String, String> mockReturnClinicalTokenMap = makeMockClinicalTokenMap();
        Mockito.when(redcapSessionManager.getTimelineTokenMapByStableId(SIMPLE_MIXED_TYPE_CLINICAL_STABLE_ID)).thenReturn(mockReturnTimelineTokenMap);
        Mockito.when(redcapSessionManager.getClinicalTokenMapByStableId(SIMPLE_MIXED_TYPE_CLINICAL_STABLE_ID)).thenReturn(mockReturnClinicalTokenMap);
        //configure meta data requests
        RedcapAttributeMetadata[] mockReturnForGetMetadata = makeMockRedcapIdToMetadataList();
        Mockito.when(redcapSessionManager.getRedcapMetadataByToken(Matchers.eq(METADATA_TOKEN))).thenReturn(mockReturnForGetMetadata);
        JsonNode[] mockReturnForGetData = makeMockReturnForGetData();
        //configure data requests
        Mockito.when(redcapSessionManager.getRedcapDataForProjectByToken(Matchers.eq(ONE_DIGIT_PROJECT_TOKEN))).thenReturn(mockReturnForGetData);
        RedcapProjectAttribute[] mockReturnForGetAttributesData = makeMockReturnForGetAttributesData();
        //Mockito.when(redcapSessionManager.getRedcapAttributeByToken(SIMPLE_MIXED_TYPE_CLINICAL_PROJECT_TOKEN)).thenReturn(mockReturnForGetAttributesData);
        Mockito.when(redcapSessionManager.getRedcapAttributeByToken(Matchers.any(String.class))).thenReturn(mockReturnForGetAttributesData);
        Mockito.when(redcapSessionManager.getMetadataToken()).thenReturn(METADATA_TOKEN);
        Mockito.when(redcapSessionManager.getRedcapInstrumentNameByToken(SIMPLE_MIXED_TYPE_CLINICAL_PROJECT_TOKEN)).thenReturn(SIMPLE_MIXED_TYPE_CLINICAL_PROJECT_INSTRUMENT_NAME);
        return redcapSessionManager;
    }

    @Bean
    public RedcapRepositoryTest redcapRepositoryTest() {
        return new RedcapRepositoryTest();
    }

    private RedcapAttributeMetadata[] makeMockRedcapIdToMetadataList() {
        RedcapAttributeMetadata[] metadata = new RedcapAttributeMetadata[6];
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
        //Added the following for getSampleHeader() / getPatientHeader() tests
        metadata[3] = new RedcapAttributeMetadata();
        metadata[3].setRecordId(4L);
        metadata[3].setNormalizedColumnHeader("SAMPLE_ID");
        metadata[3].setExternalColumnHeader("SAMPLE_ID");
        metadata[3].setRedcapId("sample_id");
        metadata[3].setAttributeType("SAMPLE");
        metadata[3].setPriority("1");
        metadata[3].setDisplayName("Sample Id");
        metadata[3].setDatatype("STRING");
        metadata[3].setDescriptions("This identifies a sample");
        metadata[4] = new RedcapAttributeMetadata();
        metadata[4].setRecordId(5L);
        metadata[4].setNormalizedColumnHeader("NECROSIS");
        metadata[4].setExternalColumnHeader("NECROSIS");
        metadata[4].setRedcapId("necrosis");
        metadata[4].setAttributeType("SAMPLE");
        metadata[4].setPriority("1");
        metadata[4].setDisplayName("Necrosis");
        metadata[4].setDatatype("STRING");
        metadata[4].setDescriptions("State of tissue");
        metadata[5] = new RedcapAttributeMetadata();
        metadata[5].setRecordId(6L);
        metadata[5].setNormalizedColumnHeader("ETHNICITY");
        metadata[5].setExternalColumnHeader("ETHNICITY");
        metadata[5].setRedcapId("ethnicity");
        metadata[5].setAttributeType("PATIENT");
        metadata[5].setPriority("1");
        metadata[5].setDisplayName("Ethnicity");
        metadata[5].setDatatype("STRING");
        metadata[5].setDescriptions("Patient Ethnicity");
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

    private RedcapProjectAttribute[] makeMockReturnForGetAttributesData() {
        RedcapProjectAttribute[] attributeArray = new RedcapProjectAttribute[5];
        RedcapProjectAttribute patientIdAttribute = new RedcapProjectAttribute();
        patientIdAttribute.setFieldName("patient_id");
        patientIdAttribute.setFormName("my_first_instrument");
        attributeArray[0] = patientIdAttribute;
        RedcapProjectAttribute sampleIdAttribute = new RedcapProjectAttribute();
        sampleIdAttribute.setFieldName("sample_id");
        sampleIdAttribute.setFormName("my_first_instrument");
        attributeArray[1] = sampleIdAttribute;
        RedcapProjectAttribute necrosisAttribute = new RedcapProjectAttribute();
        necrosisAttribute.setFieldName("necrosis");
        necrosisAttribute.setFormName("my_first_instrument");
        attributeArray[2] = necrosisAttribute;
        RedcapProjectAttribute ethnicityAttribute = new RedcapProjectAttribute();
        ethnicityAttribute.setFieldName("ethnicity");
        ethnicityAttribute.setFormName("my_first_instrument");
        attributeArray[3] = ethnicityAttribute;
        RedcapProjectAttribute myFirstInstrumentCompleteAttribute = new RedcapProjectAttribute();
        myFirstInstrumentCompleteAttribute.setFieldName("my_first_instrument_complete");
        myFirstInstrumentCompleteAttribute.setFormName("my_first_instrument");
        attributeArray[4] = myFirstInstrumentCompleteAttribute;
        return attributeArray;
    }

    private Map<String, String> makeMockClinicalTokenMap() {
        Map<String, String> returnMap = new HashMap<>();
        returnMap.put(SIMPLE_MIXED_TYPE_CLINICAL_PROJECT_TITLE, SIMPLE_MIXED_TYPE_CLINICAL_PROJECT_TOKEN);
        return returnMap;
    }

    private Map<String, String> makeMockTimelineTokenMap() {
        Map<String, String> returnMap = new HashMap<>();
        return returnMap;
    }

}
