/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cbioportal.cmo.pipelines.darwin;

import org.cbioportal.cmo.pipelines.darwin.model.MSK_ImpactPatientDemographics;
import org.cbioportal.cmo.pipelines.darwin.model.MSK_ImpactPatientIcdoRecord;

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
public class MSK_ImpactPatientDemographicsReader implements ItemStreamReader<MSK_ImpactPatientDemographics>{
    @Value("${darwin.demographics_view}")
    private String patientDemographicsView;
    
    @Value("${darwin.icdo_view}")
    private String patientIcdoView;
    
    @Autowired
    SQLQueryFactory darwinQueryFactory;
    
    private List<MSK_ImpactPatientDemographics> darwinDemographicsResults;
    private List<String> darwinDemographicsIDs = new ArrayList<>();
    @Override
    public void open(ExecutionContext executionContext) throws ItemStreamException{
        this.darwinDemographicsResults = getDarwinDemographicsResults();
    }
            
    @Transactional
    private List<MSK_ImpactPatientDemographics> getDarwinDemographicsResults(){
        System.out.println("Start of Darwin Patient Demographics View Import...");
        MSK_ImpactPatientDemographics qDPD = alias(MSK_ImpactPatientDemographics.class, patientDemographicsView);
        MSK_ImpactPatientIcdoRecord qDPIR = alias(MSK_ImpactPatientIcdoRecord.class, patientIcdoView);
        List<MSK_ImpactPatientDemographics> darwinDemographicsResults = darwinQueryFactory.select(Projections.constructor(MSK_ImpactPatientDemographics.class,
                            $(qDPD.getPT_ID_DEMO()), 
                            $(qDPD.getDMP_ID_DEMO()), 
                            $(qDPD.getGENDER()),
                            $(qDPD.getRACE()), 
                            $(qDPD.getRELIGION()), 
                            $(qDPD.getAGE_AT_DATE_OF_DEATH_IN_DAYS()),
                            $(qDPD.getDEATH_SOURCE_DESCRIPTION()), 
                            $(qDPD.getVITAL_STATUS()), 
                            $(qDPD.getPT_COUNTRY()), 
                            $(qDPD.getPT_STATE()),
                            $(qDPD.getPT_ZIP3_CD()), 
                            $(qDPD.getPT_BIRTH_YEAR()), 
                            $(qDPD.getPT_SEX_DESC()),
                            $(qDPD.getPT_VITAL_STATUS()), 
                            $(qDPD.getPT_MARITAL_STS_DESC()), 
                            $(qDPD.getPT_DEATH_YEAR()), 
                            $(qDPD.getPT_MRN_CREATE_YEAR()), 
                            $(qDPIR.getTUMOR_YEAR())))
                    .from($(qDPD))
                    .join($(qDPIR))
                    .on($(qDPD.getDMP_ID_DEMO()).eq($(qDPIR.getDMP_ID_ICDO())))
                    .orderBy($(qDPD.getDMP_ID_DEMO()).asc())
                    .orderBy($(qDPIR.getTUMOR_YEAR()).asc())
                    .fetch();
            
        System.out.println("Imported " + darwinDemographicsResults.size() + " records from Darwin Patient Demographics View.");
        return darwinDemographicsResults;
    }
    
    @Override
    public void update(ExecutionContext executionContext) throws ItemStreamException{}
    
    @Override
    public void close() throws ItemStreamException{}
    
    @Override
    public MSK_ImpactPatientDemographics read() throws Exception{
        if(!darwinDemographicsResults.isEmpty()){
            if (!darwinDemographicsIDs.isEmpty()) {
                while (darwinDemographicsIDs.contains(darwinDemographicsResults.get(0).getDMP_ID_DEMO())&&!darwinDemographicsResults.isEmpty()) {
                    if (!darwinDemographicsResults.isEmpty()) {
                        darwinDemographicsResults.remove(0);
                    } else {
                        return null;
                    }
                }
            }
            darwinDemographicsIDs.add(darwinDemographicsResults.get(0).getDMP_ID_DEMO());
            return darwinDemographicsResults.remove(0);
        }      
        return null;
    }
}
