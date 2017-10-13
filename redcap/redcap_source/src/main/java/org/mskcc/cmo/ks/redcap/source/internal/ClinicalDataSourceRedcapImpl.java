/*
 * Copyright (c) 2016 - 2017 Memorial Sloan-Kettering Cancer Center.
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
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.io.*;
import java.net.*;
import java.util.*;
import org.apache.commons.text.StringEscapeUtils;
import org.apache.log4j.Logger;
import org.mskcc.cmo.ks.redcap.models.RedcapAttributeMetadata;
import org.mskcc.cmo.ks.redcap.models.RedcapProjectAttribute;
import org.mskcc.cmo.ks.redcap.source.ClinicalDataSource;
import org.springframework.beans.factory.annotation.*;
import org.springframework.http.*;
import org.springframework.stereotype.Repository;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.RestTemplate;

/**
 *
 * @author Zachary Heins
 *
 * Use Redcap to fetch clinical metadata and data
 *
 */
@Repository
public class ClinicalDataSourceRedcapImpl implements ClinicalDataSource {

    @Autowired
    private RedcapSessionManager redcapSessionManager;

    private Map<String, String> clinicalDataTokens = null;
    private Map<String, String> clinicalTimelineTokens = null;
    private String metadataToken = null;
    private List<RedcapAttributeMetadata> metadata;

    private List<String> sampleHeader;
    private List<String> patientHeader;
    private List<String> combinedHeader;
    private Map<String, List<String>> fullPatientHeader = new HashMap<>();
    private Map<String, List<String>> fullSampleHeader = new HashMap<>();
    private String nextClinicalId;
    private String nextTimelineId;

    private static final Map<String, String> externalToRedcapFieldOverrideMap = initializeExternalToRedcapFieldOverrideMap();

    private final Logger log = Logger.getLogger(ClinicalDataSourceRedcapImpl.class);

    @Override
    public boolean projectExists(String projectTitle) {
        return redcapSessionManager.getTokenByProjectTitle(projectTitle) != null;
    }

    @Override
    public boolean redcapDataTypeIsTimeline(String projectTitle) {
        return redcapSessionManager.redcapDataTypeIsTimeline(projectTitle);
    }

    @Override
    public List<Map<String, String>> exportRawDataForProjectTitle(String projectTitle) {
        String projectToken = redcapSessionManager.getTokenByProjectTitle(projectTitle);
        List<Map<String, String>> data = getRedcapDataForProject(projectToken);
        if (redcapDataTypeIsTimeline(projectTitle)) {
            if (clinicalTimelineTokens != null) {
                clinicalTimelineTokens.remove(projectTitle);
            }
        } else {
            if (clinicalDataTokens != null) {
                clinicalDataTokens.remove(projectTitle);
            }
        }
        return data;
    }

    @Override
    public boolean projectsExistForStableId(String stableId) {
        return !redcapSessionManager.getClinicalTokenMapByStableId(stableId).isEmpty() ||
                !redcapSessionManager.getTimelineTokenMapByStableId(stableId).isEmpty();
    }

    @Override
    public List<String> getProjectHeader(String projectTitle) {
        String projectToken = redcapSessionManager.getTokenByProjectTitle(projectTitle);
        return getNormalizedColumnHeaders(projectToken);
    }

    @Override
    public List<String> getSampleHeader(String stableId) {
        checkTokensByStableId(stableId);
        getClinicalHeaderData();
        return sampleHeader;
    }

    @Override
    public List<String> getPatientHeader(String stableId) {
        checkTokensByStableId(stableId);
        getClinicalHeaderData();
        return patientHeader;
    }

    @Override
    public List<String> getTimelineHeader(String stableId) {
        checkTokensByStableId(stableId);
        getTimelineHeaderData();
        return combinedHeader;
    }

    @Override
    public List<Map<String, String>> getClinicalData(String stableId) {
        checkTokensByStableId(stableId);
        String projectToken = clinicalDataTokens.remove(nextClinicalId);
        return getRedcapDataForProject(projectToken);
    }

    @Override
    public List<Map<String, String>> getTimelineData(String stableId) {
        checkTokensByStableId(stableId);
        String projectToken = clinicalTimelineTokens.remove(nextTimelineId);
        return getRedcapDataForProject(projectToken);
    }

    @Override
    public String getNextClinicalProjectTitle(String stableId) {
        checkTokensByStableId(stableId);
        List<String> keys = new ArrayList(clinicalDataTokens.keySet());
        nextClinicalId = keys.get(0);
        return nextClinicalId;
    }

    @Override
    public String getNextTimelineProjectTitle(String stableId) {
        checkTokensByStableId(stableId);
        List<String> keys = new ArrayList(clinicalTimelineTokens.keySet());
        nextTimelineId = keys.get(0);
        return nextTimelineId;
    }

    @Override
    public boolean hasMoreTimelineData(String stableId) {
        checkTokensByStableId(stableId);
        return !clinicalTimelineTokens.isEmpty();
    }

    @Override
    public boolean hasMoreClinicalData(String stableId) {
        checkTokensByStableId(stableId);
        return !clinicalDataTokens.isEmpty();
    }

    @Override
    public void importClinicalDataFile(String projectTitle, String filename, boolean overwriteProjectData) {
        String projectToken = redcapSessionManager.getTokenByProjectTitle(projectTitle);
        if (projectToken == null) {
            log.error("Project not found in redcap clinicalDataTokens or clincalTimelineTokens: " + projectTitle);
            return;
        }
        try {
            File file = new File(filename);
            if (!file.exists()) {
                log.error("error : could not find file " + filename);
                return;
            }
            List<String> dataFileContentsTSV = readClinicalFile(file);
            if (dataFileContentsTSV.size() == 0) {
                log.error("error: file " + filename + " was empty ... aborting attempt to import data");
                return;
            }
            if (overwriteProjectData) {
                redcapSessionManager.deleteRedcapProjectData(projectToken);
            }
            replaceExternalHeadersWithRedcapIds(dataFileContentsTSV);
            addRecordIdColumnIfMissingInFileAndPresentInProject(dataFileContentsTSV, projectToken);
            List<String> dataFileContentsCSV = convertTSVtoCSV(dataFileContentsTSV, true);
            String dataForImport = String.join("\n",dataFileContentsCSV.toArray(new String[0])) + "\n";
            if (dataFileContentsCSV.size() == 1) {
                log.warn("file " + filename + " contained a single line (presumed to be the header). RedCap project has been cleared (now has 0 records).");
            } else {
                redcapSessionManager.importClinicalData(projectToken, dataForImport);
                log.info("import completed, " + Integer.toString(dataFileContentsCSV.size() - 1) + " records imported");
            }
        } catch (IOException e) {
            log.error("IOException thrown while attempting to read file " + filename + " : " + e.getMessage());
        }
    }

    private void replaceExternalHeadersWithRedcapIds(List<String> dataFileContentsTSV) {
        String[] externalHeaderFields = dataFileContentsTSV.get(0).split("\t",-1);
        String[] redcapHeaderIds = externalFieldNamesToRedcapFieldIds(externalHeaderFields);
        String newHeaderLine = String.join("\t", redcapHeaderIds);
        dataFileContentsTSV.set(0, newHeaderLine);
    }

    private void addRecordIdColumnIfMissingInFileAndPresentInProject(List<String> dataFileContentsTSV, String projectToken) {
        if (dataFileContentsTSV.get(0).startsWith(RedcapSessionManager.REDCAP_FIELD_NAME_FOR_RECORD_ID)) {
            return; // RECORD_ID field is already the first field in the file
        }
        Integer maximumRecordIdInProject = redcapSessionManager.getMaximumRecordIdInRedcapProjectIfPresent(projectToken);
        if (maximumRecordIdInProject == null) {
            return; // record_id field is not present in project
        }
        int nextRecordId = maximumRecordIdInProject + 1;
        boolean headerHandled = false;
        for (int index = 0; index < dataFileContentsTSV.size(); index++) {
            if (headerHandled) {
                String expandedLine = Integer.toString(nextRecordId) + "\t" + dataFileContentsTSV.get(index);
                dataFileContentsTSV.set(index, expandedLine);
                nextRecordId = nextRecordId + 1;
            } else {
                String expandedLine = RedcapSessionManager.REDCAP_FIELD_NAME_FOR_RECORD_ID + "\t" + dataFileContentsTSV.get(index);
                dataFileContentsTSV.set(index, expandedLine);
                headerHandled = true;
            }
        }
    }

    private List<String> getNormalizedColumnHeaders(String projectToken) {
        metadata = getMetadata();
        List<RedcapProjectAttribute> attributes = getAttributesByToken(projectToken);
        Map<RedcapProjectAttribute, RedcapAttributeMetadata> attributeMap = new LinkedHashMap<>();
        for (RedcapProjectAttribute attribute : attributes) {
            for (RedcapAttributeMetadata meta : metadata) {
                if (attribute.getFieldName().toUpperCase().equals(meta.getNormalizedColumnHeader().toUpperCase())) {
                    attributeMap.put(attribute, meta);
                    break;
                }
            }
        }
        return makeHeader(attributeMap);
    }

    private void getClinicalHeaderData() {
        metadata = getMetadata();
        List<RedcapProjectAttribute> attributes = getAttributes(false);

        Map<RedcapProjectAttribute, RedcapAttributeMetadata> sampleAttributeMap = new LinkedHashMap<>();
        Map<RedcapProjectAttribute, RedcapAttributeMetadata> patientAttributeMap = new LinkedHashMap<>();

        for (RedcapProjectAttribute attribute : attributes) {
            for (RedcapAttributeMetadata meta : metadata) {
                if (attribute.getFieldName().toUpperCase().equals(meta.getNormalizedColumnHeader().toUpperCase())) {
                    if(meta.getAttributeType().equals("SAMPLE")) {
                        sampleAttributeMap.put(attribute, meta);
                        break;
                    }
                    else {
                        patientAttributeMap.put(attribute, meta);
                        break;
                    }
                }
            }
        }
        sampleHeader = makeHeader(sampleAttributeMap);
        patientHeader = makeHeader(patientAttributeMap);
    }

    private void getTimelineHeaderData() {
        metadata = getMetadata();
        List<RedcapProjectAttribute> attributes = getAttributes(true);
        Map<RedcapProjectAttribute, RedcapAttributeMetadata> combinedAttributeMap = new LinkedHashMap<>();
         for (RedcapProjectAttribute attribute : attributes) {
            for (RedcapAttributeMetadata meta : metadata) {
                if (attribute.getFieldName().toUpperCase().equals(meta.getRedcapId().toUpperCase())) {
                    combinedAttributeMap.put(attribute, meta);
                }
            }
        }

         combinedHeader = makeHeader(combinedAttributeMap);
    }

    public static Map<String, String> initializeExternalToRedcapFieldOverrideMap() {
        Map<String, String> overrides = new HashMap<>();
        overrides.put("CRDB_BASIC_COMMENTS", "crdb_basic_comments"); //crdb_basic
        overrides.put("12_245_PARTA_CONSENTED", "parta_consented_12_245"); //crdb_basic
        overrides.put("CRDB_SURVEY_COMMENTS", "crdb_survey_comments"); //crdb_survey
        overrides.put("ARCHER", "archer"); //cvr
        overrides.put("SOURCE", "source"); //mskimpact_timeline_status_caisis
        overrides.put("SURGERY_DETAILS", "surgery_details"); //mskimpact_timeline_surgery_caisis
        overrides.put("EVENT_TYPE_DETAILED", "event_type_detailed"); //mskimpact_timeline_surgery_caisis
        overrides.put("SOURCE_PATHOLOGY", "source_pathology"); //mskimpact_timeline_surgery_caisis
        overrides.put("DIAGNOSTIC_TYPE", "diagnostic_type"); //mskimpact_timeline_imagery_caisis
        overrides.put("DIAGNOSTIC_TYPE_DETAILED", "diagnostic_type_detailed"); //mskimpact_timeline_imagery_caisis
        overrides.put("AGE_AT_DEATH", "age_at_death"); //mskimpact_data_clinical_darwin_demographics
        return overrides;
    }

    public String[] externalFieldNamesToRedcapFieldIds(String[] externalFieldNames) {
        if (externalFieldNames == null) {
            return new String[0];
        }
        String[] redcapFieldIds = new String[externalFieldNames.length];
        metadata = getMetadata();
        Map<String, String> externalToRedcapFieldMap = mapExternalToRedcapFieldFromMetadata(metadata);
        for (int i = 0; i < externalFieldNames.length; i++) {
            String redcapFieldId = externalToRedcapFieldOverrideMap.get(externalFieldNames[i]);
            if (redcapFieldId == null) {
                redcapFieldId = externalToRedcapFieldMap.get(externalFieldNames[i]);
            }
            if (redcapFieldId == null) {
                String errorString = "Error : attempt to persist file to RedCap failed due to field name " + externalFieldNames[i] + " not having a redcap_id defined in the RedCap Metadata Project";
                log.warn(errorString);
                throw new RuntimeException(errorString);
            }
            redcapFieldIds[i] = redcapFieldId;
        }
        return redcapFieldIds;
    }

    private Map<String, String> mapExternalToRedcapFieldFromMetadata(List<RedcapAttributeMetadata> metadata) {
        HashMap<String, String> externalToRedcapFieldnameMap = new HashMap<String, String>();
        for (RedcapAttributeMetadata metadataEntry : metadata) {
            String extColHeader = metadataEntry.getExternalColumnHeader();
            String redcapId = metadataEntry.getRedcapId();
            if (extColHeader == null) {
                String errorString = "Error : missing value in EXTERNAL_COLUMN_HEADER filed in Redcap Metadata project";
                log.warn(errorString);
                throw new RuntimeException(errorString);
            }
            if (redcapId == null) {
                String errorString = "Error : missing value in redcap_id field in Redcap Metadata project";
                log.warn(errorString);
                throw new RuntimeException(errorString);
            }
            externalToRedcapFieldnameMap.put(extColHeader, redcapId);
        }
        return externalToRedcapFieldnameMap;
    }

    private List<RedcapAttributeMetadata> getMetadata() {
        if (metadata != null) {
            return metadata;
        }
        RestTemplate restTemplate = new RestTemplate();

        log.info("Getting attribute metadatas...");

        LinkedMultiValueMap<String, String> uriVariables = new LinkedMultiValueMap<>();
        uriVariables.add("token", redcapSessionManager.getMetadataToken());
        uriVariables.add("content", "record");
        uriVariables.add("format", "json");
        uriVariables.add("type", "flat");

        HttpEntity<LinkedMultiValueMap<String, Object>> requestEntity = redcapSessionManager.getRequestEntity(uriVariables);
        ResponseEntity<RedcapAttributeMetadata[]> responseEntity = restTemplate.exchange(redcapSessionManager.getRedcapApiURI(), HttpMethod.POST, requestEntity, RedcapAttributeMetadata[].class);
        return Arrays.asList(responseEntity.getBody());
    }

    private List<RedcapProjectAttribute> getAttributes(boolean timelineData) {
        String projectToken;
        if(timelineData) {
            projectToken = clinicalTimelineTokens.get(nextTimelineId);
        }
        else {
            projectToken = clinicalDataTokens.get(nextClinicalId);
        }
        return getAttributesByToken(projectToken);
    }

    private List<RedcapProjectAttribute> getAttributesByToken(String projectToken) {
        LinkedMultiValueMap<String, String> uriVariables = new LinkedMultiValueMap<>();
        uriVariables.add("token", projectToken);
        uriVariables.add("content", "metadata");
        uriVariables.add("format", "json");
        uriVariables.add("type", "flat");

        RestTemplate restTemplate = new RestTemplate();
        HttpEntity<LinkedMultiValueMap<String, Object>> requestEntity = redcapSessionManager.getRequestEntity(uriVariables);
        log.info("Getting attributes for project...");
        ResponseEntity<RedcapProjectAttribute[]> responseEntity = restTemplate.exchange(redcapSessionManager.getRedcapApiURI(), HttpMethod.POST, requestEntity, RedcapProjectAttribute[].class);
        return Arrays.asList(responseEntity.getBody());
    }

    private List<Map<String, String>> getRedcapDataForProject(String projectToken) {
        LinkedMultiValueMap<String, String> uriVariables = new LinkedMultiValueMap<>();
        uriVariables.add("token", projectToken);
        uriVariables.add("content", "record");
        uriVariables.add("format", "json");
        uriVariables.add("type", "flat");

        RestTemplate restTemplate = new RestTemplate();
        HttpEntity<LinkedMultiValueMap<String, Object>> requestEntity = redcapSessionManager.getRequestEntity(uriVariables);
        log.info("Getting data for project...");
        ResponseEntity<ObjectNode[]> responseEntity = restTemplate.exchange(redcapSessionManager.getRedcapApiURI(), HttpMethod.POST, requestEntity, ObjectNode[].class);
        List<Map<String, String>> responses = new ArrayList<>();

        for(ObjectNode response : responseEntity.getBody())
        {
            Map<String, String> map = new HashMap<>();
            Iterator<Map.Entry<String, JsonNode>> nodeIterator = response.fields();
            while (nodeIterator.hasNext()) {
                Map.Entry<String, JsonNode> entry = (Map.Entry<String, JsonNode>) nodeIterator.next();
                map.put(entry.getKey().toUpperCase(), entry.getValue().asText());
            }
            responses.add(map);
        }
        return responses;
    }

    private List<String> makeHeader(Map<RedcapProjectAttribute, RedcapAttributeMetadata> attributeMap) {
        List<String> header = new ArrayList<>();
        for (Map.Entry<RedcapProjectAttribute, RedcapAttributeMetadata> entry : attributeMap.entrySet()) {
            header.add(entry.getValue().getNormalizedColumnHeader());
        }
        return header;
    }

    private boolean tokensHaveBeenSelected() {
        return clinicalTimelineTokens != null && clinicalDataTokens != null;
    }

    private void checkTokensByStableId(String stableId) {
        if (!tokensHaveBeenSelected()) {
            clinicalTimelineTokens = redcapSessionManager.getTimelineTokenMapByStableId(stableId);
            clinicalDataTokens = redcapSessionManager.getClinicalTokenMapByStableId(stableId);
        }
    }

    /**
     * Generates list of patient attributes from full header from redcap.
     * @param fullHeader
     * @return
     */
    @Override
    public Map<String, List<String>> getFullPatientHeader(Map<String, List<String>> fullHeader) {
        List<String> displayNames = new ArrayList<>();
        List<String> descriptions = new ArrayList<>();
        List<String> datatypes = new ArrayList<>();
        List<String> priorities = new ArrayList<>();
        List<String> externalHeader = new ArrayList<>();
        List<String> header = new ArrayList<>();

        for (int i=0; i<fullHeader.get("header").size(); i++) {
            if (fullHeader.get("attribute_types").get(i).equals("PATIENT")) {
                displayNames.add(fullHeader.get("display_names").get(i));
                descriptions.add(fullHeader.get("descriptions").get(i));
                datatypes.add(fullHeader.get("datatypes").get(i));
                priorities.add(fullHeader.get("priorities").get(i));
                externalHeader.add(fullHeader.get("external_header").get(i));
                header.add(fullHeader.get("header").get(i));
            }
        }
        fullPatientHeader.put("display_names", displayNames);
        fullPatientHeader.put("descriptions", descriptions);
        fullPatientHeader.put("datatypes", datatypes);
        fullPatientHeader.put("priorities", priorities);
        fullPatientHeader.put("external_header", externalHeader);
        fullPatientHeader.put("header", header);
        return fullPatientHeader;
    }

    /**
     * Generates list of sample attributes from full header from redcap.
     * @param fullHeader
     * @return
     */
    @Override
    public Map<String, List<String>> getFullSampleHeader(Map<String, List<String>> fullHeader) {
        List<String> displayNames = new ArrayList<>();
        List<String> descriptions = new ArrayList<>();
        List<String> datatypes = new ArrayList<>();
        List<String> priorities = new ArrayList<>();
        List<String> externalHeader = new ArrayList<>();
        List<String> header = new ArrayList<>();

        for (int i=0; i<fullHeader.get("header").size(); i++) {
            if (fullHeader.get("attribute_types").get(i).equals("SAMPLE")) {
                displayNames.add(fullHeader.get("display_names").get(i));
                descriptions.add(fullHeader.get("descriptions").get(i));
                datatypes.add(fullHeader.get("datatypes").get(i));
                priorities.add(fullHeader.get("priorities").get(i));
                externalHeader.add(fullHeader.get("external_header").get(i));
                header.add(fullHeader.get("header").get(i));
            }
        }
        fullSampleHeader.put("display_names", displayNames);
        fullSampleHeader.put("descriptions", descriptions);
        fullSampleHeader.put("datatypes", datatypes);
        fullSampleHeader.put("priorities", priorities);
        fullSampleHeader.put("external_header", externalHeader);
        fullSampleHeader.put("header", header);
        return fullSampleHeader;
    }

    private List<String> readClinicalFile(File file) throws IOException {
        LinkedList<String> lineList = new LinkedList<>();
        BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
        while (bufferedReader.ready()) {
            String line = bufferedReader.readLine();
            if (line != null) {
                lineList.add(line);
            }
        }
        return lineList;
    }

    private List<String> convertTSVtoCSV(List<String> tsvLines, boolean dropDuplicatedKeys) {
        HashSet<String> seen = new HashSet<String>();
        LinkedList<String> csvLines = new LinkedList<String>();
        for (String tsvLine : tsvLines) {
            String[] tsvFields = tsvLine.split("\t",-1);
            String key = tsvFields[0].trim();
            if (dropDuplicatedKeys && seen.contains(key)) {
                continue;
            }
            seen.add(key);
            String[] csvFields = new String[tsvFields.length];
            for (int i = 0; i < tsvFields.length; i++) {
                String tsvField = tsvFields[i];
                String csvField = tsvField;
                if (tsvField.indexOf(",") != -1) {
                    csvField = StringEscapeUtils.escapeCsv(tsvField);
                }
                csvFields[i] = csvField;
            }
            csvLines.add(String.join(",", csvFields));
        }
        return csvLines;
    }

    public static void main(String[] args) {}
}
