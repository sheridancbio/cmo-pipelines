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

package org.mskcc.cmo.ks.darwin.pipeline.mskimpactbrainspinetimeline;

import java.io.*;
import org.apache.log4j.Logger;
import org.mskcc.cmo.ks.redcap.source.ClinicalDataSource;
import org.springframework.batch.core.*;
import org.springframework.beans.factory.annotation.*;

public class MskimpactTimelineBrainSpineListener implements StepExecutionListener {

    Logger log = Logger.getLogger(MskimpactTimelineBrainSpineListener.class);

    @Autowired
    public ClinicalDataSource clinicalDataSource;

    @Value("${darwin.brainspine_timeline_filename_prefix}")
    private String darwinBrainSpineTimelineFilenamePrefix;

    @Value("${darwin.brainspine_timeline_filename_suffix}")
    private String darwinBrainSpineTimelineFilenameSuffix;

    @Value("${redcap.project_title_for_darwin_mskimpact_brain_spine_timeline_import_prefix}")
    public String redcapProjectTitlePrefix;

    @Value("${redcap.project_title_for_darwin_mskimpact_brain_spine_timeline_import_suffix}")
    public String redcapProjectTitleSuffix;

    @Override
    public void beforeStep(StepExecution stepExecution) {
    }

    private void persistStagingFile(File stagingFile, String projectTitle) {
        if (stagingFile.exists()) {
            try {
                String stagingFilename = stagingFile.getCanonicalPath();
                clinicalDataSource.importClinicalDataFile(projectTitle, stagingFilename, true);
            } catch (IOException e) {
                log.error("Error: could not persist clinical file \"" + stagingFile.getPath() + "\" to RedCap : IO error locating/reading file");
            }
        } else {
            log.error("Error: could not persist clinical file \"" + stagingFile.getPath() + "\" to RedCap : file does not exist");
        }
    }

    private String stagingFilenameFromType(BrainSpineTimelineType type) {
        return darwinBrainSpineTimelineFilenamePrefix + type.toString().toLowerCase() + darwinBrainSpineTimelineFilenameSuffix;
    }

    private String redcapProjectTitleFromType(BrainSpineTimelineType type) {
        return redcapProjectTitlePrefix + type.toString().toLowerCase() + redcapProjectTitleSuffix;
    }

    @Override
    public ExitStatus afterStep(StepExecution stepExecution) {
        String outputDirectory = stepExecution.getJobExecution().getJobParameters().getString("outputDirectory");
        for (BrainSpineTimelineType type : BrainSpineTimelineType.values()) {
            File brainSpineTimelineFilename = new File(outputDirectory, stagingFilenameFromType(type));
            String redcapProjectTitle = redcapProjectTitleFromType(type);
            persistStagingFile(brainSpineTimelineFilename, redcapProjectTitle);
        }
        return ExitStatus.COMPLETED;
    }
}
