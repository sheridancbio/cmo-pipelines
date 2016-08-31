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
package org.cbioportal.cmo.clinical.data;

import org.cbioportal.cmo.clinical.data.pipeline.BatchConfiguration;

import org.apache.commons.cli.*;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.batch.core.*;
import org.springframework.batch.core.launch.JobLauncher;

/**
 *
 * @author heinsz
 */

@SpringBootApplication
public class ClinicalDataPipeline {
    
    private static Options getOptions(String[] args)
    {
        Options gnuOptions = new Options();
        gnuOptions.addOption("h", "help", false, "shows this help document and quits.")
            .addOption("p", "clinical_data_project", true, "Clinical Project Name")
            .addOption("d", "directory", true, "Output directory");

        return gnuOptions;
    }

    private static void help(Options gnuOptions, int exitStatus)
    {
        HelpFormatter helpFormatter = new HelpFormatter();
        helpFormatter.printHelp("ClinicalDataPipeline", gnuOptions);
        System.exit(exitStatus);
    }

    private static void launchJob(String[] args, String project, String directory) throws Exception
    {
        SpringApplication app = new SpringApplication(ClinicalDataPipeline.class);      
        ConfigurableApplicationContext ctx = app.run(args);
        JobLauncher jobLauncher = ctx.getBean(JobLauncher.class);
        JobParameters jobParameters = new JobParametersBuilder()
                .addString("clinical_data_project", project)
                .addString("directory", directory)
                    .toJobParameters();  
             
            Job clinicalDataJob = ctx.getBean(BatchConfiguration.CLINICAL_DATA_JOB, Job.class);       
            JobExecution jobExecution = jobLauncher.run(clinicalDataJob, jobParameters);                

    }
    
    public static void main(String[] args) throws Exception
    {        
        Options gnuOptions = ClinicalDataPipeline.getOptions(args);
        CommandLineParser parser = new GnuParser();
        CommandLine commandLine = parser.parse(gnuOptions, args);
        if (commandLine.hasOption("h") ||
            !commandLine.hasOption("directory") ||
            !commandLine.hasOption("clinical_data_project")) {
            help(gnuOptions, 0);
        }
        launchJob(args, commandLine.getOptionValue("clinical_data_project"), commandLine.getOptionValue("directory"));
    }    
}
