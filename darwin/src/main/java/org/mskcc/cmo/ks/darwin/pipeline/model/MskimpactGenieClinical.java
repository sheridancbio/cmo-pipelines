/*
 * Copyright (c) 2016-2017 Memorial Sloan-Kettering Cancer Center.
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
package org.mskcc.cmo.ks.darwin.pipeline.model;

import java.util.*;

/**
 *
 * @author heinsz
 */
public class MskimpactGenieClinical {
    
    private String patientId;
    private String sampleId;
    private String ageAtSeqReport;
    
    public MskimpactGenieClinical() {}
    
    public MskimpactGenieClinical(String patientId, String sampleId, String ageAtSeqReport) {
        this.patientId = patientId;
        this.sampleId = sampleId;
        this.ageAtSeqReport = ageAtSeqReport;
    }
    
    public String getPatientId() {
        return patientId;
    }
    
    public void setPatientId(String patientId) {
        this.patientId = patientId;
    }
    
    public String getSampleId() {
        return sampleId;
    }
    
    public void setSampleId(String sampleId) {
        this.sampleId = sampleId;
    }
    
    public String getAgeAtSeqReport() {
        return ageAtSeqReport;
    }
    
    public void setAgeAtSeqReport(String ageAtSeqReport) {
        this.ageAtSeqReport = ageAtSeqReport;
    }
    
    public List<String> getFieldNames() {
        List<String> fieldNames = new ArrayList<>();
        fieldNames.add("PatientId");
        fieldNames.add("SampleId");
        fieldNames.add("AgeAtSeqReport");
        return fieldNames;
    }
    
    public List<String> getHeaders() {
        List<String> headerNames = new ArrayList<>();
        headerNames.add("PATIENT_ID");
        headerNames.add("SAMPLE_ID");
        headerNames.add("AGE_AT_SEQ_REPORT");        
        return headerNames;
    }
}
