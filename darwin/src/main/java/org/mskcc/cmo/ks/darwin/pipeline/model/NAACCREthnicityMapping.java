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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/
package org.mskcc.cmo.ks.darwin.pipeline.model;

/**
 *
 * @author heinsz
 */
public class NAACCREthnicityMapping {
    private Integer NECM_CODE;
    private String NECM_CBIOPORTAL_LABEL;

    public NAACCREthnicityMapping() {}

    public NAACCREthnicityMapping(Integer NECM_CODE, String NECM_CBIOPORTAL_LABEL) {
        this.NECM_CODE = NECM_CODE;
        this.NECM_CBIOPORTAL_LABEL = NECM_CBIOPORTAL_LABEL;
    }

    /**
     * @return the NECM_CODE
     */
    public Integer getNECM_CODE() {
        return NECM_CODE;
    }

    /**
     * @param NECM_CODE the NECM_CODE to set
     */
    public void setNECM_CODE(Integer NECM_CODE) {
        this.NECM_CODE = NECM_CODE;
    }

    /**
     * @return the NECM_CBIOPORTAL_LABEL
     */
    public String getNECM_CBIOPORTAL_LABEL() {
        return NECM_CBIOPORTAL_LABEL;
    }

    /**
     * @param NECM_CBIOPORTAL_LABEL the NECM_CBIOPORTAL_LABEL to set
     */
    public void setNECM_CBIOPORTAL_LABEL(String NECM_CBIOPORTAL_LABEL) {
        this.NECM_CBIOPORTAL_LABEL = NECM_CBIOPORTAL_LABEL;
    }
}
