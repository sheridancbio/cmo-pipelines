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

package org.mskcc.cmo.ks.redcap.pipeline.util;

import java.io.*;
import java.lang.StringBuilder;
import java.util.*;
import org.junit.Assert;
import org.junit.runner.RunWith;
import org.junit.Test;
import org.mskcc.cmo.ks.redcap.pipeline.util.RedcapUtils;
import org.mskcc.cmo.ks.redcap.pipeline.util.ConflictingAttributeValuesException;
import org.springframework.beans.factory.annotation.*;
import org.springframework.beans.factory.config.BeanExpressionContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@ContextConfiguration(classes=RedcapUtilsTestConfiguration.class)
@RunWith(SpringJUnit4ClassRunner.class)
public class RedcapUtilsTest {

    public class GetReplacementValueTestCase {
        public String existingValue;
        public String newValue;
        public String expectedOutput;

        public GetReplacementValueTestCase(String existingValue, String newValue, String expectedOutput) {
            this.existingValue = existingValue;
            this.newValue = newValue;
            this.expectedOutput = expectedOutput;
        }
    };

    @Autowired
    private RedcapUtils redcapUtils;

    /* This test probes various combinations of values to see if merger of values will be chosen appropriately
     * Levels of precedence : least preferred is empty values, next is equivalent to "Not Applicable", highest is anything else
     * Additionally, when previous value and new value are both empty or whitespace, prefer empty string ("")
     */
    @Test
    public void testGetReplacementValueForAttribute() {
        List<GetReplacementValueTestCase> testCaseList = makeTestCaseListForGetReplacementValueForAttribute();
        StringBuilder errorMessage = new StringBuilder("\nFailed Test Cases (existingValue, newValue, expectedOutput, acutalOutput)\n");
        int failCount = 0;
        for (GetReplacementValueTestCase testCase : testCaseList) {
            String returnedReplacementValue = testCase.existingValue;
            try {
                returnedReplacementValue = redcapUtils.getReplacementValueForAttribute(testCase.existingValue, testCase.newValue);
            } catch (ConflictingAttributeValuesException e) {
                failCount = failCount + 1;
                errorMessage.append(makeGetReplacementValueFailMessage(testCase, "ConflictingAttributeValuesException.class"));
            }
            if (returnedReplacementValue == null) {
                if (testCase.expectedOutput != null) {
                    failCount = failCount + 1;
                    errorMessage.append(makeGetReplacementValueFailMessage(testCase, returnedReplacementValue));
                }
                continue;
            }
            if (!returnedReplacementValue.equals(testCase.expectedOutput)) {
                failCount = failCount + 1;
                errorMessage.append(makeGetReplacementValueFailMessage(testCase, returnedReplacementValue));
            }
        }
        if (failCount > 0) {
            Assert.fail(errorMessage.toString());
        }
    }

    @Test
    public void testGetReplacementValueForAttributeConflict() {
        GetReplacementValueTestCase testCase = new GetReplacementValueTestCase("3", "4", "ConflictingAttributeValuesException.class");
        StringBuilder errorMessage = new StringBuilder("\nFailed Test Cases (existingValue, newValue, expectedOutput, acutalOutput)\n");
        String returnedReplacementValue;
        try {
            returnedReplacementValue = redcapUtils.getReplacementValueForAttribute(testCase.existingValue, testCase.newValue); // when new value is substantial and differs from existing value, expect exception
        } catch (ConflictingAttributeValuesException e) {
            return;
        }
        errorMessage.append(makeGetReplacementValueFailMessage(testCase, returnedReplacementValue));
        Assert.fail(errorMessage.toString());
    }

    private String makeGetReplacementValueFailMessage(GetReplacementValueTestCase testCase, String returnedReplacementValue) {
        return testCase.existingValue + "\t" + testCase.newValue + "\t" + testCase.expectedOutput + "\t" + returnedReplacementValue + "\n";
    }

    private List<GetReplacementValueTestCase> makeTestCaseListForGetReplacementValueForAttribute() {
        List<GetReplacementValueTestCase> testCases = new ArrayList<>();
        testCases.add(new GetReplacementValueTestCase("3", "3", null));
        testCases.add(new GetReplacementValueTestCase("3", "NA", null));
        testCases.add(new GetReplacementValueTestCase("3", "[NA]", null));
        testCases.add(new GetReplacementValueTestCase("3", "N/A", null));
        testCases.add(new GetReplacementValueTestCase("3", "na", null));
        testCases.add(new GetReplacementValueTestCase("3", "Not Applicable", null));
        testCases.add(new GetReplacementValueTestCase("3", "NotApplicable", null));
        testCases.add(new GetReplacementValueTestCase("3", "", null));
        testCases.add(new GetReplacementValueTestCase("3", "  ", null));
        testCases.add(new GetReplacementValueTestCase("3", null, null));
        testCases.add(new GetReplacementValueTestCase("NA", "3", "3"));
        testCases.add(new GetReplacementValueTestCase("[NA]", "3", "3"));
        testCases.add(new GetReplacementValueTestCase("N/A", "3", "3"));
        testCases.add(new GetReplacementValueTestCase("na", "3", "3"));
        testCases.add(new GetReplacementValueTestCase("Not Applicable", "3", "3"));
        testCases.add(new GetReplacementValueTestCase("NotApplicable", "3", "3"));
        testCases.add(new GetReplacementValueTestCase("na", "na", null));
        testCases.add(new GetReplacementValueTestCase("na", "NA", null));
        testCases.add(new GetReplacementValueTestCase("na", "[NA]", null));
        testCases.add(new GetReplacementValueTestCase("na", "N/A", null));
        testCases.add(new GetReplacementValueTestCase("na", "na", null));
        testCases.add(new GetReplacementValueTestCase("na", "Not Applicable", null));
        testCases.add(new GetReplacementValueTestCase("na", "NotApplicable", null));
        testCases.add(new GetReplacementValueTestCase("NA", "", null));
        testCases.add(new GetReplacementValueTestCase("[NA]", "  ", null));
        testCases.add(new GetReplacementValueTestCase("N/A", null, null));
        testCases.add(new GetReplacementValueTestCase("na", "", null));
        testCases.add(new GetReplacementValueTestCase("Not Applicable", "  ", null));
        testCases.add(new GetReplacementValueTestCase("NotApplicable", null, null));
        testCases.add(new GetReplacementValueTestCase("", "4", "4"));
        testCases.add(new GetReplacementValueTestCase("  ", "NA", "NA"));
        testCases.add(new GetReplacementValueTestCase(null, "[NA]", "[NA]"));
        testCases.add(new GetReplacementValueTestCase("", "N/A", "N/A"));
        testCases.add(new GetReplacementValueTestCase("  ", "na", "na"));
        testCases.add(new GetReplacementValueTestCase(null, "Not Applicable", "Not Applicable"));
        testCases.add(new GetReplacementValueTestCase("", "NotApplicable", "NotApplicable"));
        testCases.add(new GetReplacementValueTestCase("", "", null)); //when existing value is already empty string, no need to replace
        testCases.add(new GetReplacementValueTestCase("", "  ", null)); //when existing value is already empty string, no need to replace
        testCases.add(new GetReplacementValueTestCase("  ", "", ""));
        testCases.add(new GetReplacementValueTestCase(null, "  ", ""));
        testCases.add(new GetReplacementValueTestCase("", null, null));
        return testCases;
    }
}
