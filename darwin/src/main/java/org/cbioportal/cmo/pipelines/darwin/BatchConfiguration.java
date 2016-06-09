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

import org.cbioportal.cmo.pipelines.darwin.model.DarwinPatientDemographics;
import org.cbioportal.cmo.pipelines.darwin.model.DarwinPatientIcdoRecord;
import org.cbioportal.cmo.pipelines.darwin.model.DarwinTimelineBrainSpine;

import org.springframework.batch.core.*;
import org.springframework.batch.item.*;
import org.springframework.batch.core.configuration.annotation.*;
import org.springframework.context.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.batch.core.configuration.annotation.StepScope;
/**
 *
 * @author jake
 */
@Configuration
@EnableBatchProcessing
public class BatchConfiguration {
    public static final String DARWIN_JOB = "darwinJob";
        
    @Autowired
    public JobBuilderFactory jobBuilderFactory;
    
    @Autowired
    public StepBuilderFactory stepBuilderFactory;
    
    @Bean
    public Job darwinJob(){
        return jobBuilderFactory.get(DARWIN_JOB)
                .start(stepDPD())
                .next(stepDTBS())
                .next(stepDPIR())
                .build();
    }
    
    @Bean
    public Step stepDPD(){
        return stepBuilderFactory.get("stepDPD")
                .<DarwinPatientDemographics, String> chunk(10)
                .reader(readerDPD())
                .processor(processorDPD())
                .writer(writerDPD())
                .build();
    }
    
    @Bean
    @StepScope
    public ItemStreamReader<DarwinPatientDemographics> readerDPD(){
        return new DarwinPatientDemographicsReader();
    }
    
    @Bean
    public DarwinPatientDemographicsProcessor processorDPD()
    {
        return new DarwinPatientDemographicsProcessor();
    }
    
    @Bean
    @StepScope
    public ItemStreamWriter<String> writerDPD()
    {
        return new DarwinPatientDemographicsWriter();
    }
    
    @Bean
    public Step stepDTBS(){
        return stepBuilderFactory.get("stepDTBS")
                .<DarwinTimelineBrainSpine, String> chunk(10)
                .reader(readerDTBS())
                .processor(processorDTBS())
                .writer(writerDTBS())
                .build();
    }
    
    @Bean
    @StepScope
    public ItemStreamReader<DarwinTimelineBrainSpine> readerDTBS(){
        return new DarwinTimelineBrainSpineReader();
    }
    
    @Bean
    public DarwinTimelineBrainSpineProcessor processorDTBS(){
        return new DarwinTimelineBrainSpineProcessor();
    }
    
    @Bean
    @StepScope
    public ItemStreamWriter<String> writerDTBS(){
        return new DarwinTimelineBrainSpineWriter();
    }
    
    @Bean
    public Step stepDPIR(){
        return stepBuilderFactory.get("stepDPIR")
                .<DarwinPatientIcdoRecord, String> chunk(10)
                .reader(readerDPIR())
                .processor(processorDPIR())
                .writer(writerDPIR())
                .build();
    }
    
    @Bean
    @StepScope
    public ItemStreamReader<DarwinPatientIcdoRecord> readerDPIR(){
        return new DarwinPatientIcdoReader();
    }
    
    @Bean
    public DarwinPatientIcdoProcessor processorDPIR(){
        return new DarwinPatientIcdoProcessor();
    }
    
    @Bean
    @StepScope
    public ItemStreamWriter<String> writerDPIR(){
        return new DarwinPatientIcdoWriter();
    }
    
}
