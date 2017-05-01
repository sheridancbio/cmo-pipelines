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

package org.mskcc.cmo.ks.gene.model;

import java.util.Objects;

/**
 *
 * @author ochoaa
 */
public class GeneAlias {
    
    private Integer entrezGeneId;
    private String geneAlias;
    
    public GeneAlias() {}
    
    public GeneAlias(Integer entrezGeneId, String geneAlias) {
        this.entrezGeneId = entrezGeneId;
        this.geneAlias = geneAlias;
    }

    /**
     * @return the entrezGeneId
     */
    public Integer getEntrezGeneId() {
        return entrezGeneId;
    }

    /**
     * @param entrezGeneId the entrezGeneId to set
     */
    public void setEntrezGeneId(Integer entrezGeneId) {
        this.entrezGeneId = entrezGeneId;
    }

    /**
     * @return the geneAlias
     */
    public String getGeneAlias() {
        return geneAlias;
    }

    /**
     * @param geneAlias the geneAlias to set
     */
    public void setGeneAlias(String geneAlias) {
        this.geneAlias = geneAlias;
    }

    @Override
    public int hashCode() {
            int hash = 3;
            hash = 53 * hash + (this.geneAlias != null ? this.geneAlias.hashCode() : 0);
            hash = 53 * hash + (this.entrezGeneId != null ? this.entrezGeneId : 0);
            return hash;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (!GeneAlias.class.isAssignableFrom(obj.getClass())) {
            return false;
        }
        final GeneAlias other = (GeneAlias) obj;
        if (!Objects.equals(this.entrezGeneId, other.entrezGeneId)) {
            return false;
        }
        if ((this.geneAlias == null ) ? (other.geneAlias != null) : !this.geneAlias.equals(other.geneAlias)) {
            return false;
        }
        return true;
    }
}
