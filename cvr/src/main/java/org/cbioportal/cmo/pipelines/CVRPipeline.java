/*
 * Copyright (c) 2016 - 2017 Memorial Sloan-Kettering Cancer Center.
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

package org.cbioportal.cmo.pipelines;

/**
 *
 * @author heinsz
 */

import org.apache.commons.cli.*;
import org.apache.log4j.Logger;
import org.cbioportal.cmo.pipelines.cvr.BatchConfiguration;
import org.cbioportal.cmo.pipelines.cvr.SessionConfiguration;
import org.springframework.batch.core.*;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class CVRPipeline {

    private static Logger log = Logger.getLogger(CVRPipeline.class);

    private static Options getOptions(String[] args) {
        Options gnuOptions = new Options();
        gnuOptions.addOption("h", "help", false, "shows this help document and quits.")
            .addOption("d", "directory", true, "The staging directory")
            .addOption("j", "json", false, "To read or not to read")
            .addOption("g", "gml", false, "Run germline job")
            .addOption("s", "skipSeg", false, "Flag to skip fetching seg data")
            .addOption("m", "gmljson", false, "Only gml json")
            .addOption("t", "test", false, "Flag for running pipeline in testing mode so that samples are not consumed")
            .addOption("i", "study_id", true, "Study identifier (i.e., mskimpact, raindance, archer, etc.)");
        return gnuOptions;
    }

    private static void help(Options gnuOptions, int exitStatus) {
        HelpFormatter helpFormatter = new HelpFormatter();
        helpFormatter.printHelp("CVRPipeline", gnuOptions);
        System.exit(exitStatus);
    }

    private static void launchJob(String[] args, String directory, String studyId, Boolean json, Boolean gml, Boolean gmljson, Boolean skipSeg, Boolean testMode) throws Exception {
        SpringApplication app = new SpringApplication(CVRPipeline.class);
        ConfigurableApplicationContext ctx= app.run(args);
        JobLauncher jobLauncher = ctx.getBean(JobLauncher.class);
        
        // Job description from the BatchConfiguration
        String jobName;
        JobParametersBuilder builder = new JobParametersBuilder();
        builder.addString("stagingDirectory", directory)
                .addString("testingMode", String.valueOf(testMode))
                .addString("studyId", studyId);
        if (json) {
            jobName = BatchConfiguration.JSON_JOB;            
        }
        else if (gmljson) {
            jobName = BatchConfiguration.GML_JSON_JOB;
        }
        else if (gml) {
            // SessionID is gotten from a spring bean in the SessionConfiguration and passed through here as a param
            builder.addString("sessionId", ctx.getBean(SessionConfiguration.GML_SESSION, String.class));
            jobName = BatchConfiguration.GML_JOB;
        }
        else {
            // SessionID is gotten from a spring bean in the SessionConfiguration and passed through here as a param
            builder.addString("sessionId", ctx.getBean(SessionConfiguration.SESSION_ID, String.class))
                    .addString("skipSeg", String.valueOf(skipSeg));
            jobName = BatchConfiguration.CVR_JOB;
        }
        // log wether in testing mode or not
        if (testMode) {
            log.warn("CVR Pipeline running in TEST mode - samples will NOT be consumed");
        }
        else {
            log.warn("CVR Pipeline running in production mode - samples will be consumed");
        }
        // configure job and run
        Job cvrJob = ctx.getBean(jobName, Job.class);
        JobParameters jobParameters = builder.toJobParameters();
        JobExecution jobExecution = jobLauncher.run(cvrJob, jobParameters);
        log.info("Shutting down CVR Pipeline");
    }

    public static void main(String[] args) throws Exception {
        Options gnuOptions = CVRPipeline.getOptions(args);
        CommandLineParser parser = new GnuParser();
        CommandLine commandLine = parser.parse(gnuOptions, args);
        if (commandLine.hasOption("h") || !commandLine.hasOption("d") || !commandLine.hasOption("i")) {
            help(gnuOptions, 0);
        }
        launchJob(args, commandLine.getOptionValue("d"), commandLine.getOptionValue("i"), 
                commandLine.hasOption("j"), commandLine.hasOption("g"), commandLine.hasOption("m"), 
                commandLine.hasOption("s"), commandLine.hasOption("t"));
    }
}
