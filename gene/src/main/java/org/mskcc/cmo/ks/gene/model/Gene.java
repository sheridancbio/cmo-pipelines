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

import java.util.Set;

/**
 *
 * @author ochoaa
 */
public class Gene {
    
    private Integer entrezGeneId;
    private String hugoGeneSymbol;
    private Integer geneticEntityId;
    private String type;
    private String cytoband;
    private Integer length;
    private Set<GeneAlias> aliases;
    
    public Gene() {}
    
    public Gene(Integer entrezGeneId, String hugoGeneSymbol, String type, String cytoband, Integer length, Set<GeneAlias> aliases) {
        this.entrezGeneId = entrezGeneId;
        this.hugoGeneSymbol = hugoGeneSymbol;
        this.type = type;
        this.cytoband = cytoband;
        this.length = length;
        this.aliases = aliases;
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
     * @return the hugoGeneSymbol
     */
    public String getHugoGeneSymbol() {
        return hugoGeneSymbol;
    }

    /**
     * @param hugoGeneSymbol the hugoGeneSymbol to set
     */
    public void setHugoGeneSymbol(String hugoGeneSymbol) {
        this.hugoGeneSymbol = hugoGeneSymbol;
    }

    /**
     * @return the geneticEntityId
     */
    public Integer getGeneticEntityId() {
        return geneticEntityId;
    }

    /**
     * @param geneticEntityId the geneticEntityId to set
     */
    public void setGeneticEntityId(Integer geneticEntityId) {
        this.geneticEntityId = geneticEntityId;
    }

    /**
     * @return the type
     */
    public String getType() {
        return type;
    }

    /**
     * @param type the type to set
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * @return the cytoband
     */
    public String getCytoband() {
        return cytoband;
    }

    /**
     * @param cytoband the cytoband to set
     */
    public void setCytoband(String cytoband) {
        this.cytoband = cytoband;
    }

    /**
     * @return the length
     */
    public Integer getLength() {
        return length;
    }

    /**
     * @param length the length to set
     */
    public void setLength(Integer length) {
        this.length = length;
    }

    /**
     * @return the aliases
     */
    public Set<GeneAlias> getAliases() {
        return aliases;
    }

    /**
     * @param aliases the aliases to set
     */
    public void setAliases(Set<GeneAlias> aliases) {
        this.aliases = aliases;
    }
    
}
