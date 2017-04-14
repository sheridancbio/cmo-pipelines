/*
 * Copyright (c) 2016-2017 Memorial Sloan-Kettering Cancer Center.
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
package org.mskcc.cmo.ks.darwin.pipeline.mskimpactgeniesample;

import org.mskcc.cmo.ks.darwin.pipeline.model.*;
import org.springframework.batch.item.*;
import com.querydsl.core.types.Projections;
import com.querydsl.sql.SQLQueryFactory;
import static com.querydsl.core.alias.Alias.$;
import static com.querydsl.core.alias.Alias.alias;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.annotation.Autowired;
import org.apache.log4j.Logger;
import java.util.*;
import static com.querydsl.core.alias.Alias.$;
import static com.querydsl.core.alias.Alias.alias;

/**
 *
 * @author heinsz
 */
public class MskimpactGenieSampleReader implements ItemStreamReader<MskimpactGenieClinical> {
    @Value("${darwin.pathology_dmp_view}")
    private String patientPathologyDmpView;
    
    @Value("${darwin.pathology_view}")
    private String patientPathologyView;
    
    @Autowired
    SQLQueryFactory darwinQueryFactory;
    
    private List<MskimpactGenieClinical> darwinGenieResults;
    
    Logger log = Logger.getLogger(MskimpactGenieSampleReader.class);
    
    @Override
    public void open(ExecutionContext ec) throws ItemStreamException {
        this.darwinGenieResults = getDarwinDemographicsResults();
    }

    @Override
    public void update(ExecutionContext ec) throws ItemStreamException {}

    @Override
    public void close() throws ItemStreamException {}

    @Override
    public MskimpactGenieClinical read() throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException {
        if (!darwinGenieResults.isEmpty()) {
            return darwinGenieResults.remove(0);
        }
        return null;
    }
    
    private List<MskimpactGenieClinical> getDarwinDemographicsResults() {
        log.info("Start of Darwin Genie View Import...");
        MskimpactPathologyDmp qMskimpactPathologyDmp = alias(MskimpactPathologyDmp.class, patientPathologyDmpView);
        MskimpactPathology qMskimpactPathology = alias(MskimpactPathology.class, patientPathologyView);
        List<MskimpactGenieClinical> darwinGenieResults = darwinQueryFactory.selectDistinct(Projections.constructor(MskimpactGenieClinical.class,
                $(qMskimpactPathologyDmp.getDMP_ID_PATH_DMP()),
                $(qMskimpactPathologyDmp.getSAMPLE_ID_PATH_DMP()),
                $(qMskimpactPathology.getAGE_AT_PATHOLOGY_REPORT_DATE_IN_DAYS())))
                .from($(qMskimpactPathologyDmp))
                .innerJoin($(qMskimpactPathology))
                .on($(qMskimpactPathology.getRPT_ID_PATHOLOGY()).eq($(qMskimpactPathologyDmp.getRPT_ID_PATH_DMP())))
                .fetch();
       return darwinGenieResults;
    }    
}
