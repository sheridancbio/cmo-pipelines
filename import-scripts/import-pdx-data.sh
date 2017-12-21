#!/bin/bash

# set necessary env variables with automation-environment.sh

tmp=$PORTAL_HOME/tmp/import-cron-pdx
if [[ -d "$tmp" && "$tmp" != "/" ]] ; then
    rm -rf "$tmp"/*
fi

now=$(date "+%Y-%m-%d-%H-%M-%S")
JAVA_PROXY_ARGS="-Dhttp.proxyHost=jxi2.mskcc.org -Dhttp.proxyPort=8080"
pdx_automation_notification_file=$(mktemp $tmp/pdx-automation-portal-update-notification.$now.XXXXXX)
ONCOTREE_VERSION_TO_USE=oncotree_latest_stable

# import vetted studies into MSK portal
echo "importing cancer type updates into msk portal database..."
$JAVA_HOME/bin/java $JAVA_PROXY_ARGS -Xmx16g -ea -Dspring.profiles.active=dbcp -Djava.io.tmpdir="$tmp" -cp $PORTAL_HOME/lib/msk-pdx-importer.jar org.mskcc.cbio.importer.Admin -import_types_of_cancer --oncotree-version ${ONCOTREE_VERSION_TO_USE}
echo "importing study data into msk portal database..."
$JAVA_HOME/bin/java $JAVA_PROXY_ARGS -Xmx16g -ea -Dspring.profiles.active=dbcp -Djava.io.tmpdir="$tmp" -cp $PORTAL_HOME/lib/msk-pdx-importer.jar org.mskcc.cbio.importer.Admin -update_study_data pdx-portal:t:$pdx_automation_notification_file:t --oncotree-version ${ONCOTREE_VERSION_TO_USE}
num_studies_updated=$?

# redeploy war
if [ $num_studies_updated -gt 0 ]
then
    echo "'$num_studies_updated' studies have been updated, requesting redeployment of msk portal war..."
    TOMCAT_HOST_LIST=(dashi.cbio.mskcc.org dashi2.cbio.mskcc.org)
    TOMCAT_HOST_USERNAME=cbioportal_importer
    TOMCAT_HOST_SSH_KEY_FILE=${HOME}/.ssh/id_rsa_schultz_tomcat_restarts_key
    TOMCAT_SERVER_RESTART_PATH=/srv/data/portal-cron/schultz-tomcat-restart
    TOMCAT_SERVER_PRETTY_DISPLAY_NAME="Schultz Tomcat" # e.g. Public Tomcat
    TOMCAT_SERVER_DISPLAY_NAME="schultz-tomcat" # e.g. schultz-tomcat
    SSH_OPTIONS="-i ${TOMCAT_HOST_SSH_KEY_FILE} -o BATCHMODE=yes -o ConnectTimeout=3"
    declare -a failed_restart_server_list
    for server in ${TOMCAT_HOST_LIST[@]}; do
        if ! ssh ${SSH_OPTIONS} ${TOMCAT_HOST_USERNAME}@${server} touch ${TOMCAT_SERVER_RESTART_PATH} ; then
            failed_restart_server_list[${#failed_restart_server_list[*]}]=${server}
        fi
    done
    if [ ${#failed_restart_server_list[*]} -ne 0 ] ; then
        EMAIL_BODY="Attempt to trigger a restart of the $TOMCAT_SERVER_DISPLAY_NAME server on the following hosts failed: ${failed_restart_server_list[*]}"
        echo -e "Sending email $EMAIL_BODY"
        echo -e "$EMAIL_BODY" | mail -s "$TOMCAT_SERVER_PRETTY_DISPLAY_NAME Restart Error : unable to trigger restart" $email_list
    fi
    #echo "'$num_studies_updated' studies have been updated (no longer need to restart $TOMCAT_SERVER_DISPLAY_NAME server...)"
else
    #echo "No studies have been updated, skipping redeploy of msk portal war..."
    echo "No studies have been updated.."
fi

$JAVA_HOME/bin/java $JAVA_PROXY_ARGS -Xmx16g -ea -Dspring.profiles.active=dbcp -Djava.io.tmpdir="$tmp" -cp $PORTAL_HOME/lib/msk-pdx-importer.jar org.mskcc.cbio.importer.Admin -send_update_notification pdx-portal:$pdx_automation_notification_file

if [[ -d "$tmp" && "$tmp" != "/" ]] ; then
    rm -rf "$tmp"/*
fi
