/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cbioportal.cmo.pipelines;

import org.cbioportal.cmo.pipelines.darwin.BatchConfiguration;
import org.apache.commons.cli.*;
import org.apache.commons.cli.Options;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.batch.core.*;
import org.springframework.batch.core.launch.JobLauncher;
/**
 *
 * @author jake
 */
@SpringBootApplication
public class DarwinPipeline {
    
    private static Options getOptions(String[] args){
        Options gnuOptions = new Options();
        gnuOptions.addOption("h", "help", false, "shows this help document and quits.")
        .addOption("stage", "staging", true, "Staging directory");
        return gnuOptions;
    }
    
    private static void help(Options gnuOptions, int exitStatus){
        HelpFormatter helpFormatter = new HelpFormatter();
        helpFormatter.printHelp("Darwin Pipeline", gnuOptions);
        System.exit(exitStatus);
    }
    
    
    private static void launchJob(String[] args, String stagingDirectory) throws Exception{
        SpringApplication app = new SpringApplication(DarwinPipeline.class);
        ConfigurableApplicationContext ctx = app.run(args);
        JobLauncher jobLauncher = ctx.getBean(JobLauncher.class);
       
        Job darwinJob = ctx.getBean(BatchConfiguration.DARWIN_JOB, Job.class);
        JobParameters jobParameters = new JobParametersBuilder()
                .addString("stagingDirectory", stagingDirectory)
                .toJobParameters();
        JobExecution jobExecution = jobLauncher.run(darwinJob, jobParameters);
        
        System.out.println("Shutting down DarwinPipeline");
        ctx.close();
    }
    
    public static void main(String[] args) throws Exception{
        Options gnuOptions = DarwinPipeline.getOptions(args);
        CommandLineParser parser = new GnuParser();
        CommandLine commandLine = parser.parse(gnuOptions, args);
        if (commandLine.hasOption("h")||
            !commandLine.hasOption("stage")){
            help(gnuOptions, 0);
        }
        
        launchJob(args, commandLine.getOptionValue("stage"));
    }
            
        
    
}
