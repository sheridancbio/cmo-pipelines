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
 * Model for CRDBDataset results.
 *
 * @author ochoaa
 */
public class CRDBDataset {

    private String DMP_ID;
    private String CONSENT_DATE_DAYS;
    private String SURVIVAL_STATUS;
    private String TREATMENT_END_DAYS;
    private String OFF_STUDY_DAYS;
    private String COMMENTS;

    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    private static List<String> fieldNameList = initializeFieldNameList();

    /**
     * No args constructor for use in serialization
     */
    public CRDBDataset() {
    }

    /**
     * @param  DMP_ID
     * @param  CONSENT_DATE_DAYS
     * @param  SURVIVAL_STATUS
     * @param  TREATMENT_END_DAYS
     * @param  OFF_STUDY_DAYS
     * @param  COMMENTS
    **/
    public CRDBDataset(String DMP_ID, String CONSENT_DATE_DAYS,
            String SURVIVAL_STATUS, String TREATMENT_END_DAYS, String OFF_STUDY_DAYS, String COMMENTS) {
        this.DMP_ID = DMP_ID == null ? "NA" : DMP_ID;
        this.CONSENT_DATE_DAYS = CONSENT_DATE_DAYS == null ? "NA" : CONSENT_DATE_DAYS;
        this.SURVIVAL_STATUS = SURVIVAL_STATUS == null ? "NA" : SURVIVAL_STATUS;
        this.TREATMENT_END_DAYS = TREATMENT_END_DAYS == null ? "NA" : TREATMENT_END_DAYS;
        this.OFF_STUDY_DAYS = OFF_STUDY_DAYS == null ? "NA" : OFF_STUDY_DAYS;
        this.COMMENTS = COMMENTS == null ? "NA" : COMMENTS;
    }

    public String getDMP_ID() {
        return DMP_ID;
    }

    public void setDMP_ID(String DMP_ID) {
        this.DMP_ID = DMP_ID;
    }

    public CRDBDataset withDMP_ID(String DMP_ID) {
        this.DMP_ID = DMP_ID;
        return this;
    }

    public String getCONSENT_DATE_DAYS() {
        return CONSENT_DATE_DAYS;
    }

    public void setCONSENT_DATE_DAYS(String CONSENT_DATE_DAYS) {
        this.CONSENT_DATE_DAYS = CONSENT_DATE_DAYS;
    }

    public CRDBDataset withCONSENT_DATE_DAYS(String CONSENT_DATE_DAYS) {
        this.CONSENT_DATE_DAYS = CONSENT_DATE_DAYS;
        return this;
    }

    public String getSURVIVAL_STATUS() {
        return SURVIVAL_STATUS;
    }

    public void setSURVIVAL_STATUS(String SURVIVAL_STATUS) {
        this.SURVIVAL_STATUS = SURVIVAL_STATUS;
    }

    public CRDBDataset withSURVIVAL_STATUS(String SURVIVAL_STATUS) {
        this.SURVIVAL_STATUS = SURVIVAL_STATUS;
        return this;
    }
    public String getTREATMENT_END_DAYS() {
        return TREATMENT_END_DAYS;
    }

    public void setTREATMENT_END_DAYS(String TREATMENT_END_DAYS) {
        this.TREATMENT_END_DAYS = TREATMENT_END_DAYS;
    }

    public CRDBDataset withTREATMENT_END_DAYS(String TREATMENT_END_DAYS) {
        this.TREATMENT_END_DAYS = TREATMENT_END_DAYS;
        return this;
    }

    public String getOFF_STUDY_DAYS() {
        return OFF_STUDY_DAYS;
    }

    public void setOFF_STUDY_DAYS(String OFF_STUDY_DAYS) {
        this.OFF_STUDY_DAYS = OFF_STUDY_DAYS;
    }

    public CRDBDataset withOFF_STUDY_DAYS(String OFF_STUDY_DAYS) {
        this.OFF_STUDY_DAYS = OFF_STUDY_DAYS;
        return this;
    }

    public String getCOMMENTS() {
        return COMMENTS;
    }

    public void setCOMMENTS(String COMMENTS) {
        this.COMMENTS = COMMENTS;
    }

    public CRDBDataset withCOMMENTS(String COMMENTS) {
        this.COMMENTS = COMMENTS;
        return this;
    }

    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

    public CRDBDataset withAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
        return this;
    }

    /**
     * Returns the field names without additional properties.
     * @return List<String>
     */
    public static List<String> getFieldNames() {
        return CRDBDataset.fieldNameList;
    }

    private static List<String> initializeFieldNameList() {
        List<String> fieldNameList = new ArrayList<String>(35);
        fieldNameList.add("DMP_ID");
        fieldNameList.add("CONSENT_DATE_DAYS");
        fieldNameList.add("SURVIVAL_STATUS");
        fieldNameList.add("TREATMENT_END_DAYS");
        fieldNameList.add("OFF_STUDY_DAYS");
        fieldNameList.add("COMMENTS");
        return Collections.unmodifiableList(fieldNameList);
    }
}
