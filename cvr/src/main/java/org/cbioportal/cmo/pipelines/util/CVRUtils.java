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

package org.cbioportal.cmo.pipelines.util;

import java.io.*;
import java.util.*;

public class CVRUtils {

    private String METADATA_PREFIX = "#";
    private String DELIMITER = "\t";

    public CVRUtils() {}

    public String convertWhitespace(String s) {
        return s.replaceAll("^[\\t|\\n|\\r]+", "").replaceAll("[\\t|\\n|\\r]+$", "").replaceAll("[\\t|\\n|\\r]+", " ");
    }

    public String[] getFileHeader(File dataFile) throws IOException {
        String[] columnNames;

        try (FileReader reader = new FileReader(dataFile)) {
            BufferedReader buff = new BufferedReader(reader);
            String line = buff.readLine();

            // keep reading until line does not start with meta data prefix
            while (line.startsWith(METADATA_PREFIX)) {
                line = buff.readLine();
            }
            // extract the maf file header
            columnNames = splitDataFields(line);
            reader.close();
        }

        return columnNames;
    }

    private String[] splitDataFields(String line) {
        line = line.replaceAll("^" + METADATA_PREFIX + "+", "");
        String[] fields = line.split(DELIMITER, -1);
        return fields;
    }
}
