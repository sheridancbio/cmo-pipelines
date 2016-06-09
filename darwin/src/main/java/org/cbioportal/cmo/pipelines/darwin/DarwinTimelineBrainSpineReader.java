/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cbioportal.cmo.pipelines.darwin;

import org.cbioportal.cmo.pipelines.darwin.model.DarwinTimelineBrainSpine;

import static com.querydsl.core.alias.Alias.$;
import static com.querydsl.core.alias.Alias.alias;
import com.querydsl.core.types.Projections;
import com.querydsl.sql.SQLQueryFactory;

import org.springframework.batch.item.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
/**
 *
 * @author jake
 */
public class DarwinTimelineBrainSpineReader implements ItemStreamReader<DarwinTimelineBrainSpine>{
    @Value("${darwin.timeline_view}")
    private String timelineBrainSpineView;
    
    @Autowired
    SQLQueryFactory darwinQueryFactory;
    
    private List<DarwinTimelineBrainSpine> darwinTimelineResults;
    
    @Override
    public void open(ExecutionContext executionContext) throws ItemStreamException{
        this.darwinTimelineResults = getDarwinTimelineResults();
    }
    
    @Transactional
    private List<DarwinTimelineBrainSpine> getDarwinTimelineResults(){
        System.out.println("Start of Darwin Timeline Brain Spine View import...");
        DarwinTimelineBrainSpine qDTR = alias(DarwinTimelineBrainSpine.class, timelineBrainSpineView);
        List<DarwinTimelineBrainSpine> darwinTimelineResults = darwinQueryFactory.select(
                Projections.constructor(DarwinTimelineBrainSpine.class, $(qDTR.getDMT_PATIENT_ID_BRAINSPINETMLN()),
                        $(qDTR.getDMP_PATIENT_ID_MIN_BRAINSPINETMLN()),
                        $(qDTR.getDMP_PATIENT_ID_MAX_BRAINSPINETMLN()), $(qDTR.getDMP_PATIENT_ID_COUNT_BRAINSPINETMLN()),
                        $(qDTR.getDMP_PATIENT_ID_ALL_BRAINSPINETMLN()), $(qDTR.getSTART_DATE()), $(qDTR.getSTOP_DATE()),
                        $(qDTR.getEVENT_TYPE()), $(qDTR.getTREATMENT_TYPE()), $(qDTR.getSUBTYPE()), $(qDTR.getAGENT()),
                        $(qDTR.getSPECIMEN_REFERENCE_NUMBER()), $(qDTR.getSPECIMEN_SITE()), $(qDTR.getSPECIMEN_TYPE()),
                        $(qDTR.getSTATUS()), $(qDTR.getKARNOFSKY_PERFORMANCE_SCORE()), $(qDTR.getSURGERY_DETAILS()),
                        $(qDTR.getEVENT_TYPE_DETAILED()), $(qDTR.getHISTOLOGY()), $(qDTR.getWHO_GRADE()),
                        $(qDTR.getMGMT_STATUS()), $(qDTR.getSOURCE_PATHOLOGY()), $(qDTR.getNOTE()), $(qDTR.getDIAGNOSTIC_TYPE()),
                        $(qDTR.getDIAGNOSTIC_TYPE_DETAILED()), $(qDTR.getSOURCE())))
                .from($(qDTR))
                .where($(qDTR.getDMT_PATIENT_ID_BRAINSPINETMLN().equals("")).isFalse())
                .fetch();

        System.out.println("Imported " + darwinTimelineResults.size() + " records from Darwin Timeline Brain Spine View.");
        return darwinTimelineResults;
    }
    
    @Override
    public void update(ExecutionContext executionContext) throws ItemStreamException{}
    
    @Override
    public void close() throws ItemStreamException{}
    
    @Override
    public DarwinTimelineBrainSpine read() throws Exception{
        if(!darwinTimelineResults.isEmpty()){
            return darwinTimelineResults.remove(0);
        }
        return null;
    }
}
