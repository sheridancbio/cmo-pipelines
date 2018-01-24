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
package org.mskcc.cmo.ks.darwin.pipeline;

import org.cbioportal.cmo.pipelines.common.util.EmailUtil;

import org.mskcc.cmo.ks.darwin.pipeline.model.MskimpactBrainSpineTimeline;
import org.mskcc.cmo.ks.darwin.pipeline.util.DarwinSampleListUtil;

import org.apache.log4j.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 *
 * @author Manda Wilson
 */
@Service
@ComponentScan(basePackages = {"org.mskcc.cmo.ks.darwin.pipeline", "org.cbioportal.cmo.pipelines.common"})
public class DarwinEmailTasklet implements Tasklet {

    @Autowired
    private EmailUtil emailUtil;

    @Autowired
    private DarwinSampleListUtil darwinSampleListUtil;
    
    Logger log = Logger.getLogger(DarwinEmailTasklet.class);

    @Override
    public RepeatStatus execute(StepContribution stepContribution, ChunkContext chunkContext) throws Exception {
        StringBuilder body = new StringBuilder();
        String subject = "Darwin pipeline errors";
        Set<MskimpactBrainSpineTimeline> filteredMskimpactBrainSpineTimelineList = darwinSampleListUtil.getFilteredMskimpactBrainSpineTimelineList();
        if (filteredMskimpactBrainSpineTimelineList.size() > 0) {
            body.append(constructFilteredMskimpactBrainSpineTimelineText(filteredMskimpactBrainSpineTimelineList));
        }
        if (!body.toString().isEmpty()) {
            emailUtil.sendEmailToDefaultRecipient(subject, body.toString());
        }
        return RepeatStatus.FINISHED;
    }

    private String constructFilteredMskimpactBrainSpineTimelineText(Set<MskimpactBrainSpineTimeline> filteredMskimpactBrainSpineTimelineList) {
        log.warn(filteredMskimpactBrainSpineTimelineList.size() + " records from Darwin were filtered"); 
        StringBuilder text = new StringBuilder();
        text.append("\nFiltered ");
        text.append(filteredMskimpactBrainSpineTimelineList.size());
        text.append(" MSK IMPACT Caisis GBM timeline records:\n");
        for (MskimpactBrainSpineTimeline mskimpactBrainSpineTimeline : filteredMskimpactBrainSpineTimelineList) {
            text.append("\n\tPATIENT_ID: ");
            text.append(mskimpactBrainSpineTimeline.getDMP_PATIENT_ID_ALL_BRAINSPINETMLN());
            text.append(", START_DATE: ");
            text.append(mskimpactBrainSpineTimeline.getSTART_DATE());
            text.append(", STOP_DATE: ");
            text.append(mskimpactBrainSpineTimeline.getSTOP_DATE());
            text.append(", EVENT_TYPE: ");
            text.append(mskimpactBrainSpineTimeline.getEVENT_TYPE());
            text.append(", TREATMENT_TYPE: ");
            text.append(mskimpactBrainSpineTimeline.getTREATMENT_TYPE());
        }
        return text.toString();
    }
}
