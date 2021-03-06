/*
 * Copyright (c) 2016 - 2018 Memorial Sloan-Kettering Cancer Center.
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

package org.mskcc.cmo.ks.crdb.pipeline.model;

import java.util.*;

/**
 * Model for CRDBPDXClinicalPatientDataset results.
 *
 * @author averyniceday
 */

public class CRDBPDXTimelineDataset {

    private String PATIENT_ID;
    private String SAMPLE_ID;
    private String PDX_ID;
    private String START_DATE;
    private String STOP_DATE;
    private String EVENT_TYPE;
    private String PASSAGE_ID;
    private String TREATMENT_TYPE;
    private String SUBTYPE;
    private String AGENT;
    private String RESPONSE;
    private String RESPONSE_DURATION_MONTHS;
    private String REASON_FOR_TX_DISCONTINUATION;
    private String SURGERY_DETAILS;
    private String EVENT_TYPE_DETAILED;
    private String PROCEDURE_LOCATION;
    private String PROCEDURE_LOCATION_SPECIFY;
    private String DIAGNOSTIC_TYPE;
    private String DIAGNOSTIC_TYPE_SITE;
    private String IMAGING;
    private String SPECIMEN_TYPE;
    private String SPECIMEN_SITE;
    private String AGE_AT_PROCEDURE;
    private String LATERALITY;
    private String DISEASE_STATUS;
    private String METASTATIC_SITE;
    private String SAMPLE_TYPE;
    private String SITE_OF_RECURRENCE;
    private String TREATMENT_NOTES;

    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    private static List<String> fieldNameList = initializeFieldNameList();

    /**
     * No args constructor for use in serialization
     */
    public CRDBPDXTimelineDataset() {
    }

    public CRDBPDXTimelineDataset(String PATIENT_ID, String SAMPLE_ID, String PDX_ID,
                                  String START_DATE, String STOP_DATE, String EVENT_TYPE, String PASSAGE_ID,
                                  String TREATMENT_TYPE, String SUBTYPE, String AGENT, String RESPONSE,
                                  String RESPONSE_DURATION_MONTHS, String REASON_FOR_TX_DISCONTINUATION,
                                  String SURGERY_DETAILS, String EVENT_TYPE_DETAILED, String PROCEDURE_LOCATION,
                                  String PROCEDURE_LOCATION_SPECIFY, String DIAGNOSTIC_TYPE, String DIAGNOSTIC_TYPE_SITE,
                                  String IMAGING, String SPECIMEN_TYPE, String SPECIMEN_SITE, String AGE_AT_PROCEDURE,
                                  String LATERALITY, String DISEASE_STATUS, String METASTATIC_SITE,
                                  String SAMPLE_TYPE, String SITE_OF_RECURRENCE, String TREATMENT_NOTES) {
        this.PATIENT_ID = PATIENT_ID == null ? "NA" : PATIENT_ID;
        this.SAMPLE_ID = SAMPLE_ID == null ? "NA" : SAMPLE_ID;
        this.PDX_ID = PDX_ID == null ? "NA" : PDX_ID;
        this.START_DATE = START_DATE == null ? "NA" : START_DATE;
        this.STOP_DATE = STOP_DATE == null ? "NA" : STOP_DATE;
        this.EVENT_TYPE = EVENT_TYPE == null ? "NA" : EVENT_TYPE;
        this.PASSAGE_ID = PASSAGE_ID == null ? "NA" : PASSAGE_ID;
        this.TREATMENT_TYPE = TREATMENT_TYPE == null ? "NA" : TREATMENT_TYPE;
        this.SUBTYPE = SUBTYPE == null ? "NA" : SUBTYPE;
        this.AGENT = AGENT == null ? "NA" : AGENT;
        this.RESPONSE = RESPONSE == null ? "NA" : RESPONSE;
        this.RESPONSE_DURATION_MONTHS = RESPONSE_DURATION_MONTHS == null ? "NA" : RESPONSE_DURATION_MONTHS;
        this.REASON_FOR_TX_DISCONTINUATION = REASON_FOR_TX_DISCONTINUATION == null ? "NA" : REASON_FOR_TX_DISCONTINUATION;
        this.SURGERY_DETAILS = SURGERY_DETAILS == null ? "NA" : SURGERY_DETAILS;
        this.EVENT_TYPE_DETAILED = EVENT_TYPE_DETAILED == null ? "NA" : EVENT_TYPE_DETAILED;
        this.PROCEDURE_LOCATION = PROCEDURE_LOCATION == null ? "NA" : PROCEDURE_LOCATION;
        this.PROCEDURE_LOCATION_SPECIFY = PROCEDURE_LOCATION_SPECIFY == null ? "NA" : PROCEDURE_LOCATION_SPECIFY;
        this.DIAGNOSTIC_TYPE = DIAGNOSTIC_TYPE == null ? "NA" : DIAGNOSTIC_TYPE;
        this.DIAGNOSTIC_TYPE_SITE = DIAGNOSTIC_TYPE_SITE == null ? "NA" : DIAGNOSTIC_TYPE_SITE;
        this.IMAGING = IMAGING == null ? "NA" : IMAGING;
        this.SPECIMEN_TYPE = SPECIMEN_TYPE == null ? "NA" : SPECIMEN_TYPE;
        this.SPECIMEN_SITE = SPECIMEN_SITE == null ? "NA" : SPECIMEN_SITE;
        this.AGE_AT_PROCEDURE = AGE_AT_PROCEDURE == null ? "NA" : AGE_AT_PROCEDURE;
        this.LATERALITY = LATERALITY == null ? "NA" : LATERALITY;
        this.DISEASE_STATUS = DISEASE_STATUS == null ? "NA" : DISEASE_STATUS;
        this.METASTATIC_SITE = METASTATIC_SITE == null ? "NA" : METASTATIC_SITE;
        this.SAMPLE_TYPE = SAMPLE_TYPE == null ? "NA" : SAMPLE_TYPE;
        this.SITE_OF_RECURRENCE = SITE_OF_RECURRENCE == null ? "NA" : SITE_OF_RECURRENCE;
        this.TREATMENT_NOTES = TREATMENT_NOTES == null ? "NA" : TREATMENT_NOTES;
    }

    public String getPATIENT_ID() {
        return PATIENT_ID;
    }

    public void setPATIENT_ID(String PATIENT_ID) {
        this.PATIENT_ID = PATIENT_ID;
    }

    public String getSAMPLE_ID() {
        return SAMPLE_ID;
    }

    public void setSAMPLE_ID(String SAMPLE_ID) {
        this.SAMPLE_ID = SAMPLE_ID;
    }

    public String getPDX_ID() {
        return PDX_ID;
    }

    public void setPDX_ID(String PDX_ID) {
        this.PDX_ID = PDX_ID;
    }

    public String getSTART_DATE() {
        return START_DATE;
    }

    public void setSTART_DATE(String START_DATE) {
        this.START_DATE = START_DATE;
    }

    public String getSTOP_DATE() {
        return STOP_DATE;
    }

    public void setSTOP_DATE(String STOP_DATE) {
        this.STOP_DATE = STOP_DATE;
    }

    public String getEVENT_TYPE() {
        return EVENT_TYPE;
    }

    public void setEVENT_TYPE(String EVENT_TYPE) {
        this.EVENT_TYPE = EVENT_TYPE;
    }

    public String getPASSAGE_ID() {
        return PASSAGE_ID;
    }

    public void setPASSAGE_ID(String PASSAGE_ID) {
        this.PASSAGE_ID = PASSAGE_ID;
    }

    public String getTREATMENT_TYPE() {
        return TREATMENT_TYPE;
    }

    public void setTREATMENT_TYPE(String TREATMENT_TYPE) {
        this.TREATMENT_TYPE = TREATMENT_TYPE;
    }

    public String getSUBTYPE() {
        return SUBTYPE;
    }

    public void setSUBTYPE(String SUBTYPE) {
        this.SUBTYPE = SUBTYPE;
    }

    public String getAGENT() {
        return AGENT;
    }

    public void setAGENT(String AGENT) {
        this.AGENT = AGENT;
    }

    public String getRESPONSE() {
        return RESPONSE;
    }

    public void setRESPONSE(String RESPONSE) {
        this.RESPONSE = RESPONSE;
    }

    public String getRESPONSE_DURATION_MONTHS() {
        return RESPONSE_DURATION_MONTHS;
    }

    public void setRESPONSE_DURATION_MONTHS(String RESPONSE_DURATION_MONTHS) {
        this.RESPONSE_DURATION_MONTHS = RESPONSE_DURATION_MONTHS;
    }

    public String getREASON_FOR_TX_DISCONTINUATION() {
        return REASON_FOR_TX_DISCONTINUATION;
    }

    public void setREASON_FOR_TX_DISCONTINUATION(String REASON_FOR_TX_DISCONTINUATION) {
        this.REASON_FOR_TX_DISCONTINUATION = REASON_FOR_TX_DISCONTINUATION;
    }

    public String getSURGERY_DETAILS() {
        return SURGERY_DETAILS;
    }

    public void setSURGERY_DETAILS(String SURGERY_DETAILS) {
        this.SURGERY_DETAILS = SURGERY_DETAILS;
    }

    public String getEVENT_TYPE_DETAILED() {
        return EVENT_TYPE_DETAILED;
    }

    public void setEVENT_TYPE_DETAILED(String EVENT_TYPE_DETAILED) {
        this.EVENT_TYPE_DETAILED = EVENT_TYPE_DETAILED;
    }

    public String getPROCEDURE_LOCATION() {
        return PROCEDURE_LOCATION;
    }

    public void setPROCEDURE_LOCATION(String PROCEDURE_LOCATION) {
        this.PROCEDURE_LOCATION = PROCEDURE_LOCATION;
    }

    public String getPROCEDURE_LOCATION_SPECIFY() {
        return PROCEDURE_LOCATION_SPECIFY;
    }

    public void setPROCEDURE_LOCATION_SPECIFY(String PROCEDURE_LOCATION_SPECIFY) {
        this.PROCEDURE_LOCATION_SPECIFY = PROCEDURE_LOCATION_SPECIFY;
    }

    public String getDIAGNOSTIC_TYPE() {
        return DIAGNOSTIC_TYPE;
    }

    public void setDIAGNOSTIC_TYPE(String DIAGNOSTIC_TYPE) {
        this.DIAGNOSTIC_TYPE = DIAGNOSTIC_TYPE;
    }

    public String getDIAGNOSTIC_TYPE_SITE() {
        return DIAGNOSTIC_TYPE_SITE;
    }

    public void setDIAGNOSTIC_TYPE_SITE(String DIAGNOSTIC_TYPE_SITE) {
        this.DIAGNOSTIC_TYPE_SITE = DIAGNOSTIC_TYPE_SITE;
    }

    public String getIMAGING() {
        return IMAGING;
    }

    public void setIMAGING(String IMAGING) {
        this.IMAGING = IMAGING;
    }

    public String getSPECIMEN_TYPE() {
        return SPECIMEN_TYPE;
    }

    public void setSPECIMEN_TYPE(String SPECIMEN_TYPE) {
        this.SPECIMEN_TYPE = SPECIMEN_TYPE;
    }

    public String getSPECIMEN_SITE() {
        return SPECIMEN_SITE;
    }

    public void setSPECIMEN_SITE(String SPECIMEN_SITE) {
        this.SPECIMEN_SITE = SPECIMEN_SITE;
    }

    public String getAGE_AT_PROCEDURE() {
        return AGE_AT_PROCEDURE;
    }

    public void setAGE_AT_PROCEDURE(String AGE_AT_PROCEDURE) {
        this.AGE_AT_PROCEDURE = AGE_AT_PROCEDURE;
    }

    public String getLATERALITY() {
        return LATERALITY;
    }

    public void setLATERALITY(String LATERALITY) {
        this.LATERALITY = LATERALITY;
    }

    public String getDISEASE_STATUS() {
        return DISEASE_STATUS;
    }

    public void setDISEASE_STATUS(String DISEASE_STATUS) {
        this.DISEASE_STATUS = DISEASE_STATUS;
    }

    public String getMETASTATIC_SITE() {
        return METASTATIC_SITE;
    }

    public void setMETASTATIC_SITE(String METASTATIC_SITE) {
        this.METASTATIC_SITE = METASTATIC_SITE;
    }

    public String getSAMPLE_TYPE() {
        return SAMPLE_TYPE;
    }

    public void setSAMPLE_TYPE(String SAMPLE_TYPE) {
        this.SAMPLE_TYPE = SAMPLE_TYPE;
    }

    public String getSITE_OF_RECURRENCE() {
        return SITE_OF_RECURRENCE;
    }

    public void setSITE_OF_RECURRENCE(String SITE_OF_RECURRENCE) {
        this.SITE_OF_RECURRENCE = SITE_OF_RECURRENCE;
    }

    public String getTREATMENT_NOTES() {
        return TREATMENT_NOTES;
    }

    public void setTREATMENT_NOTES(String TREATMENT_NOTES) {
        this.TREATMENT_NOTES = TREATMENT_NOTES;
    }

    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

    /**
     * Returns the field names without additional properties.
     * @return List<String>
     */
    public static List<String> getFieldNames() {
        return CRDBPDXTimelineDataset.fieldNameList;
    }

    private static List<String> initializeFieldNameList() {
        List<String> fieldNameList = new ArrayList<String>(35);
        fieldNameList.add("PATIENT_ID");
        fieldNameList.add("SAMPLE_ID");
        fieldNameList.add("PDX_ID");
        fieldNameList.add("START_DATE");
        fieldNameList.add("STOP_DATE");
        fieldNameList.add("EVENT_TYPE");
        fieldNameList.add("PASSAGE_ID");
        fieldNameList.add("TREATMENT_TYPE");
        fieldNameList.add("SUBTYPE");
        fieldNameList.add("AGENT");
        fieldNameList.add("RESPONSE");
        fieldNameList.add("RESPONSE_DURATION_MONTHS");
        fieldNameList.add("REASON_FOR_TX_DISCONTINUATION");
        fieldNameList.add("SURGERY_DETAILS");
        fieldNameList.add("EVENT_TYPE_DETAILED");
        fieldNameList.add("PROCEDURE_LOCATION");
        fieldNameList.add("PROCEDURE_LOCATION_SPECIFY");
        fieldNameList.add("DIAGNOSTIC_TYPE");
        fieldNameList.add("DIAGNOSTIC_TYPE_SITE");
        fieldNameList.add("IMAGING");
        fieldNameList.add("SPECIMEN_TYPE");
        fieldNameList.add("SPECIMEN_SITE");
        fieldNameList.add("AGE_AT_PROCEDURE");
        fieldNameList.add("LATERALITY");
        fieldNameList.add("DISEASE_STATUS");
        fieldNameList.add("METASTATIC_SITE");
        fieldNameList.add("SAMPLE_TYPE");
        fieldNameList.add("SITE_OF_RECURRENCE");
        fieldNameList.add("TREATMENT_NOTES");
        return Collections.unmodifiableList(fieldNameList);
    }
}
