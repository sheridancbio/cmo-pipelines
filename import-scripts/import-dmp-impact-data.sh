#!/bin/bash

# set necessary env variables with automation-environment.sh

echo $(date)
tmp=$PORTAL_HOME/tmp/import-cron-dmp-msk

if [[ -d "$tmp" && "$tmp" != "/" ]]; then
	rm -rf "$tmp"/*
fi

now=$(date "+%Y-%m-%d-%H-%M-%S")
mskimpact_notification_file=$(mktemp $tmp/mskimpact-portal-update-notification.$now.XXXXXX)
#mskheme_notification_file=$(mktemp $tmp/mskheme-portal-update-notification.$now.XXXXXX)
mskraindance_notification_file=$(mktemp $tmp/mskraindance-portal-update-notification.$now.XXXXXX)
#mskarcher_notification_file=$(mktemp $tmp/mskarcher-portal-update-notification.$now.XXXXXX)

# fetch clinical data mercurial
echo "fetching updates from msk-impact repository..."
$JAVA_HOME/bin/java -Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=27182-ea -Dspring.profiles.active=dbcp -Djava.io.tmpdir="$tmp" -cp $PORTAL_HOME/lib/msk-dmp-importer.jar org.mskcc.cbio.importer.Admin -fetch_data dmp-clinical-data-mercurial:latest:f

# lets clean clinical data files
echo "cleaning all clinical & timeline data files - replacing carriage returns with newlines..."
files=$(ls $MSK_IMPACT_DATA_HOME/data_clinical*)
files="$files $(ls $MSK_IMPACT_DATA_HOME/data_timeline*)"
for file in $files
do
	tmp_file="$file.tmp"
	tr '\r' '\n' < $file > $tmp_file
	mv $tmp_file $file
done

# commit these changes
cd $MSK_IMPACT_DATA_HOME;$HG_BINARY commit -m "Latest MSK-IMPACT Dataset: newline fix"

# Code for getting line counts before fetching
    lcs=$(wc -l $MSK_IMPACT_DATA_HOME/data_clinical_supp_crdb*)
    OLDIFS=$IFS
    IFS=$'\n'
    for lc in $lcs
    do
	    echo $lc >> "$tmp"/tmpfile_crdb.tmp
    done
    IFS=$OLDIFS
    sed -e 's/[0-9]*\stotal//' < "$tmp"/tmpfile_crdb.tmp > "$tmp"/linecountsCVRDarwin_crdb.tmp 

FAIL_CRDB=0
FAIL_DARWIN=0
FAIL_CVR=0

# -----------------------------------------------------------------------------------------------------------
# lcCompare.py returns 0 on success (line counts do not decrease) 1 on failure (line counts decrease)
# if success import and push to mercurial

# fetch CRDB data
echo "fetching CRDB data"
$JAVA_HOME/bin/java -Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=27182 -jar $PORTAL_HOME/lib/crdb_fetcher.jar -stage $MSK_IMPACT_DATA_HOME

if ! $PYTHON_BINARY $PORTAL_HOME/scripts/lcCompare.py --line-counts="$tmp"/linecountsCVRDarwin_crdb.tmp --repo-path=$MSK_IMPACT_DATA_HOME --fail-output=$PORTAL_HOME/failed_CRDB/fail_message.txt; then 
	echo "Files appear smaller after CRDB fetch. Reverting mercurial repository"
    cp $MSK_IMPACT_DATA_HOME/* $PORTAL_HOME/failed_CRDB/
    cp "$tmp"/linecountsCVRDarwin_crdb.tmp $PORTAL_HOME/failed_CRDB/
	FAIL_CRDB=1
	cd $MSK_IMPACT_DATA_HOME;$HG_BINARY revert --all;rm *.orig
else
	echo "Files from CRDB fetch pass checks - committing changes locally"
	cd $MSK_IMPACT_DATA_HOME;$HG_BINARY commit -m "Latest MSK-IMPACT Dataset: CRDB"
fi

# Code for getting line counts before fetching
    lcs=$(wc -l $MSK_IMPACT_DATA_HOME/data_clinical_supp_caisis_gbm.txt)
    lcs="$lcs $(wc -l $MSK_IMPACT_DATA_HOME/data_clinical_supp_darwin*)"
    lcs="$lcs $(wc -l $MSK_IMPACT_DATA_HOME/data_timeline_diagnostics_caisis_gbm.txt)"
    lcs="$lcs $(wc -l $MSK_IMPACT_DATA_HOME/data_timeline_specimen_caisis_gbm.txt)"
    lcs="$lcs $(wc -l $MSK_IMPACT_DATA_HOME/data_timeline_status_caisis_gbm.txt)"
    lcs="$lcs $(wc -l $MSK_IMPACT_DATA_HOME/data_timeline_surgery_caisis_gbm.txt)"
    lcs="$lcs $(wc -l $MSK_IMPACT_DATA_HOME/data_timeline_treatment_caisis_gbm.txt)"
    OLDIFS=$IFS
    IFS=$'\n'
    for lc in $lcs
    do
	    echo $lc >> "$tmp"/tmpfile_darwin.tmp
    done
    IFS=$OLDIFS
    sed -e 's/[0-9]*\stotal//' < "$tmp"/tmpfile_darwin.tmp > "$tmp"/linecountsCVRDarwin_darwin.tmp 

# fetch Darwin data

echo "fetching Darwin data"
$JAVA_HOME/bin/java -Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=27182 -jar $PORTAL_HOME/lib/darwin_fetcher.jar -d $MSK_IMPACT_DATA_HOME -s mskimpact

if ! $PYTHON_BINARY $PORTAL_HOME/scripts/lcCompare.py --line-counts="$tmp"/linecountsCVRDarwin_darwin.tmp --repo-path=$MSK_IMPACT_DATA_HOME --fail-output=$PORTAL_HOME/failed_Darwin/fail_message.txt; then 
	echo "Files appear smaller after Darwin fetch. Reverting mercurial repository"
    cp $MSK_IMPACT_DATA_HOME/* $PORTAL_HOME/failed_Darwin/
    cp "$tmp"/linecountsCVRDarwin_darwin.tmp $PORTAL_HOME/failed_Darwin/
	FAIL_DARWIN=1
	cd $MSK_IMPACT_DATA_HOME;$HG_BINARY revert --all;rm *.orig
else
	echo "Files from Darwin fetch pass checks - committing changes locally"
	cd $MSK_IMPACT_DATA_HOME;$HG_BINARY commit -m "Latest MSK-IMPACT Dataset: Darwin"
fi

# Code for getting line counts before fetching
    lcs=$(wc -l $MSK_IMPACT_DATA_HOME/data_clinical.txt)
    OLDIFS=$IFS
    IFS=$'\n'
    for lc in $lcs
    do
	    echo $lc >> "$tmp"/tmpfile_cvr.tmp
    done
    IFS=$OLDIFS
    sed -e 's/[0-9]*\stotal//' < "$tmp"/tmpfile_cvr.tmp > "$tmp"/linecountsCVRDarwin_cvr.tmp 


# fetch new/updated IMPACT samples using CVR Web service   (must come after mercurial fetching) 
echo "fetching samples from CVR Web service  ..."
$JAVA_HOME/bin/java -Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=27182 -jar $PORTAL_HOME/lib/cvr_fetcher.jar -d $MSK_IMPACT_DATA_HOME -i mskimpact
# fetch new/updated IMPACT germline samples using CVR Web service   (must come after normal cvr fetching) 
echo "fetchomg CVR GML data ..."
$JAVA_HOME/bin/java -Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=27182 -jar $PORTAL_HOME/lib/cvr_fetcher.jar -d $MSK_IMPACT_DATA_HOME -g -i mskimpact -t
# fetch new/updated raindance samples using CVR Web service (must come after mercurial fetching). The -s flag skips segment data fetching
$JAVA_HOME/bin/java -Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=27182 -jar $PORTAL_HOME/lib/raindance_fetcher.jar -d $MSK_RAINDANCE_DATA_HOME -s -i raindance
# fetch new/updated raindance samples using CVR Web service (must come after mercurial fetching). The -s flag skips segment data fetching
#$JAVA_HOME/bin/java -Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=27182 -jar $PORTAL_HOME/lib/hemepact_fetcher.jar -d $MSK_HEMEPACT_DATA_HOME
# fetch new/updated raindance samples using CVR Web service (must come after mercurial fetching). The -s flag skips segment data fetching
#$JAVA_HOME/bin/java -Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=27182 -jar $PORTAL_HOME/lib/archer_fetcher.jar -d $MSK_ARCHER_DATA_HOME

if ! $PYTHON_BINARY $PORTAL_HOME/scripts/lcCompare.py --line-counts="$tmp"/linecountsCVRDarwin_cvr.tmp --repo-path=$MSK_IMPACT_DATA_HOME --fail-output=$PORTAL_HOME/failed_CVR/fail_message.txt; then 
	echo "Files appear smaller after CVR fetch. Reverting mercurial repository"
	FAIL_CVR=1
    cp $MSK_IMPACT_DATA_HOME/* $PORTAL_HOME/failed_CVR/
    cp $MSK_IMPACT_DATA_HOME/cvr_data.json $PORTAL_HOME/failed_CVR/cvr_data.$(date +%s).json
    cp "$tmp"/linecountsCVRDarwin_cvr.tmp $PORTAL_HOME/failed_CVR/
	cd $MSK_IMPACT_DATA_HOME;$HG_BINARY revert --all;rm *.orig
	cd $PORTAL_DATA_HOME;$HG_BINARY revert --all;rm *.orig
else
	echo "Files from CVR fetch pass checks - committing changes locally"
	cd $MSK_RAINDANCE_DATA_HOME; rm data_CNA.txt; rm data_fusions.txt; rm data_SV.txt; rm mskimpact_data_cna_hg19.seg;
	cd $MSK_IMPACT_DATA_HOME;$HG_BINARY commit -m "Latest MSK-IMPACT Dataset: CVR"
fi

#Refactor any residual DMP identifiers from staging files and ensure that other (i.e. legacy) identifiers are retained
# arguments are data source name and cancer study name
#echo "refactoring legacy DMP identifiers......."
#$JAVA_HOME/bin/java -Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=27182 -ea -Dspring.profiles.active=dbcp -Djava.io.tmpdir="$tmp" -cp $PORTAL_HOME/lib/msk-dmp-importer.jar org.mskcc.cbio.importer.cvr.dmp.util.ImpactDataClinicalTimelineIdDRefactoringUtility dmp-clinical-data-darwin

# create case lists by cancer type
rm $MSK_IMPACT_DATA_HOME/case_lists/*
$PYTHON_BINARY $PORTAL_HOME/scripts/oncotree_code_converter.py --oncotree-url "http://oncotree.mskcc.org/oncotree/api/tumor_types.txt" --clinical-file $MSK_IMPACT_DATA_HOME/data_clinical.txt
$PYTHON_BINARY $PORTAL_HOME/scripts/create_case_lists_by_cancer_type.py --clinical-file="$MSK_IMPACT_DATA_HOME"/data_clinical.txt --output-directory="$MSK_IMPACT_DATA_HOME"/case_lists
cd $MSK_IMPACT_DATA_HOME;$HG_BINARY add;$HG_BINARY commit -m "Latest MSK-IMPACT Dataset: Case Lists"

$PYTHON_BINARY $PORTAL_HOME/scripts/impact_timeline.py --hgrepo=$MSK_IMPACT_DATA_HOME
cd $MSK_IMPACT_DATA_HOME; rm *.orig
cd $MSK_IMPACT_DATA_HOME;$HG_BINARY add;$HG_BINARY commit -m "Latest MSK-IMPACT Dataset: Sample Date Clinical File"

# import into portal database
echo "importing cancer type updates into msk portal database..."
$JAVA_HOME/bin/java -Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=27182 -ea -Dspring.profiles.active=dbcp -Djava.io.tmpdir="$tmp" -cp $PORTAL_HOME/lib/msk-dmp-importer.jar org.mskcc.cbio.importer.Admin -import_types_of_cancer
echo "importing DMP-IMPACT study data into msk portal database..."
$JAVA_HOME/bin/java -Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=27182 -Xmx64g -ea -Dspring.profiles.active=dbcp -Djava.io.tmpdir="$tmp" -cp $PORTAL_HOME/lib/msk-dmp-importer.jar org.mskcc.cbio.importer.Admin -update_study_data mskimpact-portal:f:$mskimpact_notification_file:f
num_studies_updated=$?
$JAVA_HOME/bin/java -Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=27182 -Xmx64g -ea -Dspring.profiles.active=dbcp -Djava.io.tmpdir="$tmp" -cp $PORTAL_HOME/lib/msk-dmp-importer.jar org.mskcc.cbio.importer.Admin -update_study_data mskraindance-portal:f:$mskraindance_notification_file:f

# redeploy war
if [ $num_studies_updated -gt 0 ]
then
	echo "'$num_studies_updated' studies have been updated, requesting redeployment of msk portal war..."
	ssh -i $HOME/.ssh/id_rsa_tomcat_restarts_key cbioportal_importer@dashi.cbio.mskcc.org touch /srv/data/portal-cron/msk-tomcat-restart
	ssh -i $HOME/.ssh/id_rsa_tomcat_restarts_key cbioportal_importer@dashi2.cbio.mskcc.org touch /srv/data/portal-cron/msk-tomcat-restart
else
	# echo "No studies have been updated, skipping redeploy of msk portal war..."
	echo "No studies have been updated.."
fi

# check updated data back into mercurial
echo "Pushing DMP-IMPACT updates back to msk-impact repository..."
cd $MSK_IMPACT_DATA_HOME;$HG_BINARY push

### FAILURE EMAIL ###

message=""
EMAIL_BODY="Some file(s) edited by the import-dmp-impact-data.sh script in the msk-impact repository appear smaller than the originals."
EMAIL_CRDB="CRDB_failure"
EMAIL_DARWIN="Darwin_failure"
EMAIL_CVR="CVR_failure"
CAUSE_CRDB=$(<$PORTAL_HOME/failed_CRDB/fail_message.txt)
CAUSE_DARWIN=$(<$PORTAL_HOME/failed_Darwin/fail_message.txt)
CAUSE_CVR=$(<$PORTAL_HOME/failed_CVR/fail_message.txt)
# send email if failures occured
if [ $FAIL_CRDB -gt 0 ]
then
	message="$message\n\n$EMAIL_CRDB\n$CAUSE_CRDB"
fi
if [ $FAIL_DARWIN -gt 0 ]
then
	message="$message\n\n$EMAIL_DARWIN\n$CAUSE_DARWIN"
fi
if [ $FAIL_CVR -gt 0 ]
then
	message="$message\n\n$EMAIL_CVR\n$CAUSE_CVR"
fi
if [[ ! -z $message ]]
then
	echo -e "Sending email $EMAIL_BODY$message"
	echo -e "$EMAIL_BODY$message" | mail -s "WARNING: CVR Pipeline Potential Data Loss" cbioportal-cmo-importer@cbio.mskcc.org
fi

$JAVA_HOME/bin/java -Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=27182 -Xmx16g -ea -Dspring.profiles.active=dbcp -Djava.io.tmpdir="$tmp" -cp $PORTAL_HOME/lib/msk-dmp-importer.jar org.mskcc.cbio.importer.Admin -send_update_notification mskimpact-portal:$mskimpact_notification_file
$JAVA_HOME/bin/java -Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=27182 -Xmx16g -ea -Dspring.profiles.active=dbcp -Djava.io.tmpdir="$tmp" -cp $PORTAL_HOME/lib/msk-dmp-importer.jar org.mskcc.cbio.importer.Admin -send_update_notification mskraindance-portal:$mskraindance_notification_file

if [[ -d "$tmp" && "$tmp" != "/" ]]; then
	rm -rf "$tmp"/*
fi
