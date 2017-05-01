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

import java.util.*;
import java.io.*;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.*;
import org.springframework.batch.core.*;
import org.springframework.batch.item.ItemStreamException;
import org.springframework.beans.factory.annotation.Value;

/**
 *
 * @author ochoaa
 */
public class GeneDataListener implements StepExecutionListener {

    @Value("${DATABASE_NAME}")
    private String DATABASE_NAME;
    
    private static final Log LOG = LogFactory.getLog(GeneDataListener.class);
    
    @Override
    public void beforeStep(StepExecution stepExecution) {}

    @Override
    public ExitStatus afterStep(StepExecution stepExecution) {
        String notificationFileName = (String) stepExecution.getJobParameters().getString("notificationFileName");
        
        int genesAdded = (int) stepExecution.getExecutionContext().getInt("genesAdded");
        int genesUpdated = (int) stepExecution.getExecutionContext().getInt("genesUpdated");
        int geneAliasesAdded = (int) stepExecution.getExecutionContext().getInt("geneAliasesAdded");
        
        List<String> results = new ArrayList();
        if ((genesAdded + genesUpdated + geneAliasesAdded) == 0) {
            results.add("Nothing to update in database: " + DATABASE_NAME);            
        }
        else {
            results = Arrays.asList(new String[] {"Gene update results for database: " + DATABASE_NAME,
                                    "\tNew genes added to database: " + String.valueOf(genesAdded), 
                                    "\tTotal genes updated in database: " + String.valueOf(genesUpdated),
                                    "\tNew aliases added to database: " + String.valueOf(geneAliasesAdded)});
        }
        LOG.info("\n" + StringUtils.join(results, "\n"));
        
        if (notificationFileName != null) {
            File notificationFile = new File(notificationFileName);
            try {
                org.apache.commons.io.FileUtils.writeLines(notificationFile, results);
            } catch (IOException ex) {
                LOG.error("Error writing results to notification file!");
                throw new ItemStreamException(ex);
            }
        }
        
         return ExitStatus.COMPLETED;   
    }
    
}
