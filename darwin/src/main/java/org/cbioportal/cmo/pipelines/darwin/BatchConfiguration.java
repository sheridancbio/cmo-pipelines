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

import org.cbioportal.cmo.pipelines.darwin.brainspineclinical.*;
import org.cbioportal.cmo.pipelines.darwin.brainspinetimeline.*;
import org.cbioportal.cmo.pipelines.darwin.demographics.*;
import org.cbioportal.cmo.pipelines.darwin.melanomaclinical.*;
import org.cbioportal.cmo.pipelines.darwin.melanomatimeline.*;
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
@ComponentScan(basePackages="org.cbioportal.cmo.clinical.data.source")
public class BatchConfiguration {
    
    public static final String MSK_IMPACT_JOB = "msk_ImpactJob";    
    public static final String MELANOMA_JOB = "melanomaJob";
      
    private final List<ItemProcessor> processorDelegates = new ArrayList<>();
    
    
    @Value("${darwin.chunk_size}")
    private Integer chunkSize;
    
    @Autowired
    public JobBuilderFactory jobBuilderFactory;
    
    @Autowired
    public StepBuilderFactory stepBuilderFactory;

    @Bean
    public static PropertySourcesPlaceholderConfigurer propertyConfigInDev() {
        return new PropertySourcesPlaceholderConfigurer();
    }
    
    
    @Bean
    public Job msk_ImpactJob(){
        return jobBuilderFactory.get(MSK_IMPACT_JOB)
                .start(stepDarwinPatientDemographics())
                .next(stepDarwinTimelineBrainSpine())
                .next(stepDarwinClinicalBrainSpine())
                .build();
    }
    
    @Bean
    public Job melanomaJob(){
        return jobBuilderFactory.get(MELANOMA_JOB)
                .start(melanomaClinicalStep())
                .next(melanomaTimelineStep())
                .build();
    }
    
    @Bean
    public Step stepDarwinPatientDemographics(){
        return stepBuilderFactory.get("stepDarwinPatientDemographics")
                .<MSKImpactPatientDemographics, String> chunk(chunkSize)
                .reader(readerDarwinPatientDemographics())
                .processor(processorDarwinPatientDemographics())
                .writer(writerDarwinPatientDemographics())
                .build();
    }
    
    @Bean
    public Step stepDarwinTimelineBrainSpine(){
        return stepBuilderFactory.get("stepDarwinTimelineBrainSpine")
                .<MSKImpactBrainSpineTimeline, MSKImpactBrainSpineCompositeTimeline> chunk(chunkSize)
                .reader(readerDarwinTimelineBrainSpine())
                .processor(processorDarwinTimelineBrainSpine())
                .writer(writerDarwinTimelineBrainSpine())
                .build();
    }   
    
    @Bean
    public Step stepDarwinClinicalBrainSpine(){
        return stepBuilderFactory.get("stepDarwinClinicalBrainSpine")
                .<MSKImpactBrainSpineClinical, String> chunk(chunkSize)
                .reader(readerDarwinClinicalBrainSpine())
                .processor(processorDarwinClinicalBrainSpine())
                .writer(writerDarwinClinicalBrainSpine())
                .build();
    } 
    
    @Bean
    public Step melanomaClinicalStep() {
        return stepBuilderFactory.get("melanomaClinicalStep")
                .<MelanomaClinicalRecord, String> chunk(chunkSize)
                .reader(melanomaClinicalReader())
                .processor(melanomaClinicalProcessor())
                .writer(melanomaClinicalWriter())
                .build();
    }
    
    @Bean
    public Step melanomaTimelineStep() {
        return stepBuilderFactory.get("melanomaTimelineStep")
                .<MelanomaTimelineRecord, String> chunk(chunkSize)
                .reader(melanomaTimelineReader())
                .processor(melanomaTimelineProcessor())
                .writer(melanomaTimelineWriter())
               .build();
    }    
    
    @Bean
    @StepScope
    public ItemStreamReader<MSKImpactPatientDemographics> readerDarwinPatientDemographics(){
        return new MSKImpactPatientDemographicsReader();
    }
    
    
    
    @Bean
    public MSKImpactPatientDemographicsProcessor processorDarwinPatientDemographics()
    {
        return new MSKImpactPatientDemographicsProcessor();
    }
    
    @Bean
    @StepScope
    public ItemStreamWriter<String> writerDarwinPatientDemographics()
    {
        return new MSKImpactPatientDemographicsWriter();
    }
    
    @Bean
    @StepScope
    public ItemStreamReader<MSKImpactBrainSpineTimeline> readerDarwinTimelineBrainSpine(){
        return new MSKImpactTimelineBrainSpineReader();
    }
    
    @Bean
    @StepScope
    public CompositeItemProcessor processorDarwinTimelineBrainSpine(){
        
        processorDelegates.add(new MSKImpactTimelineBrainSpineStatusProcessor());
        processorDelegates.add(new MSKImpactTimelineBrainSpineSpecimenProcessor());
        processorDelegates.add(new MSKImpactTimelineBrainSpineTreatmentProcessor());
        processorDelegates.add(new MSKImpactTimelineBrainSpineImagingProcessor());
        processorDelegates.add(new MSKImpactTimelineBrainSpineSurgeryProcessor());
        CompositeItemProcessor processor = new CompositeItemProcessor<>();
        processor.setDelegates(processorDelegates);
        return processor;
    }
    
    @Bean
    @StepScope
    public ItemStreamWriter<MSKImpactBrainSpineCompositeTimeline> statusWriter(){
        return new MSKImpactTimelineBrainSpineStatusWriter();
    }
    
    @Bean
    @StepScope
    public ItemStreamWriter<MSKImpactBrainSpineCompositeTimeline> specimenWriter(){
        return new MSKImpactTimelineBrainSpineSpecimenWriter();
    }
    
    @Bean
    @StepScope
    public ItemStreamWriter<MSKImpactBrainSpineCompositeTimeline> surgeryWriter(){
        return new MSKImpactTimelineBrainSpineSurgeryWriter();
    }
    
    @Bean
    @StepScope
    public ItemStreamWriter<MSKImpactBrainSpineCompositeTimeline> treatmentWriter(){
        return new MSKImpactTimelineBrainSpineTreatmentWriter();
    }
    
    @Bean
    @StepScope
    public ItemStreamWriter<MSKImpactBrainSpineCompositeTimeline> imagingWriter(){
        return new MSKImpactTimelineBrainSpineImagingWriter();
    }
    
    @Bean
    @StepScope
    public CompositeItemWriter<MSKImpactBrainSpineCompositeTimeline> writerDarwinTimelineBrainSpine(){
        List<ItemStreamWriter> writerDelegates = new ArrayList<>();
        writerDelegates.add(statusWriter());
        writerDelegates.add(surgeryWriter());
        writerDelegates.add(imagingWriter());
        writerDelegates.add(specimenWriter());
        writerDelegates.add(treatmentWriter());
        CompositeItemWriter writer = new CompositeItemWriter<>();
        writer.setDelegates(writerDelegates);
        return writer;
    }    
        
    @Bean
    @StepScope
    public ItemStreamReader<MSKImpactBrainSpineClinical> readerDarwinClinicalBrainSpine(){
        return new MSKImpactBrainSpineClinicalReader();
    }
    
    @Bean
    public MSKImpactBrainSpineClinicalProcessor processorDarwinClinicalBrainSpine(){
        return new MSKImpactBrainSpineClinicalProcessor();
    }
    
    @Bean
    @StepScope
    public MSKImpactBrainSpineClinicalWriter writerDarwinClinicalBrainSpine(){
        return new MSKImpactBrainSpineClinicalWriter();
    }
    
    @Bean
    @StepScope
    public MelanomaClinicalReader melanomaClinicalReader(){
        return new MelanomaClinicalReader();
    }
    
    @Bean
    @StepScope
    public MelanomaClinicalProcessor melanomaClinicalProcessor(){
        return new MelanomaClinicalProcessor();
    }    
    
    @Bean
    @StepScope
    public MelanomaClinicalWriter melanomaClinicalWriter(){
        return new MelanomaClinicalWriter();
    }   
	
    @Bean
    @StepScope
    public MelanomaTimelineReader melanomaTimelineReader(){
        return new MelanomaTimelineReader();
    }
    
    @Bean
    @StepScope
    public MelanomaTimelineProcessor melanomaTimelineProcessor(){
        return new MelanomaTimelineProcessor();
    }    
    
    @Bean
    @StepScope
    public MelanomaTimelineWriter melanomaTimelineWriter(){
        return new MelanomaTimelineWriter();
    }      
    
}
