#!/bin/bash

# set necessary env variables with automation-environment.sh

tmp=$PORTAL_HOME/tmp/import-cron-genie
if [[ -d "$tmp" && "$tmp" != "/" ]]; then
    rm -rf "$tmp"/*
fi
email_list="cbioportal-pipelines@cbio.mskcc.org"
now=$(date "+%Y-%m-%d-%H-%M-%S")
genie_portal_notification_file=$(mktemp $tmp/genie-portal-update-notification.$now.XXXXXX)

DB_VERSION_FAIL=0
# check database version before importing anything
echo "Checking if database version is compatible"
$JAVA_HOME/bin/java -Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=27186 -ea -Dspring.profiles.active=dbcp -Djava.io.tmpdir="$tmp" -cp $PORTAL_HOME/lib/genie-importer.jar org.mskcc.cbio.importer.Admin --check-db-version
if [ $? -gt 0 ]; then
    echo "Database version expected by portal does not match version in database!"
    DB_VERSION_FAIL=1
fi

if [ $DB_VERSION_FAIL -eq 0 ]; then
    # import genie studies into genie portal
    echo "importing cancer type updates into genie portal database..."
    $JAVA_HOME/bin/java -Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=27186 -Xmx16g -ea -Dspring.profiles.active=dbcp -Djava.io.tmpdir="$tmp" -cp $PORTAL_HOME/lib/genie-importer.jar org.mskcc.cbio.importer.Admin --import-types-of-cancer
    echo "importing study data into genie portal database..."
    $JAVA_HOME/bin/java -Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=27186 -Xmx64g -ea -Dspring.profiles.active=dbcp -Djava.io.tmpdir="$tmp" -cp $PORTAL_HOME/lib/genie-importer.jar org.mskcc.cbio.importer.Admin --update-study-data --portal genie-portal --update-worksheet --notification-file "$genie_portal_notification_file"
    num_studies_updated=`cat $tmp/num_studies_updated.txt`

    # redeploy war
    if [ $num_studies_updated -gt 0 ]; then
        echo "'$num_studies_updated' studies have been updated, requesting redeployment of genie portal war..."
        ssh -i $HOME/.ssh/id_rsa_schultz_tomcat_restarts_key cbioportal_importer@dashi.cbio.mskcc.org touch /srv/data/portal-cron/schultz-tomcat-restart
        ssh -i $HOME/.ssh/id_rsa_schultz_tomcat_restarts_key cbioportal_importer@dashi2.cbio.mskcc.org touch /srv/data/portal-cron/schultz-tomcat-restart
        echo "'$num_studies_updated' studies have been updated (no longer need to restart schultz-tomcat server...)"
    else
        echo "No studies have been updated, skipping redeploy of schultz portal war..."
    fi
fi

EMAIL_BODY="The genie database version is incompatible. Imports will be skipped until database is updated."
# send email if db version isn't compatible
if [ $DB_VERSION_FAIL -gt 0 ]; then
    echo -e "Sending email $EMAIL_BODY"
    echo -e "$EMAIL_BODY" | mail -s "GENIE Update Failure: DB version is incompatible" $email_list
fi

$JAVA_HOME/bin/java -Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=27186 -Xmx16g -ea -Dspring.profiles.active=dbcp -Djava.io.tmpdir="$tmp" -cp $PORTAL_HOME/lib/genie-importer.jar org.mskcc.cbio.importer.Admin --send-update-notification --portal genie-portal --notification-file "$genie_portal_notification_file"

if [[ -d "$tmp" && "$tmp" != "/" ]]; then    
    rm -rf "$tmp"/*
fi
