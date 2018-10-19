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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/

package org.mskcc.cmo.ks.darwin.pipeline.util;

import org.mskcc.cmo.ks.darwin.pipeline.model.MskimpactPatientDemographics;

import java.util.List;
import java.util.ArrayList;

public class VitalStatusUtils {

    private final static int EIGHTYNINE_YEARS_OLD_IN_DAYS = 32485;
    private final static int EIGHTEEN_YEARS_OLD_IN_DAYS = 6570;

    /**
     * Generates a row of vital status information.  Note, the rules this method follows has been dictated by the vital 
     * status file format defined by the GENIE clinical data working group consortium:
     * https://www.synapse.org/#!Synapse:syn3380222/wiki/495552 (login required)
     */
    public static List<String> getVitalStatusResult(DarwinUtils darwinUtils, final MskimpactPatientDemographics darwinPatientDemographics) throws Exception {
        List<String> toReturn = new ArrayList<>();
        for (String field : MskimpactPatientDemographics.getVitalStatusFieldNames()) {
            Object value = darwinPatientDemographics.getClass().getMethod("get"+field).invoke(darwinPatientDemographics);
            if (value == null) {
                toReturn.add("NA");
            }
            else {
                String valueToReturn = darwinUtils.convertWhitespace(value.toString());
                switch(field) {
                case "DMP_ID_DEMO":
                    // nothing to be done
                    break;
                case "LAST_CONTACT_YEAR":
                case "PT_DEATH_YEAR":
                    if (valueToReturn.equals("-1")) {
                        valueToReturn = "NA";
                    }
                    break;
                case "AGE_AT_LAST_CONTACT_YEAR_IN_DAYS":
                case "AGE_AT_DATE_OF_DEATH_IN_DAYS":
                    if (Integer.valueOf(valueToReturn) > EIGHTYNINE_YEARS_OLD_IN_DAYS) {
                        valueToReturn = ">32485";
                    } else if (Integer.valueOf(valueToReturn) < EIGHTEEN_YEARS_OLD_IN_DAYS) {
                        valueToReturn = "<6570";
                    }
                    break;
                case "OS_STATUS":
                    // OS_STATUS becomes "DEAD" in file
                    if (valueToReturn.equals("LIVING")) {
                        valueToReturn = "False";
                    }
                    else if (valueToReturn.equals("DECEASED")) {
                        valueToReturn = "True";
                    }
                    break;
                default:
                    throw new Exception("Unknown Vital Status field name: " + field);
                }
                toReturn.add(valueToReturn);
            }
        }
        return toReturn;
    }
}
