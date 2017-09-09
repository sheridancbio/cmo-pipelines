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

package org.mskcc.cmo.ks.crdb;

import org.mskcc.cmo.ks.crdb.model.CRDBDataset;
import org.mskcc.cmo.ks.crdb.model.CRDBSurvey;
import org.mskcc.cmo.ks.crdb.pipeline.CRDBDatasetListener;
import org.mskcc.cmo.ks.crdb.pipeline.CRDBSurveyListener;
import org.springframework.batch.core.*;
import org.springframework.batch.core.configuration.annotation.*;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.*;

/**
 * Configuration for running the CRDB clinical data fetcher.
 *
 * @author ochoaa
 */

@Configuration
@EnableBatchProcessing
public class BatchConfiguration
{
    public static final String CRDB_IMPACT_JOB = "crdbImpactJob";

    @Autowired
    public JobBuilderFactory jobBuilderFactory;

    @Autowired
    public StepBuilderFactory stepBuilderFactory;

    @Bean
    public Job crdbImpactJob() {
        return jobBuilderFactory.get(CRDB_IMPACT_JOB)
            .start(step1())
            .next(step2())
            .build();
    }

    /**
     * Step 1 reads, processes, and writes the CRDB Survey query results
     */
    @Bean
    public Step step1() {
        return stepBuilderFactory.get("step1")
            .listener(CRDBSurveyListener())
            .<CRDBSurvey, String> chunk(10)
            .reader(reader1())
            .processor(processor1())
            .writer(writer1())
            .build();
    }

    @Bean
    @StepScope
	public ItemStreamReader<CRDBSurvey> reader1() {
		return new CRDBSurveyReader();
	}

    @Bean
    public CRDBSurveyProcessor processor1() {
        return new CRDBSurveyProcessor();
    }

    @Bean
    @StepScope
    public ItemStreamWriter<String> writer1() {
        return new CRDBSurveyWriter();
    }

    /**
     * Step 2 reads, processes, and writes the CRDB Dataset query results
     */
    @Bean
    public Step step2() {
        return stepBuilderFactory.get("step2")
            .listener(CRDBDatasetListener())
            .<CRDBDataset, String> chunk(10)
            .reader(reader2())
            .processor(processor2())
            .writer(writer2())
            .build();
    }

    @Bean
    public StepExecutionListener CRDBSurveyListener() {
        return new CRDBSurveyListener();
    }

    @Bean
    public StepExecutionListener CRDBDatasetListener() {
        return new CRDBDatasetListener();
    }

    @Bean
    @StepScope
	public ItemStreamReader<CRDBDataset> reader2() {
		return new CRDBDatasetReader();
	}

    @Bean
    public CRDBDatasetProcessor processor2() {
        return new CRDBDatasetProcessor();
    }

    @Bean
    @StepScope
    public ItemStreamWriter<String> writer2() {
        return new CRDBDatasetWriter();
    }
}
