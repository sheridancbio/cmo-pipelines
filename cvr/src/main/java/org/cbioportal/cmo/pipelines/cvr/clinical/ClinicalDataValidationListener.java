/*
 * Copyright (c) 2020 Memorial Sloan-Kettering Cancer Center.
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

package org.cbioportal.cmo.pipelines.cvr.clinical;

import org.apache.log4j.Logger;
import org.cbioportal.cmo.pipelines.cvr.CvrSampleListUtil;
import org.springframework.batch.core.*;
import org.springframework.beans.factory.annotation.*;


public class ClinicalDataValidationListener implements StepExecutionListener {

    private static final double DROP_THRESHOLD = 0.9;

    private Logger log = Logger.getLogger(ClinicalDataValidationListener.class);

    @Value("#{jobParameters[validateClinicalRecordCount]}")
    private boolean validateClinicalRecordCount;

    @Autowired
    public CvrSampleListUtil cvrSampleListUtil;

    @Override
    public void beforeStep(StepExecution se) {
    }

    @Override
    public ExitStatus afterStep(StepExecution se) {
        if (validateClinicalRecordCount) {
            Integer originalSampleCount = cvrSampleListUtil.getOriginalClinicalFileRecordCount();
            Integer newSampleCount = cvrSampleListUtil.getNewClinicalFileRecordCount();
            if (droppedTooMuch(newSampleCount, originalSampleCount)) {
                log.error("Clinical data validation failed : original sample count was " + originalSampleCount +
                        ", and the new sample count was " + newSampleCount +
                        ", which is less than the largest allowable drop (to sample count " + (originalSampleCount * DROP_THRESHOLD) + ")");
                return ExitStatus.FAILED;
            }
        }
        return ExitStatus.COMPLETED;
    }

    private boolean droppedTooMuch(Integer newSampleCount, Integer originalSampleCount) {
        return (newSampleCount < (originalSampleCount * DROP_THRESHOLD));
    }


}
