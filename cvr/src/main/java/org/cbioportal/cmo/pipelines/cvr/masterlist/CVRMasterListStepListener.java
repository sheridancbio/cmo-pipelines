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

package org.cbioportal.cmo.pipelines.cvr.masterlist;

import java.util.List;
import org.apache.log4j.Logger;
import org.cbioportal.cmo.pipelines.cvr.EmailUtil;
import org.cbioportal.cmo.pipelines.cvr.model.CVRRequeueRecord;
import org.springframework.batch.core.*;
import org.springframework.beans.factory.annotation.Autowired;

/**
 *
 * @author Manda Wilson
 */
public class CVRMasterListStepListener implements StepExecutionListener {

    Logger log = Logger.getLogger(CVRMasterListStepListener.class);
        
    @Autowired
    private EmailUtil emailUtil;
    
    @Override
    public void beforeStep(StepExecution stepExecution) {
    }

    @Override
    public ExitStatus afterStep(StepExecution stepExecution) {
        log.debug("afterStep(): checking for portal samples not in dmp or for samples that failed to requeue...");

        List<String> portalSamplesNotInDmp = (List<String>) stepExecution.getExecutionContext().get("portalSamplesNotInDmp");
        List<CVRRequeueRecord> failedToRequeueSamples = (List<CVRRequeueRecord>) stepExecution.getExecutionContext().get("failedToRequeueSamples");
    
        if ((portalSamplesNotInDmp != null && portalSamplesNotInDmp.size() > 0) ||
            (failedToRequeueSamples != null && failedToRequeueSamples.size() > 0)) {
            String subject = "CVR pipeline master list errors";
            StringBuilder body = new StringBuilder();

            if (failedToRequeueSamples != null && failedToRequeueSamples.size() > 0) {
                log.warn(failedToRequeueSamples.size() + " samples from the dmp master list failed to requeue");
                body.append("\nSamples that failed to requeue:\n");
                for (CVRRequeueRecord requeueRecord : failedToRequeueSamples) {
                    body.append("\n\t");
                    body.append(requeueRecord.getSampleId());
                    body.append(": ");
                    body.append(requeueRecord.getResult());
                    body.append(" result; '");
                    body.append(requeueRecord.getInformation());
                    body.append("'");
                }
                body.append("\n");
            }

            if (portalSamplesNotInDmp != null && portalSamplesNotInDmp.size() > 0) {
                log.warn(portalSamplesNotInDmp.size() + " portal samples are not in the dmp master list");
                body.append("\nPortal samples not in DMP master list:\n");
                for (String sample : portalSamplesNotInDmp) {
                    body.append("\n\t");
                    body.append(sample);
                    log.warn("Portal sample '" + sample + "' is not in the dmp master list");
                }
                body.append("\n");
            }

            emailUtil.sendEmail(subject, body.toString());
        }
 
        return ExitStatus.COMPLETED;
    }
    
}
