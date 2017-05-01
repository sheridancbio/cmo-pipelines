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

package org.mskcc.cmo.ks.gene;

import org.mskcc.cmo.ks.gene.model.Gene;
import org.mskcc.cmo.ks.gene.model.GeneAlias;

import java.util.*;
import javax.annotation.Resource;
import org.springframework.batch.item.ItemProcessor;

/**
 *
 * @author ochoaa
 */
public class GeneDataProcessor implements ItemProcessor<Gene, Gene> {

    @Resource(name="entrezGeneIdMap")
    private Map<Integer, Gene> entrezGeneIdMap;
    
    @Override
    public Gene process(Gene geneRecord) throws Exception {
        // check entrezGeneIdMap if gene exists - if does not exist then set genetic entity id to -1 and return gene record
        // otherwise, set genetic entity id to the existing gene genetic entity id from database
        Gene existingGene = entrezGeneIdMap.get(geneRecord.getEntrezGeneId());
        if (existingGene == null) {
            geneRecord.setGeneticEntityId(-1);
            return geneRecord;
        }
        else {
            geneRecord.setGeneticEntityId(existingGene.getGeneticEntityId());
        }
        // if gene needs updating then return updated gene record, otherwise return null to filter out record from GeneDataWriter
        if (geneNeedsUpdating(geneRecord, existingGene)) {
            return geneRecord;
        }
        return null;
    }
    
    /**
     * Determines whether gene record needs updating in the database.
     * 1. Compare hugo symbols - if not equal then hugo symbol from database is added as an alias to current gene record
     * 2. Compare gene aliases - if not equal then remove aliases from current gene record that already exist in database. 
     *      Only new aliases should remain in set for current gene record. 
     * 3. Compare the following:
     *      - if size of current set of gene record aliases is greater than 1
     *      - if gene type doesn't match what's in the database
     *      - if gene cytoband doesn't match what's in the database
     *      - if gene length doesn't match what's in the database
     * 
     * @param geneRecord
     * @param existingGene
     * @return 
     */
    private boolean geneNeedsUpdating(Gene geneRecord, Gene existingGene) {
        boolean needsUpdating = false;
        // update gene aliases for current gene record if hugo symbols don't match
        if (!geneRecord.getHugoGeneSymbol().equalsIgnoreCase(existingGene.getHugoGeneSymbol())) {
            // assuming that we do not have alias in `gene_alias` table for current entrez id and existing gene hugo symbol
            geneRecord.getAliases().add(new GeneAlias(geneRecord.getEntrezGeneId(), existingGene.getHugoGeneSymbol()));
            needsUpdating = true;
        }
        // make sure that we remove any GeneAlias objects from current gene record that already exist in database
        for (GeneAlias alias : existingGene.getAliases()) {
            if (geneRecord.getAliases().contains(alias)) {
                geneRecord.getAliases().remove(alias);
            }
        }
        
        // set update to true if any of these conditions are met
        if (geneRecord.getAliases().size() > 1 || !geneRecord.getType().equals(existingGene.getType()) || 
                !geneRecord.getCytoband().equals(existingGene.getCytoband()) || !Objects.equals(geneRecord.getLength(), existingGene.getLength())) {
            needsUpdating = true;
        }
        return needsUpdating;
    }
}
