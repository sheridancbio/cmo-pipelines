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
package org.mskcc.cmo.ks.darwin.pipeline.age;

import org.mskcc.cmo.ks.darwin.pipeline.model.*;
import com.querydsl.core.types.Projections;
import com.querydsl.sql.SQLQueryFactory;
import org.springframework.batch.item.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.apache.log4j.Logger;
import java.util.*;
import javax.annotation.Resource;
import static com.querydsl.core.alias.Alias.$;
import static com.querydsl.core.alias.Alias.alias;
/**
 *
 * @author jake
 */
public class MskimpactAgeReader implements ItemStreamReader<MskimpactPatientDemographics>{
    @Value("${darwin.demographics_view}")
    private String patientDemographicsView;

    @Value("${darwin.pathology_dmp_view}")
    private String pathologyDmpView;


    @Value("#{jobParameters[studyID]}")
    private String studyID;

    @Autowired
    SQLQueryFactory darwinQueryFactory;

    @Resource(name="studyIdRegexMap")
    Map<String, String> studyIdRegexMap;

    private List<MskimpactPatientDemographics> darwinDemographicsResults;

    Logger log = Logger.getLogger(MskimpactAgeReader.class);

    @Override
    public void open(ExecutionContext executionContext) throws ItemStreamException{
        this.darwinDemographicsResults = getDarwinDemographicsResults();
    }

    @Transactional
    private List<MskimpactPatientDemographics> getDarwinDemographicsResults(){

        log.info("Query darwin tables for age data...");
        MskimpactPatientDemographics qMskImpactPatientDemographics = alias(MskimpactPatientDemographics.class, patientDemographicsView);
        MskimpactPathologyDmp qMskimpactPathologyDmp = alias(MskimpactPathologyDmp.class, pathologyDmpView);
        List<MskimpactPatientDemographics> darwinDemographicsResults = darwinQueryFactory.selectDistinct(Projections.constructor(MskimpactPatientDemographics.class,
                $(qMskImpactPatientDemographics.getDMP_ID_DEMO()),
                $(qMskImpactPatientDemographics.getPT_BIRTH_YEAR()),
                $(qMskImpactPatientDemographics.getPT_DEATH_YEAR())))
                .from($(qMskImpactPatientDemographics))
                .innerJoin($(qMskimpactPathologyDmp))
                .on($(qMskImpactPatientDemographics.getPT_ID_DEMO()).eq($(qMskimpactPathologyDmp.getPT_ID_PATH_DMP())))
                .where($(qMskimpactPathologyDmp.getSAMPLE_ID_PATH_DMP()).like(studyIdRegexMap.get(studyID)))
                .fetch();
        
        return darwinDemographicsResults;
    }

    @Override
    public void update(ExecutionContext executionContext) throws ItemStreamException{}

    @Override
    public void close() throws ItemStreamException{}

    @Override
    public MskimpactPatientDemographics read() throws Exception{
        return getRecord();
    }

    // Recursively remove items from darwinDemographicsResults looking for record to return.
    // Patients can have multiple entries in the icdo view - we only want to return the one with the oldest year
    // The records are ordered by TM_DX_YEAR
    private MskimpactPatientDemographics getRecord() {
        if (!darwinDemographicsResults.isEmpty()) {
            return darwinDemographicsResults.remove(0);
        }
        return null;
    }
}
