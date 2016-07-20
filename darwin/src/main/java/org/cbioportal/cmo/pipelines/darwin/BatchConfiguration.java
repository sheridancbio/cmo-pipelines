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

package org.cbioportal.cmo.pipelines.darwin;

import org.cbioportal.cmo.pipelines.darwin.model.*;

import java.util.*;
import org.springframework.batch.core.*;
import org.springframework.batch.item.*;
import org.springframework.batch.core.configuration.annotation.*;
import org.springframework.context.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.batch.item.support.CompositeItemProcessor;
import org.springframework.batch.item.support.CompositeItemWriter;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
/**
 *
 * @author jake
 */
@Configuration
@EnableBatchProcessing
public class BatchConfiguration {
    
    public static final String MSK_IMPACT_JOB = "msk_ImpactJob";
    public static final String STUDYID_JOB = "studyIDJob";
      
    private final List<ItemProcessor> processorDelegates = new ArrayList<>();
    private final List<ItemStreamWriter> writerDelegates = new ArrayList<>();
    
    @Value("${darwin.chunk_size}")
    private Integer chunkSize;
    
    @Autowired
    public JobBuilderFactory jobBuilderFactory;
    
    @Autowired
    public StepBuilderFactory stepBuilderFactory;
    /*
    MSK_ImpactTimelineBrainSpineStatusProcessor statusProcessor = new MSK_ImpactTimelineBrainSpineStatusProcessor();
    MSK_ImpactTimelineBrainSpineSpecimenProcessor specimenProcessor = new MSK_ImpactTimelineBrainSpineSpecimenProcessor();
    MSK_ImpactTimelineBrainSpineTreatmentProcessor treatmentProcessor = new MSK_ImpactTimelineBrainSpineTreatmentProcessor();
    MSK_ImpactTimelineBrainSpineImagingProcessor imagingProcessor = new MSK_ImpactTimelineBrainSpineImagingProcessor();
    MSK_ImpactTimelineBrainSpineSurgeryProcessor surgeryProcessor = new MSK_ImpactTimelineBrainSpineSurgeryProcessor();
    
    MSK_ImpactTimelineBrainSpineStatusWriter statusWriter = new MSK_ImpactTimelineBrainSpineStatusWriter();
    MSK_ImpactTimelineBrainSpineSpecimenWriter specimenWriter = new MSK_ImpactTimelineBrainSpineSpecimenWriter();
    MSK_ImpactTimelineBrainSpineTreatmentWriter treatmentWriter = new MSK_ImpactTimelineBrainSpineTreatmentWriter();
    MSK_ImpactTimelineBrainSpineImagingWriter imagingWriter = new MSK_ImpactTimelineBrainSpineImagingWriter();
    MSK_ImpactTimelineBrainSpineSurgeryWriter surgeryWriter = new MSK_ImpactTimelineBrainSpineSurgeryWriter();
    */
    @Bean
    public static PropertySourcesPlaceholderConfigurer propertyConfigInDev() {
        return new PropertySourcesPlaceholderConfigurer();
    }
    
    
    @Bean
    public Job msk_ImpactJob(){
        return jobBuilderFactory.get(MSK_IMPACT_JOB)
                .start(stepDarwinPatientDemographics())
                .next(stepDarwinTimelineBrainSpine())
                //.next(stepDPIR())
                .next(stepDarwinClinicalBrainSpine())
                .build();
    }
    
    @Bean
    public Step stepDarwinPatientDemographics(){
        return stepBuilderFactory.get("stepDPD")
                .<MSK_ImpactPatientDemographics, String> chunk(chunkSize)
                .reader(readerDarwinPatientDemographics())
                .processor(processorDarwinPatientDemographics())
                .writer(writerDarwinPatientDemographics())
                .build();
    }
    
    @Bean
    @StepScope
    public ItemStreamReader<MSK_ImpactPatientDemographics> readerDarwinPatientDemographics(){
        return new MSK_ImpactPatientDemographicsReader();
    }
    
    
    
    @Bean
    public MSK_ImpactPatientDemographicsProcessor processorDarwinPatientDemographics()
    {
        return new MSK_ImpactPatientDemographicsProcessor();
    }
    
    @Bean
    @StepScope
    public ItemStreamWriter<String> writerDarwinPatientDemographics()
    {
        return new MSK_ImpactPatientDemographicsWriter();
    }
    
    @Bean
    public Step stepDarwinTimelineBrainSpine(){
        return stepBuilderFactory.get("stepDTBS")
                .<MSK_ImpactTimelineBrainSpine, TimelineBrainSpineComposite> chunk(chunkSize)
                .reader(readerDarwinTimelineBrainSpine())
                .processor(processorDarwinTimelineBrainSpine())
                .writer(writerDarwinTimelineBrainSpine())
                .build();
    }
    
    @Bean
    @StepScope
    public ItemStreamReader<MSK_ImpactTimelineBrainSpine> readerDarwinTimelineBrainSpine(){
        return new MSK_ImpactTimelineBrainSpineReader();
    }
    
    @Bean
    @StepScope
    public CompositeItemProcessor processorDarwinTimelineBrainSpine(){
        processorDelegates.add(new MSK_ImpactTimelineBrainSpineStatusProcessor());
        processorDelegates.add(new MSK_ImpactTimelineBrainSpineSpecimenProcessor());
        processorDelegates.add(new MSK_ImpactTimelineBrainSpineTreatmentProcessor());
        processorDelegates.add(new MSK_ImpactTimelineBrainSpineImagingProcessor());
        processorDelegates.add(new MSK_ImpactTimelineBrainSpineSurgeryProcessor());
        CompositeItemProcessor processor = new CompositeItemProcessor<>();
        processor.setDelegates(processorDelegates);
        return processor;
    }
    
    @Bean
    @StepScope
    public CompositeItemWriter<TimelineBrainSpineComposite> writerDarwinTimelineBrainSpine(){
        writerDelegates.add(new MSK_ImpactTimelineBrainSpineStatusWriter());
        writerDelegates.add(new MSK_ImpactTimelineBrainSpineSpecimenWriter());
        writerDelegates.add(new MSK_ImpactTimelineBrainSpineTreatmentWriter());
        writerDelegates.add(new MSK_ImpactTimelineBrainSpineImagingWriter());
        writerDelegates.add(new MSK_ImpactTimelineBrainSpineSurgeryWriter());
        CompositeItemWriter writer = new CompositeItemWriter<>();
        writer.setDelegates(writerDelegates);
        return writer;
    }
    
    @Bean
    public Step stepDPIR(){
        return stepBuilderFactory.get("stepDPIR")
                .<MSK_ImpactPatientIcdoRecord, String> chunk(chunkSize)
                .reader(readerDarwinPatientICDORecord())
                .processor(processorDarwinPatientICDORecrod())
                .writer(writerDarwinPatientICDORecord())
                .build();
    }
    
    @Bean
    @StepScope
    public ItemStreamReader<MSK_ImpactPatientIcdoRecord> readerDarwinPatientICDORecord(){
        return new MSK_ImpactPatientIcdoReader();
    }
    
    @Bean
    public MSK_ImpactPatientIcdoProcessor processorDarwinPatientICDORecrod(){
        return new MSK_ImpactPatientIcdoProcessor();
    }
    
    @Bean
    @StepScope
    public ItemStreamWriter<String> writerDarwinPatientICDORecord(){
        return new MSK_ImpactPatientIcdoWriter();
    }
    
    @Bean
    public Step stepDarwinClinicalBrainSpine(){
        return stepBuilderFactory.get("stepDCBS")
                .<MSK_ImpactClinicalBrainSpine, String> chunk(chunkSize)
                .reader(readerDarwinClinicalBrainSpine())
                .processor(processorDarwinClinicalBrainSpine())
                .writer(writerDarwinClinicalBrainSpine())
                .build();
    }
    
    @Bean
    @StepScope
    public ItemStreamReader<MSK_ImpactClinicalBrainSpine> readerDarwinClinicalBrainSpine(){
        return new MSK_ImpactClinicalBrainSpineReader();
    }
    
    @Bean
    public MSK_ImpactClinicalBrainSpineProcessor processorDarwinClinicalBrainSpine(){
        return new MSK_ImpactClinicalBrainSpineProcessor();
    }
    
    @Bean
    @StepScope
    public MSK_ImpactClinicalBrainSpineWriter writerDarwinClinicalBrainSpine(){
        return new MSK_ImpactClinicalBrainSpineWriter();
    }
    
}
