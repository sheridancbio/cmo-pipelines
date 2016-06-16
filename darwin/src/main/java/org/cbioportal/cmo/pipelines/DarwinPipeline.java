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
        .addOption("stage", "staging", true, "Staging directory")
        .addOption("study", "Study_ID", true, "Cancer Study ID");
        return gnuOptions;
    }
    
    private static void help(Options gnuOptions, int exitStatus){
        HelpFormatter helpFormatter = new HelpFormatter();
        helpFormatter.printHelp("Darwin Pipeline", gnuOptions);
        System.exit(exitStatus);
    }
    
    
    private static void launchJob(String[] args, String stagingDirectory, String studyID) throws Exception{
        SpringApplication app = new SpringApplication(DarwinPipeline.class);
        
        ConfigurableApplicationContext ctx = app.run(args);
        JobLauncher jobLauncher = ctx.getBean(JobLauncher.class);
       
        Job darwinJob;
        JobParameters jobParameters = new JobParametersBuilder()
                .addString("stagingDirectory", stagingDirectory)
                .addString("studyID", studyID)
                .toJobParameters();
        switch (studyID) {
            case "SomeStudy":
                darwinJob = ctx.getBean(BatchConfiguration.STUDYID_JOB, Job.class);
                break;
            case "MSK_IMPACT":
                darwinJob = ctx.getBean(BatchConfiguration.MSK_IMPACT_JOB, Job.class);
                break;
            default:
                darwinJob = null;
                break;
        }
        if(darwinJob!=null){
            JobExecution jobExecution = jobLauncher.run(darwinJob, jobParameters);
        }
        else{
            System.out.println("Failed to start DarwinPipeline");
        }
        
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
        
        launchJob(args, commandLine.getOptionValue("stage"), commandLine.getOptionValue("studyID"));
    }
            
        
    
}
