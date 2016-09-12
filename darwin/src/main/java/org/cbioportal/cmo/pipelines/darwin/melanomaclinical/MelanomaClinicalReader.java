/*
 * Copyright (c) 2016 Memorial Sloan-Kettering Cancer Center.
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
package org.cbioportal.cmo.pipelines.darwin.melanomaclinical;

import org.cbioportal.cmo.pipelines.darwin.demographics.MSKImpactPatientDemographicsReader;
import org.cbioportal.cmo.pipelines.darwin.model.MelanomaClinicalRecord;
import java.util.*;
import org.springframework.batch.item.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.apache.log4j.Logger;
import com.querydsl.core.types.Projections;
import static com.querydsl.core.alias.Alias.$;
import static com.querydsl.core.alias.Alias.alias;
import com.querydsl.sql.SQLQueryFactory;
/**
 *
 * @author heinsz
 */
public class MelanomaClinicalReader implements ItemStreamReader<MelanomaClinicalRecord>{
    
    @Value("${darwin.melanoma.staging_path_current_view}")
    private String stagingPathCurrentView;
    
    @Value("${darwin.melanoma.primary_view}")
    private String primaryView;

    @Value("${darwin.melanoma.general_view}")
    private String generalView;     
    
    @Value("${darwin.melanoma.impact_view}")
    private String impactView;         
    
    @Autowired
    SQLQueryFactory darwinQueryFactory;    
    
    Logger log = Logger.getLogger(MSKImpactPatientDemographicsReader.class);
    
    List<MelanomaClinicalRecord> melanomaClinicalRecords;
    
    @Override
    public void open(ExecutionContext executionContext) throws ItemStreamException {
        this.melanomaClinicalRecords = getMelanomaClinicalRecords();
    }
    
    @Override
    public void update(ExecutionContext executionContext) throws ItemStreamException{}
    
    @Override
    public void close() throws ItemStreamException{}
    
    @Override
    public MelanomaClinicalRecord read() throws Exception{
        if(!melanomaClinicalRecords.isEmpty()) {
            return melanomaClinicalRecords.remove(0);
        }
        return null;
    }
    
    @Transactional
    private List<MelanomaClinicalRecord> getMelanomaClinicalRecords() {
        log.info("Start of Darwin melanoma clinical records query..");
        
        MelanomaClinicalRecord qStagingPathCurrentView = alias(MelanomaClinicalRecord.class, stagingPathCurrentView);
        MelanomaClinicalRecord qPrimaryView = alias(MelanomaClinicalRecord.class, primaryView);      
        MelanomaClinicalRecord qGeneralView = alias(MelanomaClinicalRecord.class, generalView);
        MelanomaClinicalRecord qImpactView = alias(MelanomaClinicalRecord.class, impactView);
        
        List<MelanomaClinicalRecord> darwinMelanomaRecords = darwinQueryFactory.selectDistinct(Projections.constructor(MelanomaClinicalRecord.class,
                $(qStagingPathCurrentView.getMELSPC_PTID()),
                $(qStagingPathCurrentView.getMELSPC_STAGE_YEAR()),
                $(qStagingPathCurrentView.getMELSPC_STG_GRP_NAME()),
                $(qGeneralView.getMELG_PTID()),
                $(qGeneralView.getMELG_STS_DESC()),
                $(qGeneralView.getMELG_STS_SRCE_DESC()),
                $(qGeneralView.getMELG_ACTV_STS_DESC()),
                $(qGeneralView.getMELG_DERMAGRPHX_DESC()),
                $(qGeneralView.getMELG_PRES_STG_YEAR()),
                $(qGeneralView.getMELG_FAMILY_HX_DESC()),
                $(qGeneralView.getMELG_1ST_RECUR_YEAR()),
                $(qGeneralView.getMELG_LOCAL_DESC()),
                $(qGeneralView.getMELG_NODAL_DESC()),
                $(qGeneralView.getMELG_INTRANSIT_DESC()),
                $(qGeneralView.getMELG_SYS_DESC()),
                $(qGeneralView.getMELG_RECUR_NDSZ_DESC()),
                $(qGeneralView.getMELG_RECUR_NODAL_NO()),
                $(qGeneralView.getMELG_LDH()),
                $(qGeneralView.getMELG_LDH_YEAR()),
                $(qGeneralView.getMELG_METS_DESC()),
                $(qGeneralView.getMELG_ADJVNT_TX_DESC()),
                $(qGeneralView.getMELG_SYS_TX_DESC()),
                $(qGeneralView.getMELG_RAD_TX_DESC()),
                $(qGeneralView.getMELG_SURG_DESC()),
                $(qGeneralView.getMELG_TISSUE_BANK_AVAIL()),
                $(qPrimaryView.getMELP_PTID()),
                $(qPrimaryView.getMELP_PRIM_SEQ()),
                $(qPrimaryView.getMELP_DX_YEAR()),
                $(qPrimaryView.getMELP_MSK_REVIEW_DESC()),
                $(qPrimaryView.getMELP_THICKNESS_MM()),
                $(qPrimaryView.getMELP_CLARK_LVL_DESC()),
                $(qPrimaryView.getMELP_ULCERATION_DESC()),
                $(qPrimaryView.getMELP_SITE_DESC()),
                $(qPrimaryView.getMELP_SUB_SITE_DESC()),
                $(qPrimaryView.getMELP_TILS_DESC()),
                $(qPrimaryView.getMELP_REGRESSION_DESC()),
                $(qPrimaryView.getMELP_MARGINS_DESC()),
                $(qPrimaryView.getMELP_MITIDX_UNK_DESC()),
                $(qPrimaryView.getMELP_HIST_TYPE_DESC()),
                $(qPrimaryView.getMELP_SATELLITES_DESC()),
                $(qPrimaryView.getMELP_EXT_SLIDES_DESC()),
                $(qPrimaryView.getMELP_LNORG_DX_DESC()),
                $(qPrimaryView.getMELP_LNCLIN_STS_DESC()),
                $(qPrimaryView.getMELP_LNSENTINBX_DESC()),
                $(qPrimaryView.getMELP_LNSENTINBX_YEAR()),
                $(qPrimaryView.getMELP_LNPROLYSCT_DESC()),
                $(qPrimaryView.getMELP_LNPROSUCC_DESC()),
                $(qPrimaryView.getMELP_LNDSCT_CMP_DESC()),
                $(qPrimaryView.getMELP_LNDSCT_YEAR()),
                $(qPrimaryView.getMELP_LNMATTED_DESC()),
                $(qPrimaryView.getMELP_LNEXTNODST_DESC()),
                $(qPrimaryView.getMELP_LNINTRMETS_DESC()),
                $(qPrimaryView.getMELP_LNSIZE()),
                $(qPrimaryView.getMELP_LNSIZE_UNK_DESC()),
                $(qPrimaryView.getMELP_LNSLNLARG_SIZE()),
                $(qPrimaryView.getMELP_LNIHC_DESC()),
                $(qPrimaryView.getMELP_LNIMM_S100_DESC()),
                $(qPrimaryView.getMELP_LNIMMHMB45_DESC()),
                $(qPrimaryView.getMELP_LNIMM_MELA_DESC()),
                $(qImpactView.getMELI_PTID()),
                $(qImpactView.getMELI_DMP_PATIENT_ID()),
                $(qImpactView.getMELI_DMP_SAMPLE_ID()),
                $(qImpactView.getMELI_REPORT_YEAR()),
                $(qImpactView.getMELI_PROCEDURE_YEAR()),
                $(qImpactView.getMELI_TUMOR_TYPE()),
                $(qImpactView.getMELI_PRIMARY_SITE()),
                $(qImpactView.getMELI_MET_SITE())))
                .from($(qStagingPathCurrentView))
                .fullJoin($(qPrimaryView))
                .on($(qStagingPathCurrentView.getMELSPC_PTID()).eq($(qPrimaryView.getMELP_PTID())))
                .fullJoin($(qGeneralView))
                .on($(qStagingPathCurrentView.getMELSPC_PTID()).eq($(qGeneralView.getMELG_PTID())).or($(qPrimaryView.getMELP_PTID()).eq($(qGeneralView.getMELG_PTID()))))
                .fullJoin($(qImpactView))
                .on($(qStagingPathCurrentView.getMELSPC_PTID()).eq($(qImpactView.getMELI_PTID())).or($(qPrimaryView.getMELP_PTID()).eq($(qImpactView.getMELI_PTID())).or($(qGeneralView.getMELG_PTID()).eq($(qImpactView.getMELI_PTID())))))
                .fetch();
        return darwinMelanomaRecords;
    }
}
