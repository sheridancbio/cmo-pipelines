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
public class NAACCRSexMapping {
    private Integer NSCM_CODE;
    private String NSCM_CBIOPORTAL_LABEL;

    public NAACCRSexMapping() {}

    public NAACCRSexMapping(Integer NSCM_CODE, String NSCM_CBIOPORTAL_LABEL) {
        this.NSCM_CODE = NSCM_CODE;
        this.NSCM_CBIOPORTAL_LABEL = NSCM_CBIOPORTAL_LABEL;
    }

    /**
     * @return the NSCM_CODE
     */
    public Integer getNSCM_CODE() {
        return NSCM_CODE;
    }

    /**
     * @param NSCM_CODE the NSCM_CODE to set
     */
    public void setNSCM_CODE(Integer NSCM_CODE) {
        this.NSCM_CODE = NSCM_CODE;
    }

    /**
     * @return the NSCM_CBIOPORTAL_LABEL
     */
    public String getNSCM_CBIOPORTAL_LABEL() {
        return NSCM_CBIOPORTAL_LABEL;
    }

    /**
     * @param NSCM_CBIOPORTAL_LABEL the NSCM_CBIOPORTAL_LABEL to set
     */
    public void setNSCM_CBIOPORTAL_LABEL(String NSCM_CBIOPORTAL_LABEL) {
        this.NSCM_CBIOPORTAL_LABEL = NSCM_CBIOPORTAL_LABEL;
    }

}
