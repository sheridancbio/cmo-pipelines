/*
 * Copyright (c) 2022 Memorial Sloan Kettering Cancer Center.
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

package org.cbioportal.cmo.pipelines.cvr;

import org.cbioportal.cmo.pipelines.cvr.model.staging.CVRSvRecord;
import org.cbioportal.cmo.pipelines.cvr.sv.SvException;
import org.cbioportal.cmo.pipelines.cvr.sv.SvUtilities;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@ContextConfiguration(classes=CvrTestConfiguration.class)
@RunWith(SpringJUnit4ClassRunner.class)
public class SvUtilitiesTest {

    @Autowired
    public SvUtilities svUtilities;

    // define strings as separate objects (to catch "==" v. ".equals()" errors)
    private String SITE1NULL = null;
    private String SITE1EMPTY = new String("");
    private String SITE1SOME = new String("SOMEGENE");
    private String SITE1OTHER = new String("OTHERGENE");
    private String SITE2NULL = null;
    private String SITE2EMPTY = new String("");
    private String SITE2SOME = new String("SOMEGENE");
    private String SITE2OTHER = new String("OTHERGENE");

    private CVRSvRecord makeSvRecord(String site1HugoSymbol, String site2HugoSymbol) {
        CVRSvRecord cvrSvRecord = new CVRSvRecord();
        cvrSvRecord.setSite1_Hugo_Symbol(site1HugoSymbol);
        cvrSvRecord.setSite2_Hugo_Symbol(site2HugoSymbol);
        return cvrSvRecord;
    }

    @Test(expected = SvException.class)
    public void testSimplifyIntergenicEventGeneReferencesBothNull() throws Exception {
        CVRSvRecord invalidSvRecord = makeSvRecord(SITE1NULL, SITE2NULL);
        svUtilities.simplifyIntergenicEventGeneReferences(invalidSvRecord);
    }

    @Test(expected = SvException.class)
    public void testSimplifyIntergenicEventGeneReferencesBothEmpty() throws Exception {
        CVRSvRecord invalidSvRecord = makeSvRecord(SITE1EMPTY, SITE2EMPTY);
        svUtilities.simplifyIntergenicEventGeneReferences(invalidSvRecord);
    }

    @Test(expected = SvException.class)
    public void testSimplifyIntergenicEventGeneReferencesNullAndEmpty() throws Exception {
        CVRSvRecord invalidSvRecord = makeSvRecord(SITE1NULL, SITE2EMPTY);
        svUtilities.simplifyIntergenicEventGeneReferences(invalidSvRecord);
    }

    @Test
    public void testSimplifyIntergenicEventGeneReferences1Null() throws Exception {
        CVRSvRecord testSvRecord = makeSvRecord(SITE1NULL, SITE2SOME);
        svUtilities.simplifyIntergenicEventGeneReferences(testSvRecord);
        Assert.assertEquals(testSvRecord.getSite1_Hugo_Symbol(), ""); // null genes reported as empty string in model getters
        Assert.assertEquals(testSvRecord.getSite2_Hugo_Symbol(), "SOMEGENE");
        
    }

    @Test
    public void testSimplifyIntergenicEventGeneReferences1Empty() throws Exception {
        CVRSvRecord testSvRecord = makeSvRecord(SITE1EMPTY, SITE2SOME);
        svUtilities.simplifyIntergenicEventGeneReferences(testSvRecord);
        Assert.assertEquals(testSvRecord.getSite1_Hugo_Symbol(), "");
        Assert.assertEquals(testSvRecord.getSite2_Hugo_Symbol(), "SOMEGENE");
    }

    @Test
    public void testSimplifyIntergenicEventGeneReferences2Null() throws Exception {
        CVRSvRecord testSvRecord = makeSvRecord(SITE1SOME, SITE2NULL);
        svUtilities.simplifyIntergenicEventGeneReferences(testSvRecord);
        Assert.assertEquals(testSvRecord.getSite1_Hugo_Symbol(), "SOMEGENE");
        Assert.assertEquals(testSvRecord.getSite2_Hugo_Symbol(), ""); // null genes reported as empty string in model getters
        
    }

    @Test
    public void testSimplifyIntergenicEventGeneReferences2Empty() throws Exception {
        CVRSvRecord testSvRecord = makeSvRecord(SITE1SOME, SITE2EMPTY);
        svUtilities.simplifyIntergenicEventGeneReferences(testSvRecord);
        Assert.assertEquals(testSvRecord.getSite1_Hugo_Symbol(), "SOMEGENE");
        Assert.assertEquals(testSvRecord.getSite2_Hugo_Symbol(), "");
    }

    @Test
    public void testSimplifyIntergenicEventGeneReferencesBothGenesMatch() throws Exception {
        CVRSvRecord testSvRecord = makeSvRecord(SITE1SOME, SITE2SOME);
        svUtilities.simplifyIntergenicEventGeneReferences(testSvRecord);
        Assert.assertEquals(testSvRecord.getSite1_Hugo_Symbol(), "SOMEGENE");
        Assert.assertEquals(testSvRecord.getSite2_Hugo_Symbol(), "");
    }

    @Test
    public void testSimplifyIntergenicEventGeneReferencesGenesDiffer() throws Exception {
        CVRSvRecord testSvRecord = makeSvRecord(SITE1SOME, SITE2OTHER);
        svUtilities.simplifyIntergenicEventGeneReferences(testSvRecord);
        Assert.assertEquals(testSvRecord.getSite1_Hugo_Symbol(), "SOMEGENE");
        Assert.assertEquals(testSvRecord.getSite2_Hugo_Symbol(), "OTHERGENE");
    }

    @Test
    public void testEventInfoIsEmptyNull() {
        CVRSvRecord testSvRecord = new CVRSvRecord();
        testSvRecord.setEvent_Info(null);
        Assert.assertTrue(svUtilities.eventInfoIsEmpty(testSvRecord));
    }

    @Test
    public void testEventInfoIsEmptyEmpty() {
        CVRSvRecord testSvRecord = new CVRSvRecord();
        testSvRecord.setEvent_Info("");
        Assert.assertTrue(svUtilities.eventInfoIsEmpty(testSvRecord));
    }

    @Test
    public void testEventInfoIsEmptyDash() {
        CVRSvRecord testSvRecord = new CVRSvRecord();
        testSvRecord.setEvent_Info("-");
        Assert.assertTrue(svUtilities.eventInfoIsEmpty(testSvRecord));
    }

    @Test
    public void testEventInfoIsEmptyOther() {
        CVRSvRecord testSvRecord = new CVRSvRecord();
        testSvRecord.setEvent_Info("SOMETHING");
        Assert.assertFalse(svUtilities.eventInfoIsEmpty(testSvRecord));
    }

    @Test(expected = SvException.class)
    public void testPopulateEventInfoGeneReferencesBothNull() throws Exception {
        CVRSvRecord invalidSvRecord = makeSvRecord(SITE1NULL, SITE2NULL);
        svUtilities.populateEventInfo(invalidSvRecord);
    }

    @Test(expected = SvException.class)
    public void testPopulateEventInfoGeneReferencesBothEmpty() throws Exception {
        CVRSvRecord invalidSvRecord = makeSvRecord(SITE1EMPTY, SITE2EMPTY);
        svUtilities.populateEventInfo(invalidSvRecord);
    }

    @Test(expected = SvException.class)
    public void testPopulateEventInfoGeneReferencesNullAndEmpty() throws Exception {
        CVRSvRecord invalidSvRecord = makeSvRecord(SITE1NULL, SITE2EMPTY);
        svUtilities.populateEventInfo(invalidSvRecord);
    }

    @Test
    public void testpopulateEventInfo1Null() throws Exception {
        CVRSvRecord testSvRecord = makeSvRecord(SITE1NULL, SITE2SOME);
        svUtilities.populateEventInfo(testSvRecord);
        Assert.assertEquals(testSvRecord.getEvent_Info(), "SOMEGENE-intragenic");
        
    }

    @Test
    public void testpopulateEventInfo1Empty() throws Exception {
        CVRSvRecord testSvRecord = makeSvRecord(SITE1EMPTY, SITE2SOME);
        svUtilities.populateEventInfo(testSvRecord);
        Assert.assertEquals(testSvRecord.getEvent_Info(), "SOMEGENE-intragenic");
    }

    @Test
    public void testpopulateEventInfo2Null() throws Exception {
        CVRSvRecord testSvRecord = makeSvRecord(SITE1SOME, SITE2NULL);
        svUtilities.populateEventInfo(testSvRecord);
        Assert.assertEquals(testSvRecord.getEvent_Info(), "SOMEGENE-intragenic");
        
    }

    @Test
    public void testpopulateEventInfo2Empty() throws Exception {
        CVRSvRecord testSvRecord = makeSvRecord(SITE1SOME, SITE2EMPTY);
        svUtilities.populateEventInfo(testSvRecord);
        Assert.assertEquals(testSvRecord.getEvent_Info(), "SOMEGENE-intragenic");
    }

    @Test
    public void testpopulateEventInfoBothGenesMatch() throws Exception {
        CVRSvRecord testSvRecord = makeSvRecord(SITE1SOME, SITE2SOME);
        svUtilities.populateEventInfo(testSvRecord);
        Assert.assertEquals(testSvRecord.getEvent_Info(), "SOMEGENE-intragenic");
    }

    @Test
    public void testpopulateEventInfoGenesDiffer() throws Exception {
        CVRSvRecord testSvRecord = makeSvRecord(SITE1SOME, SITE2OTHER);
        svUtilities.populateEventInfo(testSvRecord);
        Assert.assertEquals(testSvRecord.getEvent_Info(), "SOMEGENE-OTHERGENE Fusion");
    }

}
