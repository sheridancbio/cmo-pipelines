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

import com.mysema.query.NonUniqueResultException;
import static com.querydsl.core.alias.Alias.$;
import static com.querydsl.core.alias.Alias.alias;
import com.querydsl.sql.OracleTemplates;
import static com.querydsl.sql.SQLExpressions.select;
import com.querydsl.sql.SQLQueryFactory;
import com.querydsl.sql.SQLTemplates;

import org.cbioportal.cmo.pipelines.crdb.model.CRDBSurvey;

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
public class CRDBSurveyReader implements ItemStreamReader<CRDBSurvey>
{
    @Value("${crdb.survey_view}")
    private String crdbSurveyView;
    
    @Value("#{jobParameters[cancerStudy]}")
    private String cancerStudy;

    private List<CRDBSurvey> crdbSurvey;

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
            Logger.getLogger(CRDBSurveyReader.class.getName()).log(Level.SEVERE, null, ex);
        }
        this.crdbSurvey = getCrdbSurveyResults(crdbDataSource);
                
    }
    
    @Transactional
    private List<CRDBSurvey> getCrdbSurveyResults(OracleDataSource crdbDataSource) {
        SQLTemplates templates = new OracleTemplates();
        com.querydsl.sql.Configuration config = new com.querydsl.sql.Configuration(templates);
        SQLQueryFactory queryFactory = new SQLQueryFactory(config, crdbDataSource);
        
        CRDBSurvey qCRDBS = alias(CRDBSurvey.class, crdbSurveyView);
        List<CRDBSurvey> crdbSurveyResults = new ArrayList<>();
        for (String dmpId : queryFactory.select($(qCRDBS.getDMP_ID())).from($(qCRDBS)).fetch()) {
            
            if (dmpId != null) {
                System.out.println(dmpId);
                CRDBSurvey record = new CRDBSurvey();
                
                //assuming only one record per dmpId right now...
                try { 
                    record.setDMP_ID(dmpId);
                    record.setQS_DATE(queryFactory.selectDistinct($(qCRDBS.getQS_DATE())).from($(qCRDBS)).where($(qCRDBS.getDMP_ID()).eq(dmpId)).fetchOne());
                    record.setADJ_TXT(queryFactory.selectDistinct($(qCRDBS.getADJ_TXT())).from($(qCRDBS)).where($(qCRDBS.getDMP_ID()).eq(dmpId)).fetchOne());
                    record.setNOSYSTXT(queryFactory.selectDistinct($(qCRDBS.getNOSYSTXT())).from($(qCRDBS)).where($(qCRDBS.getDMP_ID()).eq(dmpId)).fetchOne());
                    record.setPRIOR_RX(queryFactory.selectDistinct($(qCRDBS.getPRIOR_RX())).from($(qCRDBS)).where($(qCRDBS.getDMP_ID()).eq(dmpId)).fetchOne());
                    record.setBRAINMET(queryFactory.selectDistinct($(qCRDBS.getBRAINMET())).from($(qCRDBS)).where($(qCRDBS.getDMP_ID()).eq(dmpId)).fetchOne());
                    record.setECOG(queryFactory.selectDistinct($(qCRDBS.getECOG())).from($(qCRDBS)).where($(qCRDBS.getDMP_ID()).eq(dmpId)).fetchOne());
                    record.setCOMMENTS(queryFactory.selectDistinct($(qCRDBS.getCOMMENTS())).from($(qCRDBS)).where($(qCRDBS.getDMP_ID()).eq(dmpId)).fetchOne());
                    crdbSurveyResults.add(record);
                }
                catch (NonUniqueResultException ex) {
                    ex.printStackTrace();                    
                }                
            }                     
        }        
        return crdbSurveyResults;
    }

    private OracleDataSource getCrdbDataSource() throws SQLException {
        OracleDataSource crdbDataSource = new OracleDataSource();
        crdbDataSource.setUser(username);
        crdbDataSource.setPassword(password);
        crdbDataSource.setURL(connection_string);
        return crdbDataSource;
    }

    @Override
    public void update(ExecutionContext executionContext) throws ItemStreamException {}

    @Override
    public void close() throws ItemStreamException {}

    @Override
    public CRDBSurvey read() throws Exception
    {
        if (!crdbSurvey.isEmpty()) {            
            return crdbSurvey.remove(0);            
        }
        return null;
    } 
}
