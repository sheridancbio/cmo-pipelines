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

package org.cbioportal.cmo.pipelines.crdb;

import static com.querydsl.core.alias.Alias.$;
import static com.querydsl.core.alias.Alias.alias;
import com.querydsl.sql.OracleTemplates;
import com.querydsl.sql.SQLQueryFactory;
import com.querydsl.sql.SQLTemplates;

import org.cbioportal.cmo.pipelines.crdb.model.CRDBDataset;

import org.springframework.batch.item.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

import oracle.jdbc.pool.OracleDataSource;


/**
 * @author Benjamin Gross
 */
public class CRDBDatasetReader implements ItemStreamReader<CRDBDataset>
{
    @Value("${crdb.dataset_view}")
    private String crdbDatasetView;

    private List<CRDBDataset> crdbDataset;

    @Value("${crdb.username}")
    private String username;
    
    @Value("${crdb.password}")
    private String password;
    
    @Value("${crdb.connection_string}")
    private String connection_string;
    
    @Override
    public void open(ExecutionContext executionContext) throws ItemStreamException
    {
        //read from CRDB view 
        OracleDataSource crdbDataSource = null;
        try {
            crdbDataSource = getCrdbDataSource();
        } catch (SQLException ex) {
            Logger.getLogger(CRDBDatasetReader.class.getName()).log(Level.SEVERE, null, ex);
        }
        this.crdbDataset = getCrdbDatasetResults(crdbDataSource);
                
    }
    
    @Transactional
    private List<CRDBDataset> getCrdbDatasetResults(OracleDataSource crdbDataSource) {
        System.out.println("Beginning CRDB Dataset View import...");
        
        SQLTemplates templates = new OracleTemplates();
        com.querydsl.sql.Configuration config = new com.querydsl.sql.Configuration(templates);
        SQLQueryFactory queryFactory = new SQLQueryFactory(config, crdbDataSource);
        
        CRDBDataset qCRDBD = alias(CRDBDataset.class, crdbDatasetView);
        List<?> crdbSurveyList = (List<?>)(List<?>)queryFactory.select($(qCRDBD.getDMP_ID()), 
                $(qCRDBD.getCONSENT_DATE_DAYS()), $(qCRDBD.getPRIM_DISEASE_12245()), $(qCRDBD.getINITIAL_SX_DAYS()), 
                $(qCRDBD.getINITIAL_DX_DAYS()), $(qCRDBD.getFIRST_METASTASIS_DAYS()), $(qCRDBD.getINIT_DX_STATUS_ID()), 
                $(qCRDBD.getINIT_DX_STATUS()), $(qCRDBD.getINIT_DX_STATUS_DAYS()), $(qCRDBD.getINIT_DX_STAGING_DSCRP()), 
                $(qCRDBD.getINIT_DX_STAGE()), $(qCRDBD.getINIT_DX_STAGE_DSCRP()), $(qCRDBD.getINIT_DX_GRADE()), 
                $(qCRDBD.getINIT_DX_GRADE_DSCRP()), $(qCRDBD.getINIT_DX_T_STAGE()), $(qCRDBD.getINIT_DX_T_STAGE_DSCRP()), 
                $(qCRDBD.getINIT_DX_N_STAGE()), $(qCRDBD.getINIT_DX_N_STAGE_DSCRP()), $(qCRDBD.getINIT_DX_M_STAGE()), 
                $(qCRDBD.getINIT_DX_M_STAGE_DSCRP()), $(qCRDBD.getINIT_DX_HIST()), $(qCRDBD.getINIT_DX_SUB_HIST()), 
                $(qCRDBD.getINIT_DX_SUB_SUB_HIST()), $(qCRDBD.getINIT_DX_SUB_SUB_SUB_HIST()), $(qCRDBD.getINIT_DX_SITE()), 
                $(qCRDBD.getINIT_DX_SUB_SITE()), $(qCRDBD.getINIT_DX_SUB_SUB_SITE()), $(qCRDBD.getENROLL_DX_STATUS_ID()), 
                $(qCRDBD.getENROLL_DX_STATUS()), $(qCRDBD.getENROLL_DX_STATUS_DAYS()), $(qCRDBD.getENROLL_DX_STAGING_DSCRP()), 
                $(qCRDBD.getENROLL_DX_STAGE()), $(qCRDBD.getENROLL_DX_STAGE_DSCRP()), $(qCRDBD.getENROLL_DX_GRADE()), 
                $(qCRDBD.getENROLL_DX_GRADE_DSCRP()), $(qCRDBD.getENROLL_DX_T_STAGE()), $(qCRDBD.getENROLL_DX_T_STAGE_DSCRP()), 
                $(qCRDBD.getENROLL_DX_N_STAGE()), $(qCRDBD.getENROLL_DX_N_STAGE_DSCRP()), $(qCRDBD.getENROLL_DX_M_STAGE()), 
                $(qCRDBD.getENROLL_DX_M_STAGE_DSCRP()), $(qCRDBD.getENROLL_DX_HIST()), $(qCRDBD.getENROLL_DX_SUB_HIST()), 
                $(qCRDBD.getENROLL_DX_SUB_SUB_HIST()), $(qCRDBD.getENROLL_DX_SUB_SUB_SUB_HIST()), $(qCRDBD.getENROLL_DX_SITE()), 
                $(qCRDBD.getENROLL_DX_SUB_SITE()), $(qCRDBD.getENROLL_DX_SUB_SUB_SITE()), $(qCRDBD.getSURVIVAL_STATUS()), 
                $(qCRDBD.getTREATMENT_END_DAYS()), $(qCRDBD.getOFF_STUDY_DAYS()), $(qCRDBD.getCOMMENTS()))
                .from($(qCRDBD)).fetch();
        
        List<CRDBDataset> crdbDatasetResults = new ArrayList<>();
        Integer numRows = 0;
        for (int i=0; i<crdbSurveyList.size(); i++){
            CRDBDataset record = createRecord(crdbSurveyList.get(i).toString());
            crdbDatasetResults.add(record);
            numRows++;
        }      
        
        System.out.println("Imported "+numRows+" records from CRDB Dataset View.");
        return crdbDatasetResults;
    }
    
    private CRDBDataset createRecord(String recordString){
        String[] vals = recordString.replace("[","").replace("]","").split(", ");
        String dmpId = vals[0]; String consentDateDays = fixNull(vals[1]); String primDisease = fixNull(vals[2]); String initialSxDays = fixNull(vals[3]);
        String initialDxDays = fixNull(vals[4]); String firstMetDays = fixNull(vals[5]); String initDxStatusId = fixNull(vals[6]);
        String initDxStatus = fixNull(vals[7]); String initDxStatusDays = fixNull(vals[8]); String initDxStagingDscrp = fixNull(vals[9]);
        String initDxStage = fixNull(vals[10]); String initDxStageDscrp = fixNull(vals[11]); String initDxGrade = fixNull(vals[12]);
        String initDxGradeDscrp = fixNull(vals[13]); String initDxTStage = fixNull(vals[14]); String initDxTStageDscrp = fixNull(vals[15]);
        String initDxNStage = fixNull(vals[16]); String initDxNStageDscrp = fixNull(vals[17]); String initDxMStage = fixNull(vals[18]);
        String initDxMStageDscrp = fixNull(vals[19]); String initDxHist = fixNull(vals[20]); String initDxSubHist = fixNull(vals[21]);
        String initDxSubSubHist = fixNull(vals[22]); String initDxSubSubSubHist = fixNull(vals[23]); String initDxSite = fixNull(vals[24]);
        String initDxSubSite = fixNull(vals[25]); String initDxSubSubSite = fixNull(vals[26]); String enrollDxStatusId = fixNull(vals[27]);
        String enrollDxStatus = fixNull(vals[28]); String enrollDxStatusDays = fixNull(vals[29]); String enrollDxStatusDscrp = fixNull(vals[30]);
        String enrollDxStage = fixNull(vals[31]); String enrollDxStageDscrp = fixNull(vals[32]); String enrollDxGrade = fixNull(vals[33]);
        String enrollDxGradeDscrp = fixNull(vals[34]); String enrollDxTStage = fixNull(vals[35]); String enrollDxTStageDscrp = fixNull(vals[36]);
        String enrollDxNStage = fixNull(vals[37]); String enrollDxNStageDscrp = fixNull(vals[38]); String enrollDxMStage = fixNull(vals[39]);
        String enrollDxMStageDscrp = fixNull(vals[40]); String enrollDxHist = fixNull(vals[41]); String enrollDxSubHist = fixNull(vals[42]);
        String enrollDxSubSubHist = fixNull(vals[43]); String enrollDxSubSubSubHist = fixNull(vals[44]); String enrollDxSite = fixNull(vals[45]);
        String enrollDxSubSite = fixNull(vals[46]); String enrollDxSubSubSite = fixNull(vals[47]); String survivalStatus = fixNull(vals[48]);
        String treatmentEndDays = fixNull(vals[49]); String offStudyDays = fixNull(vals[50]); String comments = fixNull(vals[51]);
        
        return new CRDBDataset(dmpId, consentDateDays, primDisease, initialSxDays, initialDxDays, firstMetDays, 
                initDxStatusId, initDxStatus, initDxStatusDays, initDxStagingDscrp, initDxStage, initDxStageDscrp, 
                initDxGrade, initDxGradeDscrp, initDxTStage, initDxTStageDscrp, initDxNStage, initDxNStageDscrp, 
                initDxMStage, initDxMStageDscrp, initDxHist, initDxSubHist, initDxSubSubHist, initDxSubSubSubHist, 
                initDxSite, initDxSubSite, initDxSubSubSite, enrollDxStatusId, enrollDxStatus, enrollDxStatusDays, 
                enrollDxStatusDscrp, enrollDxStage, enrollDxStageDscrp, enrollDxGrade, enrollDxGradeDscrp, enrollDxTStage, 
                enrollDxTStageDscrp, enrollDxNStage, enrollDxNStageDscrp, enrollDxMStage, enrollDxMStageDscrp, enrollDxHist, 
                enrollDxSubHist, enrollDxSubSubHist, enrollDxSubSubSubHist, enrollDxSite, enrollDxSubSite, enrollDxSubSubSite, 
                survivalStatus, treatmentEndDays, offStudyDays, comments);
    } 

    private OracleDataSource getCrdbDataSource() throws SQLException {
        OracleDataSource crdbDataSource = new OracleDataSource();
        crdbDataSource.setUser(username);
        crdbDataSource.setPassword(password);
        crdbDataSource.setURL(connection_string);
        return crdbDataSource;
    }
    
    private String fixNull(String val){
        if (val == null){
            return "NA";
        }
        return val;
    }

    @Override
    public void update(ExecutionContext executionContext) throws ItemStreamException {}

    @Override
    public void close() throws ItemStreamException {}

    @Override
    public CRDBDataset read() throws Exception
    {
        if (!crdbDataset.isEmpty()) {            
            return crdbDataset.remove(0);            
        }
        return null;
    } 
}
