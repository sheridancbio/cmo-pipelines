#!/bin/bash

# set necessary env variables with automation-environment.sh
if [[ -z $PORTAL_HOME || -z $JAVA_HOME ]] ; then
    echo "Error : import-public-data.sh cannot be run without setting PORTAL_HOME and JAVA_HOME environment variables. (Use automation-environment.sh)"
    exit 1
fi

tmp=$PORTAL_HOME/tmp/import-cron-public
if [[ -d "$tmp" && "$tmp" != "/" ]]; then
    rm -rf "$tmp"/*
fi
email_list="cbioportal-pipelines@cbio.mskcc.org"
now=$(date "+%Y-%m-%d-%H-%M-%S")
public_portal_notification_file=$(mktemp $tmp/public-portal-update-notification.$now.XXXXXX)
ONCOTREE_VERSION_TO_USE=oncotree_latest_stable

DB_VERSION_FAIL=0
# check database version before importing anything
echo "Checking if database version is compatible"
$JAVA_HOME/bin/java -Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=27185 -ea -Dspring.profiles.active=dbcp -Djava.io.tmpdir="$tmp" -cp $PORTAL_HOME/lib/public-importer.jar org.mskcc.cbio.importer.Admin --check-db-version
if [ $? -gt 0 ]; then
    echo "Database version expected by portal does not match version in database!"
    DB_VERSION_FAIL=1
fi

if [ $DB_VERSION_FAIL -eq 0 ]; then
    # import public studies into public portal
    echo "importing cancer type updates into public portal database..."
    $JAVA_HOME/bin/java -Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=27185 -Xmx16g -ea -Dspring.profiles.active=dbcp -Djava.io.tmpdir="$tmp" -cp $PORTAL_HOME/lib/public-importer.jar org.mskcc.cbio.importer.Admin --import-types-of-cancer --oncotree-version ${ONCOTREE_VERSION_TO_USE}
    echo "importing study data into public portal database..."
    IMPORT_FAIL=0
    $JAVA_HOME/bin/java -Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=27185 -Xmx64g -ea -Dspring.profiles.active=dbcp -Djava.io.tmpdir="$tmp" -cp $PORTAL_HOME/lib/public-importer.jar org.mskcc.cbio.importer.Admin --update-study-data --portal public-portal --update-worksheet --notification-file "$public_portal_notification_file" --oncotree-version ${ONCOTREE_VERSION_TO_USE} --transcript-overrides-source uniprot
    if [ $? -gt 0 ]; then
        echo "Public import failed!"
        IMPORT_FAIL=1
        EMAIL_BODY="Public import failed"
        echo -e "Sending email $EMAIL_BODY"
        echo -e "$EMAIL_BODY" | mail -s "Import failure: public" $email_list
    fi
    num_studies_updated=`cat $tmp/num_studies_updated.txt`
    echo "'$num_studies_updated' studies have been updated"
fi

echo "requesting redeployment of public portal war..."
TOMCAT_HOST_LIST=(dashi.cbio.mskcc.org dashi2.cbio.mskcc.org)
TOMCAT_HOST_USERNAME=cbioportal_importer
TOMCAT_HOST_SSH_KEY_FILE=${HOME}/.ssh/id_rsa_public_tomcat_restarts_key
TOMCAT_SERVER_RESTART_PATH=/srv/data/portal-cron/public-tomcat-restart
SSH_OPTIONS="-i ${TOMCAT_HOST_SSH_KEY_FILE} -o BATCHMODE=yes -o ConnectTimeout=3"
declare -a failed_restart_server_list
for server in ${TOMCAT_HOST_LIST[@]}; do
    if ! ssh ${SSH_OPTIONS} ${TOMCAT_HOST_USERNAME}@${server} touch ${TOMCAT_SERVER_RESTART_PATH} ; then
        failed_restart_server_list[${#failed_restart_server_list[*]}]=${server}
    fi
done
if [ ${#failed_restart_server_list[*]} -ne 0 ] ; then
    EMAIL_BODY="Attempt to trigger a restart of the public-tomcat server on the following hosts failed: ${failed_restart_server_list[*]}"
    echo -e "Sending email $EMAIL_BODY"
    echo -e "$EMAIL_BODY" | mail -s "Public Tomcat Restart Error : unable to trigger restart" $email_list
fi

EMAIL_BODY="The Public database version is incompatible. Imports will be skipped until database is updated."
# send email if db version isn't compatible
if [ $DB_VERSION_FAIL -gt 0 ]; then
    echo -e "Sending email $EMAIL_BODY"
    echo -e "$EMAIL_BODY" | mail -s "Public Update Failure: DB version is incompatible" $email_list
fi

$JAVA_HOME/bin/java -Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=27185 -Xmx16g -ea -Dspring.profiles.active=dbcp -Djava.io.tmpdir="$tmp" -cp $PORTAL_HOME/lib/public-importer.jar org.mskcc.cbio.importer.Admin --send-update-notification --portal public-portal --notification-file "$public_portal_notification_file"

if [[ -d "$tmp" && "$tmp" != "/" ]]; then
    rm -rf "$tmp"/*
fi
