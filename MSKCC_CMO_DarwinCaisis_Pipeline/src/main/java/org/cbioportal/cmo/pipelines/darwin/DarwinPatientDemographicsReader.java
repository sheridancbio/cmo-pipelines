/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cbioportal.cmo.pipelines.darwin;

import org.cbioportal.cmo.pipelines.darwin.model.DarwinPatientDemographics;

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
public class DarwinPatientDemographicsReader implements ItemStreamReader<DarwinPatientDemographics>{
    @Value("${DVCBIO.DEMOGRAPHICS_V}")
    private String patientDemographicsView;
    
    @Autowired
    SQLQueryFactory darwinQueryFactory;
    
    private List<DarwinPatientDemographics> darwinDemographicsResults;
    
    @Override
    public void open(ExecutionContext executionContext) throws ItemStreamException{
        this.darwinDemographicsResults = getDarwinDemographicsResults();
    }
    
    @Transactional
    private List<DarwinPatientDemographics> getDarwinDemographicsResults(){
        System.out.println("Start of Darwin Patient Demographics View Import...");
        DarwinPatientDemographics qDDR = alias(DarwinPatientDemographics.class, patientDemographicsView);
        List<DarwinPatientDemographics> darwinDemographicsResults = darwinQueryFactory.select(
                Projections.constructor(DarwinPatientDemographics.class, 
                        $(qDDR.getPT_ID_DEMO()), $(qDDR.getDMP_ID_DEMO()), $(qDDR.getGENDER()),
                        $(qDDR.getRACE()), $(qDDR.getRELIGION()), $(qDDR.getAGE_AT_DATE_OF_DEATH_IN_DAYS()),
                        $(qDDR.getDEATH_SOURCE_DESCRIPTION()), $(qDDR.getPT_COUNTRY()), $(qDDR.getPT_STATE()),
                        $(qDDR.getPT_ZIP3_CD()), $(qDDR.getPT_BIRTH_YEAR()), $(qDDR.getPT_SEX_DESC()), 
                        $(qDDR.getPT_VITAL_STATUS()), $(qDDR.getPT_MARITAL_STS_DESC()), $(qDDR.getPT_DEATH_YEAR())))
                .from($(qDDR))
                .fetch();
        
        System.out.println("Imported " + darwinDemographicsResults.size() + " records from Darwin Patient Demographics View.");
        return darwinDemographicsResults;
    }
    
    @Override
    public void update(ExecutionContext executionContext) throws ItemStreamException{}
    
    @Override
    public void close() throws ItemStreamException{}
    
    @Override
    public DarwinPatientDemographics read() throws Exception{
        if(!darwinDemographicsResults.isEmpty()){
            return darwinDemographicsResults.remove(0);
        }
        return null;
    }
}
