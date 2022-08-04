/*
 * Copyright (c) 2022 Memorial Sloan-Kettering Cancer Center.
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

package org.cbioportal.cmo.pipelines.cvr.sv;

import org.apache.log4j.Logger;
import org.cbioportal.cmo.pipelines.cvr.model.staging.CVRSvRecord;
import org.cbioportal.cmo.pipelines.cvr.sv.SvException;

public class SvUtilities {

    Logger log = Logger.getLogger(SvUtilities.class);

    public SvUtilities() {}

    private boolean stringIsNullOrEmpty(String s) {
        return s == null || s.trim().length() == 0;
    }

    /* when processing new sv events fetched from cvr, blank out site2_hugo_symbol if site1_hugo_symbol matches 
     *
     * CVR fetches do not populate site1EntrezGeneId or site2EntrezGeneId, so only site1HugoSymbol and site2HugoSymbol are examined
     *
    */
    public void simplifyIntergenicEventGeneReferences(CVRSvRecord svRecord) throws SvException {
        String site1HugoSymbol = svRecord.getSite1_Hugo_Symbol();
        String site2HugoSymbol = svRecord.getSite2_Hugo_Symbol();
        String sampleId = svRecord.getSample_ID();
        if (stringIsNullOrEmpty(site1HugoSymbol) && stringIsNullOrEmpty(site2HugoSymbol)) { 
            // illegal case ... every sv record must have at least 1 gene specified
            String msg = String.format("attempting to standardize an event from sv-variants for sample %s where both site1HugoSymbol and site2HugoSymbol are null/empty", sampleId == null ? "" : sampleId);
            log.warn(msg);
            throw new SvException(msg);
        }
        if (site1HugoSymbol != null && site2HugoSymbol != null && site1HugoSymbol.trim().equals(site2HugoSymbol.trim())) {
            // blank out site2 gene when it is a duplicate
            svRecord.setSite2_Hugo_Symbol("");
        }
    }

    public boolean eventInfoIsEmpty(CVRSvRecord svRecord) {
        String eventInfo = svRecord.getEvent_Info();
        if (stringIsNullOrEmpty(eventInfo)) {
            return true;
        }
        if (eventInfo.trim().equals("-")) {
            return true;
        }
        return false;
    }

    public void populateEventInfo(CVRSvRecord svRecord) throws SvException {
        String site1HugoSymbol = svRecord.getSite1_Hugo_Symbol();
        String site2HugoSymbol = svRecord.getSite2_Hugo_Symbol();
        String sampleId = svRecord.getSample_ID();
        if (stringIsNullOrEmpty(site1HugoSymbol) && stringIsNullOrEmpty(site2HugoSymbol)) { 
            // illegal case ... every sv record must have at least 1 gene specified
            String msg = String.format("attempting to populate an empty Event_Info field for an event from sv-variants for sample %s where both site1HugoSymbol and site2HugoSymbol are null/empty", sampleId == null ? "" : sampleId);
            log.warn(msg);
            throw new SvException(msg);
        }
        if (stringIsNullOrEmpty(site1HugoSymbol) || stringIsNullOrEmpty(site2HugoSymbol) || site1HugoSymbol.trim().equals(site2HugoSymbol.trim())) {
            // intergenic case
            String hugoSymbol = null;
            if (stringIsNullOrEmpty(site1HugoSymbol)) {
                hugoSymbol = site2HugoSymbol.trim();
            } else {
                hugoSymbol = site1HugoSymbol.trim();
            }
            svRecord.setEvent_Info(String.format("%s-intragenic", hugoSymbol));
        } else {
            svRecord.setEvent_Info(String.format("%s-%s Fusion", site1HugoSymbol.trim(), site2HugoSymbol.trim()));
        }
    }

    public void populateEventInfoWhenEmpty(CVRSvRecord svRecord) throws SvException {
        if (eventInfoIsEmpty(svRecord)) {
            populateEventInfo(svRecord);
        }
    }

}
