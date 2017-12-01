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

package org.mskcc.cmo.ks.redcap.pipeline.util;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;

/**
 *
 * @author ochoaa
 */
public class MetaFileUtil {
    public static String CLINICAL_SAMPLE_DATATYPE = "SAMPLE_ATTRIBUTES";
    public static String CLINICAL_PATIENT_DATATYPE = "PATIENT_ATTRIBUTES";
    public static String TIMELINE_DATATYPE = "TIMELINE";
    
    /**
     * Creates meta clinical file for patient or sample data.
     * @param directory
     * @param clinicalFilename
     * @param datatype
     * @param cancerStudyIdentifier
     * @throws IOException 
     */
    public static void createMetaClinicalFile(String directory, String clinicalFilename, String datatype, String cancerStudyIdentifier) throws IOException {
        createMetaFile(directory, clinicalFilename, datatype, "clinical", cancerStudyIdentifier);
    }
    
    /**
     * Creates timeline meta file.
     * @param directory
     * @param clinicalFilename
     * @param cancerStudyIdentifier
     * @throws IOException 
     */
    public static void createMetaTimelineFile(String directory, String clinicalFilename, String cancerStudyIdentifier) throws IOException {
        createMetaFile(directory, clinicalFilename, TIMELINE_DATATYPE, "timeline", cancerStudyIdentifier);
    }
    
    /**
     * Meta file writer helper function.
     * @param directory
     * @param clinicalFilename
     * @param datatype
     * @param replacementContext
     * @param cancerStudyIdentifier
     * @throws IOException 
     */
    private static void createMetaFile(String directory, String clinicalFilename, String datatype, String replacementContext, String cancerStudyIdentifier) throws IOException {
        List<String> rows = new ArrayList<>();
        rows.add("cancer_study_identifier: " + cancerStudyIdentifier);
        rows.add("genetic_alteration_type: CLINICAL");
        rows.add("datatype: " + datatype);
        rows.add("data_filename: " + clinicalFilename);
        Path file = Paths.get(directory, clinicalFilename.replace("data_" + replacementContext, "meta_" + replacementContext));
        Files.write(file, rows, StandardCharsets.UTF_8);
    }
}