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

import java.util.ArrayList;
import java.util.List;
import org.mskcc.cmo.ks.gene.jdbc.internal.DaoGeneJdbcImpl;
import org.mskcc.cmo.ks.gene.model.Gene;
import org.mskcc.cmo.ks.gene.model.GeneAlias;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemStreamException;
import org.springframework.batch.item.ItemStreamWriter;
import org.springframework.beans.factory.annotation.Autowired;

/**
 *
 * @author ochoaa
 */
public class GeneDataWriter implements ItemStreamWriter<Gene> {

    @Autowired
    private DaoGeneJdbcImpl daoGeneJdbcImpl;
    
    private int genesAdded;
    private int genesUpdated;
    private int geneAliasesAdded; 
    
    @Override
    public void open(ExecutionContext executionContext) throws ItemStreamException {}

    @Override
    public void update(ExecutionContext executionContext) throws ItemStreamException {
        executionContext.put("genesAdded", genesAdded);
        executionContext.put("genesUpdated", genesUpdated);
        executionContext.put("geneAliasesAdded", geneAliasesAdded);
    }

    @Override
    public void close() throws ItemStreamException {}

    @Override
    public void write(List<? extends Gene> list) throws Exception {
        List<Gene> newGenes = new ArrayList();
        List<Gene> genesToUpdate = new ArrayList();
        List<GeneAlias> aliasesToUpdate = new ArrayList();
        
        for (Gene gene : list) {
            if (gene.getGeneticEntityId() == -1) {
                gene.setGeneticEntityId(daoGeneJdbcImpl.addGeneGeneticEntity());
                newGenes.add(gene);
            }
            else {
                genesToUpdate.add(gene);                
            }
            aliasesToUpdate.addAll(gene.getAliases());
        }
        
        this.genesAdded += daoGeneJdbcImpl.batchInsertGene(newGenes);
        this.genesUpdated += daoGeneJdbcImpl.batchUpdateGene(genesToUpdate);
        this.geneAliasesAdded += daoGeneJdbcImpl.batchInsertGeneAlias(aliasesToUpdate);
    }
    
}
