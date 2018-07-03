#!/bin/bash

JENKINS_PROPERTIES_DIRECTORY=/home/jenkins-user/pipelines-configuration/properties
JENKINS_PIPELINES_CREDENTIALS=/home/jenkins-user/pipelines-credentials
TESTING_DIRECTORY=/home/jenkins-user/tempdir

# CMO_PIPELINES_DIRECTORY is starting directory - location on jenkins machine where github repo is cloned
CMO_PIPELINES_DIRECTORY=`pwd`
APPLICATION_PROPERTIES=application.properties

cp $JENKINS_PROPERTIES_DIRECTORY/redcap-pipeline/$APPLICATION_PROPERTIES $CMO_PIPELINES_DIRECTORY/redcap/redcap_pipeline/src/main/resources
cp $JENKINS_PROPERTIES_DIRECTORY/fetch-cvr/$APPLICATION_PROPERTIES $CMO_PIPELINES_DIRECTORY/cvr/src/main/resources
cp $JENKINS_PROPERTIES_DIRECTORY/fetch-darwin/$APPLICATION_PROPERTIES $CMO_PIPELINES_DIRECTORY/darwin/src/main/resources
cp $JENKINS_PROPERTIES_DIRECTORY/fetch-crdb/$APPLICATION_PROPERTIES $CMO_PIPELINES_DIRECTORY/crdb/src/main/resources
cp $JENKINS_PROPERTIES_DIRECTORY/fetch-ddp/$APPLICATION_PROPERTIES $CMO_PIPELINES_DIRECTORY/ddp/ddp_pipeline/src/main/resources
cp $JENKINS_PIPELINES_CREDENTIALS/fetch-ddp/application-secure.properties $CMO_PIPELINES_DIRECTORY/ddp/ddp_pipeline/src/main/resources
