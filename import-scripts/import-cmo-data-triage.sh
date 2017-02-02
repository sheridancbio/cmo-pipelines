#!/bin/bash

# set necessary env variables with automation-environment.sh

echo $(date)
tmp=$PORTAL_HOME/tmp/import-cron-cmo-triage
if [[ -d "$tmp" && "$tmp" != "/" ]]; then
	rm -rf "$tmp"/*
fi

now=$(date "+%Y-%m-%d-%H-%M-%S")
triage_notification_file=$(mktemp $tmp/triage-portal-update-notification.$now.XXXXXX)

# fetch updates in CMO repository
echo "fetching updates from bic-mskcc..."
$JAVA_HOME/bin/java -Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=27183 -Xmx16g -ea -Dspring.profiles.active=dbcp -Djava.io.tmpdir="$tmp" -cp $PORTAL_HOME/lib/triage-cmo-importer.jar org.mskcc.cbio.importer.Admin -fetch_data bic-mskcc:latest:t

# fetch updates in private repository
echo "fetching updates from private..."
$JAVA_HOME/bin/java -Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=27183 -Xmx16g -ea -Dspring.profiles.active=dbcp -Djava.io.tmpdir="$tmp" -cp $PORTAL_HOME/lib/triage-cmo-importer.jar org.mskcc.cbio.importer.Admin -fetch_data private:latest:t

# fetch clinical data mercurial
echo "fetching updates from msk-impact repository..."
$JAVA_HOME/bin/java -Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=27183 -ea -Dspring.profiles.active=dbcp -Djava.io.tmpdir="$tmp" -cp $PORTAL_HOME/lib/triage-cmo-importer.jar org.mskcc.cbio.importer.Admin -fetch_data dmp-clinical-data-mercurial:latest:f

# fetch clinical data grail 
echo "fetching updates from grail repository..."
$JAVA_HOME/bin/java -Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=27183 -ea -Dspring.profiles.active=dbcp -Djava.io.tmpdir="$tmp" -cp $PORTAL_HOME/lib/triage-cmo-importer.jar org.mskcc.cbio.importer.Admin -fetch_data grail:latest:f

# fetch updates in CMO impact
echo "fetching updates from impact..."
$JAVA_HOME/bin/java -Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=27183 -Xmx16g -ea -Dspring.profiles.active=dbcp -Djava.io.tmpdir="$tmp" -cp $PORTAL_HOME/lib/triage-cmo-importer.jar org.mskcc.cbio.importer.Admin -fetch_data impact:latest:t
echo "fetching updates from impact-MERGED..."
$JAVA_HOME/bin/java -Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=27183-Xmx16g -ea -Dspring.profiles.active=dbcp -Djava.io.tmpdir="$tmp" -cp $PORTAL_HOME/lib/triage-cmo-importer.jar org.mskcc.cbio.importer.Admin -fetch_data impact-MERGED:latest:t

# fetch updates in studies repository
echo "fetching updates from cbio-portal-data..."
$JAVA_HOME/bin/java -Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=27183 -Xmx16g -ea -Dspring.profiles.active=dbcp -Djava.io.tmpdir="$tmp" -cp $PORTAL_HOME/lib/triage-cmo-importer.jar org.mskcc.cbio.importer.Admin -fetch_data knowledge-systems-curated-studies:latest:f

# import data that requires QC into triage portal
echo "importing cancer type updates into triage portal database..."
$JAVA_HOME/bin/java -Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=27183 -Xmx16g -ea -Dspring.profiles.active=dbcp -Djava.io.tmpdir="$tmp" -cp $PORTAL_HOME/lib/triage-cmo-importer.jar org.mskcc.cbio.importer.Admin -import_types_of_cancer

$JAVA_HOME/bin/java -Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=27183 -Xmx16g -ea -Dspring.profiles.active=dbcp -Djava.io.tmpdir="$tmp" -cp $PORTAL_HOME/lib/triage-cmo-importer.jar org.mskcc.cbio.importer.Admin -apply_overrides triage-portal

echo "importing study data into triage portal database..."
$JAVA_HOME/bin/java -Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=27183 -Xmx32G -ea -Dspring.profiles.active=dbcp -Djava.io.tmpdir="$tmp" -cp $PORTAL_HOME/lib/triage-cmo-importer.jar org.mskcc.cbio.importer.Admin -update_study_data triage-portal:t:$triage_notification_file:t
num_studies_updated=$?

# redeploy war
if [ $num_studies_updated -gt 0 ]
then
	#echo "'$num_studies_updated' studies have been updated, redeploying triage-portal war..."
	echo "'$num_studies_updated' studies have been updated.  Restarting triage-tomcat server..."
	sudo /etc/init.d/triage-tomcat7 restart
	#echo "'$num_studies_updated' studies have been updated (no longer need to restart triage-tomcat server...)"
else
	echo "No studies have been updated, skipping redeploy of triage-portal war..."
fi

$JAVA_HOME/bin/java -Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=27183 -Xmx16g -ea -Dspring.profiles.active=dbcp -Djava.io.tmpdir="$tmp" -cp $PORTAL_HOME/lib/triage-cmo-importer.jar org.mskcc.cbio.importer.Admin -send_update_notification triage-portal:$triage_notification_file

if [[ -d "$tmp" && "$tmp" != "/" ]]; then
	rm -rf "$tmp"/*
fi
