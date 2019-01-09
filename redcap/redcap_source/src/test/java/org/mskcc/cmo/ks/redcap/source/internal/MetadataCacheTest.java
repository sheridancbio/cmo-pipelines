/*
 * Copyright (c) 2017-2018 Memorial Sloan-Kettering Cancer Center.
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

import java.util.*;
import org.junit.runner.RunWith;
import org.junit.Test;
import org.mskcc.cmo.ks.redcap.source.internal.RedcapSourceTestConfiguration;
import org.mskcc.cmo.ks.redcap.source.internal.MetadataManagerRedcapImpl;
import org.mskcc.cmo.ks.redcap.models.RedcapAttributeMetadata;
import org.mskcc.cmo.ks.redcap.source.internal.MetadataCache;
import org.springframework.beans.factory.annotation.*;
import org.junit.Assert;
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
public class MetadataCacheTest {

    @Autowired  
    private MetadataCache overriddenMetadataCache; 
   
    @Autowired
    private MetadataCache defaultMetadataCache;

    /* Test to check if overrides are properly applied when initializing metadataCache
     * Two caches are autowired - one has overriddenStudyId set to "overridden_study"
     * returned metadata should have different priority for attribute "ETHNICITY"
     */ 
    @Test
    public void testOverridesAppliedToMetadataCache() {
        overriddenMetadataCache.setOverrideStudyId("overridden_study");
        List<RedcapAttributeMetadata> default_metadata = defaultMetadataCache.getMetadata();
        List<RedcapAttributeMetadata> overridden_metadata = overriddenMetadataCache.getMetadata();
        RedcapAttributeMetadata default_attribute = defaultMetadataCache.getMetadataByNormalizedColumnHeader("ETHNICITY");
        RedcapAttributeMetadata overridden_attribute = overriddenMetadataCache.getMetadataByNormalizedColumnHeader("ETHNICITY");
        if (overridden_attribute.getPriority() != "100" || default_attribute.getPriority() != "1") {
            Assert.fail("Override was not applied when cache was initialized. Expected values were: 100, 1.  Returned values were: " + overridden_attribute.getPriority() + ", " + default_attribute.getPriority());
        }
        try {
            overriddenMetadataCache.setOverrideStudyId("second_overridden_study");
            Assert.fail("MetadataCacheset overrideStudyId after metadataCache initialized -- should throw Runtime Exception");
        } catch (RuntimeException e) {
            return;
        }
    }
}
