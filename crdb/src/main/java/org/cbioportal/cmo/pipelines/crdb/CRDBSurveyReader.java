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
import com.querydsl.sql.H2Templates;
import com.querydsl.sql.OracleTemplates;
import static com.querydsl.sql.SQLExpressions.select;
import com.querydsl.sql.SQLQuery;
import com.querydsl.sql.SQLQueryFactory;
import com.querydsl.sql.SQLTemplates;
import org.cbioportal.cmo.pipelines.crdb.model.*;
import org.cbioportal.cmo.pipelines.crdb.OracleConfiguration;

import org.springframework.batch.item.*;
import org.springframework.core.io.FileSystemResource;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;
import java.lang.annotation.Annotation;
import oracle.jdbc.pool.OracleDataSource;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jdbc.query.QueryDslJdbcTemplate;
import org.springframework.transaction.annotation.Transactional;


/**
 * @author Benjamin Gross
 */
public class CRDBSurveyReader implements ItemStreamReader<CRDBSurvey>
{
    @Value("${crdb.survey_table}")
    private String crdbSurveyTable;
    
    @Value("#{jobParameters[cancerStudy]}")
    private String cancerStudy;

    private List<CRDBSurvey> crdbSurvey;
    
    @Autowired
    public BeanFactory beanFactory;
    
    
    @Override
    public void open(ExecutionContext executionContext) throws ItemStreamException
    {
        //read from CRDB view 
        OracleDataSource crdbDataSource = beanFactory.getBean(OracleConfiguration.CRDB_DATA_SOURCE, OracleDataSource.class);
        this.crdbSurvey = getCrdbSurveyResults(crdbDataSource);
        
//        this.cancerStudy = "mskimpact";
//        Date qs_date_placeholder = new Date(04/13/2016);
//        crdbSurvey.add(new CRDBSurvey("dmp_id_placeholder",qs_date_placeholder,"adj_txt_placeholder",
//                "no_sys_txt_placeholder", "prior_rx_placeholder", "brain_met_placeholder", 
//                "ecog_placeholder", "comments_placeholder"));
    }
    
    @Transactional
    private List<CRDBSurvey> getCrdbSurveyResults(OracleDataSource crdbDataSource) {
        QueryDslJdbcTemplate template = new QueryDslJdbcTemplate(crdbDataSource);
        CRDBSurvey qCRDBS = alias(CRDBSurvey.class, crdbSurveyTable);
        
        List<CRDBSurvey> crdbSurveyResults = new ArrayList<>();
        List<String> dmpIdList = new ArrayList<>();
        for (String dmpId : select($(qCRDBS.getDmpId())).from($(qCRDBS)).fetch()) {
            if (dmpId != null) {
                dmpIdList.add(dmpId);
                CRDBSurvey record = new CRDBSurvey();
                record.setDmpId(dmpId);
                record.setQsDate((Date)select($(qCRDBS.getQsDate())).from($(qCRDBS)).where($(qCRDBS.getDmpId()).eq(dmpId)).fetch());
                record.setAdjTxt(select($(qCRDBS.getAdjTxt())).from($(qCRDBS)).where($(qCRDBS.getDmpId()).eq(dmpId)).fetchOne());
                record.setNoSysTxt(select($(qCRDBS.getNoSysTxt())).from($(qCRDBS)).where($(qCRDBS.getDmpId()).eq(dmpId)).fetchOne());
                record.setPriorRx(select($(qCRDBS.getPriorRx())).from($(qCRDBS)).where($(qCRDBS.getDmpId()).eq(dmpId)).fetchOne());
                record.setBrainMet(select($(qCRDBS.getBrainMet())).from($(qCRDBS)).where($(qCRDBS.getDmpId()).eq(dmpId)).fetchOne());
                record.setEcog(select($(qCRDBS.getEcog())).from($(qCRDBS)).where($(qCRDBS.getDmpId()).eq(dmpId)).fetchOne());
                record.setComments(select($(qCRDBS.getComments())).from($(qCRDBS)).where($(qCRDBS.getDmpId()).eq(dmpId)).fetchOne());
                crdbSurveyResults.add(record);
            }
        }        
        return crdbSurveyResults;
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
