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
package org.cbioportal.cmo.pipelines.darwin.melanomatimeline;

import org.cbioportal.cmo.pipelines.darwin.demographics.MSKImpactPatientDemographicsReader;
import org.cbioportal.cmo.pipelines.darwin.model.*;
import java.util.*;
import org.springframework.batch.item.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.batch.item.ItemStreamReader;
import com.querydsl.core.types.Projections;
import static com.querydsl.core.alias.Alias.$;
import static com.querydsl.core.alias.Alias.alias;
import com.querydsl.sql.SQLQueryFactory;
import org.apache.log4j.Logger;

/**
 *
 * @author heinsz
 */
public class MelanomaTimelineReader implements ItemStreamReader<MelanomaTimelineRecord> {
    
    @Value("${darwin.melanoma.adjuvant_tx_view}")
    private String adjuvantTxView;
    @Value("${darwin.melanoma.rad_therapy_view}")
    private String radTherapyView;
    @Value("${darwin.melanoma.systemic_tx_view}")
    private String systemicTxView;
    @Autowired
    SQLQueryFactory darwinQueryFactory;    
    
    List<MelanomaTimelineRecord> melanomaTimelineRecords;
    Logger log = Logger.getLogger(MSKImpactPatientDemographicsReader.class);
    
    @Override
    public void open(ExecutionContext executionContext) throws ItemStreamException {
        this.melanomaTimelineRecords = getMelanomaTimelineRecords();
    }
    
    @Override
    public void update(ExecutionContext executionContext) throws ItemStreamException {}
    
    @Override
    public void close() throws ItemStreamException {}
    
    @Override
    public MelanomaTimelineRecord read() throws Exception {
        if (!melanomaTimelineRecords.isEmpty()) {
            return melanomaTimelineRecords.remove(0);
        }
        return null;
    }
    
    @Transactional
    private List<MelanomaTimelineRecord> getMelanomaTimelineRecords() {
        log.info("Start of Darwin melanoma timeline records query..");
        List<MelanomaTimelineRecord> darwinRecords = new ArrayList<>();
        MelanomaTimelineAdjuvantTx qAdjuvantTxView = alias(MelanomaTimelineAdjuvantTx.class, adjuvantTxView);
        MelanomaTimelineRadTherapy qRadTherapyView = alias(MelanomaTimelineRadTherapy.class, radTherapyView);
        MelanomaTimelineSystemicTx qSystemicTxView = alias(MelanomaTimelineSystemicTx.class, systemicTxView);
        
        List<MelanomaTimelineAdjuvantTx> adjuvantTimelineRecords = darwinQueryFactory.selectDistinct(Projections.constructor(MelanomaTimelineAdjuvantTx.class,
                $(qAdjuvantTxView.getMELAT_PTID()),
                $(qAdjuvantTxView.getMELAT_ADJTX_TYP_CD()),
                $(qAdjuvantTxView.getMELAT_ADJTX_TYP_DESC()),
                $(qAdjuvantTxView.getMELAT_ADJTX_TYPE_OTH()),
                $(qAdjuvantTxView.getMELAT_ADJTX_STRT_YEAR()),
                $(qAdjuvantTxView.getMELAT_ADJTX_END_YEAR()),
                $(qAdjuvantTxView.getMELAT_ADJTX_DAYS_DURATION())))
                .from($(qAdjuvantTxView))
                .fetch();
        List<MelanomaTimelineRadTherapy> radTimelineRecords = darwinQueryFactory.selectDistinct(Projections.constructor(MelanomaTimelineRadTherapy.class, 
                $(qRadTherapyView.getMELRT_PTID()),
                $(qRadTherapyView.getMELRT_RTTX_TYPE_DESC()),
                $(qRadTherapyView.getMELRT_RTTX_ADJ_DESC()),
                $(qRadTherapyView.getMELRT_RTTX_STRT_YEAR()),
                $(qRadTherapyView.getMELRT_RTTX_END_DT()),
                $(qRadTherapyView.getMELAT_RTTX_DAYS_DURATION())))
                .from($(qRadTherapyView))
                .fetch();
        List<MelanomaTimelineSystemicTx> systemicTimelineRecords = darwinQueryFactory.selectDistinct(Projections.constructor(MelanomaTimelineSystemicTx.class, 
                $(qSystemicTxView.getMELST_PTID()),
                $(qSystemicTxView.getMELST_SYSTX_TYP_DESC()),
                $(qSystemicTxView.getMELST_SYSTX_TYPE_OTH()),
                $(qSystemicTxView.getMELST_SYSTX_STRT_YEAR()),
                $(qSystemicTxView.getMELST_SYSTX_END_YEAR()),
                $(qSystemicTxView.getMELST_SYSTX_DAYS_DURATION())))
                .from($(qSystemicTxView))
                .fetch();       
        
        darwinRecords.addAll(adjuvantTimelineRecords);
        darwinRecords.addAll(radTimelineRecords);
        darwinRecords.addAll(systemicTimelineRecords);
        
        return darwinRecords;
    }
}
