/*
 * Copyright (c) 2018 Memorial Sloan-Kettering Cancer Center.
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

package org.mskcc.cmo.ks.crdb.pipeline.util;

import java.util.*;
import org.junit.Assert;
import org.junit.runner.RunWith;
import org.junit.Test;
import org.mskcc.cmo.ks.crdb.pipeline.util.CRDBTestConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@ContextConfiguration(classes=CRDBTestConfiguration.class)
@RunWith(SpringJUnit4ClassRunner.class)
public class CRDBUtilsTest {

    @Autowired
    private CRDBUtils crdbUtils;

    @Test
    public void testStandardizeTimelineFieldOrder() {
        Map<List<String>, List<String>> standardizeFieldOrderTestCases = makeStandardizeTimelineFieldOrderTestCases();
        ArrayList<String> errorMessages = new ArrayList<>();
        for (List<String> testFields : standardizeFieldOrderTestCases.keySet()) {
            List<String> expectedFields = standardizeFieldOrderTestCases.get(testFields);
            List<String> actualFields = crdbUtils.standardizeTimelineFieldOrder(testFields);
            if (!expectedFields.equals(actualFields)) {
                errorMessages.add("\ttestfields: '" + String.join(",", testFields) +
                        "' expected: '" + String.join(",", expectedFields) +
                        "' actual: '" + String.join(",", actualFields) + "'");
            }
        }
        if (errorMessages.size() > 0) {
            Assert.fail("Messages from failed test cases:\n" + String.join("\n", errorMessages) + "\n");
        }
    }

    @Test
    public void testConvertWhitespace() {
        Map<String, String> convertWhitespaceTestCases = makeConvertWhitespaceTestCases();
        ArrayList<String> errorMessages = new ArrayList<>();
        for (String testString : convertWhitespaceTestCases.keySet()) {
            String expectedString = convertWhitespaceTestCases.get(testString);
            String actualString = crdbUtils.convertWhitespace(testString);
            if (!expectedString.equals(actualString)) {
                errorMessages.add("\ttestcase: '" + testString + "' expected: '" + expectedString + "' actual: '" + actualString + "'");
            }
        }
        if (errorMessages.size() > 0) {
            Assert.fail("Messages from failed test cases:\n" + String.join("\n", errorMessages) + "\n");
        }
    }

    private Map<List<String>, List<String>> makeStandardizeTimelineFieldOrderTestCases() {
        HashMap<List<String>, List<String>> cases = new HashMap<>();
        cases.put(Arrays.asList("PATIENT_ID", "START_DATE", "STOP_DATE", "EVENT_TYPE", "SAMPLE_ID"),
                Arrays.asList("PATIENT_ID", "START_DATE", "STOP_DATE", "EVENT_TYPE", "SAMPLE_ID"));
        cases.put(Arrays.asList("SAMPLE_ID", "START_DATE", "STOP_DATE", "EVENT_TYPE", "PATIENT_ID"),
                Arrays.asList("PATIENT_ID", "START_DATE", "STOP_DATE", "EVENT_TYPE", "SAMPLE_ID"));
        cases.put(Arrays.asList("START_DATE", "SAMPLE_ID", "STOP_DATE", "EVENT_TYPE", "PATIENT_ID"),
                Arrays.asList("PATIENT_ID", "START_DATE", "STOP_DATE", "EVENT_TYPE", "SAMPLE_ID"));
        cases.put(Arrays.asList("PATIENT_ID", "START_DATE", "STOP_DATE", "EVENT_TYPE", "SAMPLE_ID", "FIELD_A", "FIELD_B"),
                Arrays.asList("PATIENT_ID", "START_DATE", "STOP_DATE", "EVENT_TYPE", "SAMPLE_ID", "FIELD_A", "FIELD_B"));
        cases.put(Arrays.asList("FIELD_A", "PATIENT_ID", "START_DATE", "STOP_DATE", "EVENT_TYPE", "SAMPLE_ID", "FIELD_B"),
                Arrays.asList("PATIENT_ID", "START_DATE", "STOP_DATE", "EVENT_TYPE", "SAMPLE_ID", "FIELD_A", "FIELD_B"));
        cases.put(Arrays.asList("PATIENT_ID", "FIELD_A", "START_DATE", "STOP_DATE", "EVENT_TYPE", "SAMPLE_ID", "FIELD_B"),
                Arrays.asList("PATIENT_ID", "START_DATE", "STOP_DATE", "EVENT_TYPE", "SAMPLE_ID", "FIELD_A", "FIELD_B"));
        cases.put(Arrays.asList("FIELD_A", "FIELD_B", "PATIENT_ID", "START_DATE", "STOP_DATE", "EVENT_TYPE", "SAMPLE_ID"),
                Arrays.asList("PATIENT_ID", "START_DATE", "STOP_DATE", "EVENT_TYPE", "SAMPLE_ID", "FIELD_A", "FIELD_B"));
        cases.put(Arrays.asList("FIELD_A", "PATIENT_ID", "FIELD_B", "START_DATE", "STOP_DATE", "EVENT_TYPE", "SAMPLE_ID"),
                Arrays.asList("PATIENT_ID", "START_DATE", "STOP_DATE", "EVENT_TYPE", "SAMPLE_ID", "FIELD_A", "FIELD_B"));
        cases.put(Arrays.asList("FIELD_A", "PATIENT_ID", "FIELD_B", "START_DATE", "STOP_DATE", "EVENT_TYPE", "SAMPLE_ID"),
                Arrays.asList("PATIENT_ID", "START_DATE", "STOP_DATE", "EVENT_TYPE", "SAMPLE_ID", "FIELD_A", "FIELD_B"));
        cases.put(Arrays.asList("FIELD_A", "PATIENT_ID", "START_DATE", "FIELD_B", "STOP_DATE", "EVENT_TYPE", "SAMPLE_ID"),
                Arrays.asList("PATIENT_ID", "START_DATE", "STOP_DATE", "EVENT_TYPE", "SAMPLE_ID", "FIELD_A", "FIELD_B"));
        cases.put(Arrays.asList("STOP_DATE", "SAMPLE_ID", "FIELD_A", "PATIENT_ID", "FIELD_B", "EVENT_TYPE", "START_DATE"),
                Arrays.asList("PATIENT_ID", "START_DATE", "STOP_DATE", "EVENT_TYPE", "SAMPLE_ID", "FIELD_A", "FIELD_B"));
        cases.put(Arrays.asList("STOP_DATE", "SAMPLE_ID", "FIELD_A", "PATIENT_ID", "FIELD_B", "EVENT_TYPE", "START_DATE"),
                Arrays.asList("PATIENT_ID", "START_DATE", "STOP_DATE", "EVENT_TYPE", "SAMPLE_ID", "FIELD_A", "FIELD_B"));
        return cases;
    }

    private Map<String, String> makeConvertWhitespaceTestCases() {
        HashMap<String, String> cases = new HashMap<>();
        cases.put("", "");
        cases.put(" ", " ");
        cases.put("\t", "");
        cases.put("\t ", " ");
        cases.put(" \t", " ");
        cases.put(" \t ", "   ");
        cases.put("\t\n", "");
        cases.put("\t\n ", " ");
        cases.put(" \t\n", " ");
        cases.put(" \t\n ", "   ");
        cases.put("\t\n\r", "");
        cases.put("\t\n\r ", " ");
        cases.put("b", "b");
        cases.put("\tb", "b");
        cases.put("\t b", " b");
        cases.put("\t\nb", "b");
        cases.put("\t\n b", " b");
        cases.put("\t\n\rb", "b");
        cases.put("\t\n\r b", " b");
        cases.put("ab", "ab");
        cases.put("a\tb", "a b");
        cases.put("a\t b", "a  b");
        cases.put("a\t\nb", "a b");
        cases.put("a\t\n b", "a  b");
        cases.put("a\t\n\rb", "a b");
        cases.put("a\t\n\r b", "a  b");
        cases.put("a\t", "a");
        cases.put("a\t ", "a  ");
        cases.put("a\t\n", "a");
        cases.put("a\t\n ", "a  ");
        cases.put("a\t\n\r", "a");
        cases.put("a\t\n\r ", "a  ");
        return cases;
    }

}
