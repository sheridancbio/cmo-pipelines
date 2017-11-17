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

package org.mskcc.cmo.ks.redcap.pipeline;

import java.io.*;
import java.lang.StringBuilder;
import java.util.*;
import org.junit.Assert;
import org.junit.runner.RunWith;
import org.junit.Test;
import org.mskcc.cmo.ks.redcap.pipeline.ClinicalDataReader;
import org.mskcc.cmo.ks.redcap.pipeline.ClinicalDataReaderTestConfiguration;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.beans.factory.annotation.*;
import org.springframework.beans.factory.config.BeanExpressionContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@ContextConfiguration(classes=ClinicalDataReaderTestConfiguration.class)
@RunWith(SpringJUnit4ClassRunner.class)
public class ClinicalDataReaderTest {

    @Autowired
    private ClinicalDataReader clinicalDataReader;

    /** This test reads two mocked redcap projects, one of which has no SAMPLE_ID attribute.
     * Proper output is expected. In particular, no records should be missing the SAMPLE_ID field.
     */
    @Test
    public void testClinicalDataReaderWithPatientOnlyProject() {
        ExecutionContext ec = new ExecutionContext();
        clinicalDataReader.open(ec);
        StringBuilder errorMessage = new StringBuilder("\nFailures:\n");
        int failCount = 0;
        Map<String, Map<String, String>> expectedSampleToRecordMap = makeExpectedSampleToRecordMap();
        Map<String, Map<String, String>> returnedSampleToRecordMap = new HashMap<>();
        // compare returned values to expected
        while (true) {
            Map<String, String> returnedRecord = null;
            try {
                returnedRecord = clinicalDataReader.read();
            } catch (Exception e) {
                Assert.fail("Exception thrown from ClinicalDataReader.read() : " + e.getMessage());
            }
            if (returnedRecord == null) {
                break;
            }
            String sample_id = returnedRecord.get("SAMPLE_ID");
            if (sample_id == null) {
                failCount = failCount + 1;
                errorMessage.append("ClinicalDataReader.read() returned record with null SAMPLE_ID : " + recordToString(returnedRecord) + "\n");
                continue;
            }
            if (returnedSampleToRecordMap.containsKey(sample_id)) {
                failCount = failCount + 1;
                errorMessage.append("ClinicalDataReader.read() returned a record which repeats a previous sample id : " + recordToString(returnedRecord) + "\n");
                continue;
            }
            returnedSampleToRecordMap.put(sample_id, returnedRecord);
        }
        for (String returnedSampleId : returnedSampleToRecordMap.keySet()) {
            if (returnedSampleId == null) {
                continue; //already reported above
            }
            Map<String, String> returnedRecord = returnedSampleToRecordMap.get(returnedSampleId);
            Map<String, String> expectedRecord = expectedSampleToRecordMap.get(returnedSampleId);
            if (expectedRecord == null) {
                failCount = failCount + 1;
                errorMessage.append("Record from ClinicalDataReader.read() has unexpected SAMPLE_ID : " + recordToString(returnedRecord) + "\n");
                continue;
            }
            Set<String> returnedRecordKeys = returnedRecord.keySet();
            Set<String> expectedRecordKeys = expectedRecord.keySet();
            for (String key : returnedRecordKeys) {
                String returnedRecordValue = returnedRecord.get(key);
                String expectedRecordValue = expectedRecord.get(key);
                if (expectedRecordValue == null) {
                    failCount = failCount + 1;
                    errorMessage.append("Record from ClinicalDataReader.read() has unexpected field " + key + " : " + recordToString(returnedRecord) + "\n");
                    continue;
                }
                expectedRecordKeys.remove(key);
                if (returnedRecordValue != null && !returnedRecordValue.equals(expectedRecordValue)) {
                    failCount = failCount + 1;
                    errorMessage.append("Record from ClinicalDataReader.read() has unexpected value (expected: " + expectedRecordValue + ") in field " + key + " : " + recordToString(returnedRecord) + "\n");
                    continue;
                }
            }
            if (!expectedRecordKeys.isEmpty()) {
                failCount = failCount + 1;
                errorMessage.append("Record from ClinicalDataReader.read() has missing fields ( ");
                for (String key : expectedRecordKeys) {
                    errorMessage.append(key + " ");
                }
                errorMessage.append(") " + recordToString(returnedRecord) + "\n");
            }
        }
        for (String expectedSampleId : expectedSampleToRecordMap.keySet()) {
            if (!returnedSampleToRecordMap.containsKey(expectedSampleId)) {
                failCount = failCount + 1;
                errorMessage.append("Record expected from ClinicalDataReader.read() was not seen : " + recordToString(expectedSampleToRecordMap.get(expectedSampleId)) + "\n");
                continue;
            }
        }
        if (failCount > 0) {
            Assert.fail(errorMessage.toString());
        }
    }

    private String recordToString(Map<String, String> record) {
        StringBuilder returnString = new StringBuilder("[");
        int initialStringLength = returnString.length();
        LinkedHashSet<String> keys = new LinkedHashSet<>(record.keySet());
        if (keys.contains("sample_id")) {
            addToRecordToStringOutput(returnString, "sample_id", record, initialStringLength);
            keys.remove("sample_id");
        }
        if (keys.contains("patient_id")) {
            addToRecordToStringOutput(returnString, "patient_id", record, initialStringLength);
            keys.remove("patient_id");
        }
        for (String key : record.keySet()) {
            addToRecordToStringOutput(returnString, key, record, initialStringLength);
        }
        returnString.append("]");
        return returnString.toString();
    }

    private void addToRecordToStringOutput(StringBuilder returnString, String key, Map<String, String> record, int initialStringLength) {
        if (returnString.length() > initialStringLength) {
            returnString.append(",");
        }
        returnString.append(key + ":" + record.get(key));
    }

    private Map<String, Map<String, String>> makeExpectedSampleToRecordMap() {
        Map<String, Map<String, String>> expectedRecordMap = new HashMap<>(3);
        Map<String, String> nextRecord = new HashMap<>();
        nextRecord.put("SAMPLE_ID", "P-0000001-T01-IM6");
        nextRecord.put("PATIENT_ID", "P-0000001");
        nextRecord.put("CANCER_TYPE", "GBM");
        nextRecord.put("AGE", "29");
        expectedRecordMap.put(nextRecord.get("SAMPLE_ID"), nextRecord);
        nextRecord = new HashMap<>();
        nextRecord.put("SAMPLE_ID", "P-0000002-T01-IM6");
        nextRecord.put("PATIENT_ID", "P-0000002");
        nextRecord.put("CANCER_TYPE", "GBM");
        nextRecord.put("AGE", "48");
        expectedRecordMap.put(nextRecord.get("SAMPLE_ID"), nextRecord);
        nextRecord = new HashMap<>();
        nextRecord.put("SAMPLE_ID", "P-0000003-T01-IM6");
        nextRecord.put("PATIENT_ID", "P-0000003");
        nextRecord.put("CANCER_TYPE", "GBM");
        nextRecord.put("AGE", "71");
        expectedRecordMap.put(nextRecord.get("SAMPLE_ID"), nextRecord);
        return expectedRecordMap;
    }

}
