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

import java.io.*;
import java.net.*;
import java.util.*;
import org.junit.Assert;
import org.junit.runner.RunWith;
import org.junit.Test;
import org.mskcc.cmo.ks.redcap.source.ClinicalDataSource;
import org.mskcc.cmo.ks.redcap.source.internal.RedcapSourceTestConfiguration;
import org.springframework.beans.factory.annotation.*;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.TestPropertySource;

@TestPropertySource(
    properties = { "redcap.batch.size=5"
    },
    inheritLocations = false
)
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes=RedcapSourceTestConfiguration.class)
public class ClinicalDataSourceTest {

    @Autowired
    private ClinicalDataSource clinicalDataSource;

    /* This test mocks the RedcapSessionManager to add a simple project with fields {SAMPLE_ID, PATIENT_ID, NECROSIS, ETHNICITY}
     * NECROSIS is a sample attribute, ETHNICITY is a patient attribute. (See org.mskcc.cmo.ks.redcap.source.internal.RedcapSourceTestConfiguration)
     * Also mocked is appropriate metadata for all these fields and the token-finding services.
     * This test expects proper separation of the sample and patient headers and output of:
     * Sample headers = ["SAMPLE_ID", "PATIENT_ID", "NECROSIS"]
     * The same mock and setup is used in testGetPatientHeader()
     */
    @Test
    public void testGetSampleHeader() {
        if (clinicalDataSource == null) {
            Assert.fail("clinicalDataSource was not initialized properly for testing");
        }
        clinicalDataSource.getNextClinicalProjectTitle(RedcapSourceTestConfiguration.SIMPLE_MIXED_TYPE_CLINICAL_STABLE_ID);
        List<String> dataReturned = clinicalDataSource.getSampleHeader(RedcapSourceTestConfiguration.SIMPLE_MIXED_TYPE_CLINICAL_STABLE_ID);
        String[] expectedReturnValueArray = { "SAMPLE_ID", "PATIENT_ID", "NECROSIS" };
        Set<String> expectedReturnValueSet = new HashSet<>(Arrays.asList(expectedReturnValueArray));
        String differenceBetweenReturnedAndExpected = getDifferenceBetweenReturnedAndExpected(dataReturned, expectedReturnValueSet);
        if (differenceBetweenReturnedAndExpected != null && differenceBetweenReturnedAndExpected.length() > 0) {
            Assert.fail("difference between returned and expected values: " + differenceBetweenReturnedAndExpected);
        }
    }

    /* This test mocks the RedcapSessionManager to add a simple project with fields {SAMPLE_ID, PATIENT_ID, NECROSIS, ETHNICITY}
     * NECROSIS is a sample attribute, ETHNICITY is a patient attribute. (See org.mskcc.cmo.ks.redcap.source.internal.RedcapSourceTestConfiguration)
     * Also mocked is appropriate metadata for all these fields and the token-finding services.
     * This test expects proper separation of the sample and patient headers and output of:
     * Patient headers = ["PATIENT_ID", "ETHNICITY"]
     */
    @Test
    public void testGetPatientHeader() {
        if (clinicalDataSource == null) {
            Assert.fail("clinicalDataSource was not initialized properly for testing");
        }
        clinicalDataSource.getNextClinicalProjectTitle(RedcapSourceTestConfiguration.SIMPLE_MIXED_TYPE_CLINICAL_STABLE_ID);
        List<String> dataReturned = clinicalDataSource.getPatientHeader(RedcapSourceTestConfiguration.SIMPLE_MIXED_TYPE_CLINICAL_STABLE_ID);
        String[] expectedReturnValueArray = { "PATIENT_ID", "ETHNICITY" };
        Set<String> expectedReturnValueSet = new HashSet<>(Arrays.asList(expectedReturnValueArray));
        String differenceBetweenReturnedAndExpected = getDifferenceBetweenReturnedAndExpected(dataReturned, expectedReturnValueSet);
        if (differenceBetweenReturnedAndExpected != null && differenceBetweenReturnedAndExpected.length() > 0) {
            Assert.fail("difference between returned and expected values: " + differenceBetweenReturnedAndExpected);
        }
    }

    private String getDifferenceBetweenReturnedAndExpected(List<String> dataReturnedList, Set<String> expectedReturnValueSet) {
        if (dataReturnedList == null) {
            return "Data returned was null";
        }
        if (expectedReturnValueSet == null) {
            return "Expected return value was null";
        }
        Set<String> dataReturnedSet = new HashSet<>(dataReturnedList);
        if (dataReturnedSet.size() != expectedReturnValueSet.size()) {
            return "Different number of records returned (" + Integer.toString(dataReturnedSet.size()) + ") versus expected (" + Integer.toString(expectedReturnValueSet.size()) + ")";
        }
        StringBuilder result = new StringBuilder();
        for (String dataReturnedItem : dataReturnedSet) {
            if (!expectedReturnValueSet.contains(dataReturnedItem)) {
                result.append("Header field '" + dataReturnedItem + "' returned but expected value set does not contain that field name");
            }
        }
        return result.toString();
    }

}
