#!/bin/bash

# set necessary env variables with automation-environment.sh

echo $(date)
tmp=$PORTAL_HOME/tmp/import-cron-dmp-msk
num_studies_updated=0
email_list="heinsz@mskcc.org, sheridar@mskcc.org, grossb1@mskcc.org, ochoaa@mskcc.org, wilsonm2@mskcc.org"

if [[ -d "$tmp" && "$tmp" != "/" ]]; then
	rm -rf "$tmp"/*
fi

now=$(date "+%Y-%m-%d-%H-%M-%S")
mskimpact_notification_file=$(mktemp $tmp/mskimpact-portal-update-notification.$now.XXXXXX)
mskheme_notification_file=$(mktemp $tmp/mskheme-portal-update-notification.$now.XXXXXX)
mskraindance_notification_file=$(mktemp $tmp/mskraindance-portal-update-notification.$now.XXXXXX)
mixedpact_notification_file=$(mktemp $tmp/mixedpact-portal-update-notification.$now.XXXXXX)
#mskarcher_notification_file=$(mktemp $tmp/mskarcher-portal-update-notification.$now.XXXXXX)

# fetch clinical data mercurial
echo "fetching updates from msk-impact repository..."
$JAVA_HOME/bin/java -Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=27182 -ea -Dspring.profiles.active=dbcp -Djava.io.tmpdir="$tmp" -cp $PORTAL_HOME/lib/msk-dmp-importer.jar org.mskcc.cbio.importer.Admin --fetch-data --data-source dmp-clinical-data-mercurial --run-date latest

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

# -----------------------------------------------------------------------------------------------------------

# fetch CRDB data
echo "fetching CRDB data"
$JAVA_HOME/bin/java -Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=27182 -jar $PORTAL_HOME/lib/crdb_fetcher.jar -stage $MSK_IMPACT_DATA_HOME
cd $MSK_IMPACT_DATA_HOME;$HG_BINARY commit -m "Latest MSK-IMPACT Dataset: CRDB"

# fetch Darwin data

echo "fetching Darwin data"
$JAVA_HOME/bin/java -Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=27182 -jar $PORTAL_HOME/lib/darwin_fetcher.jar -d $MSK_IMPACT_DATA_HOME -s mskimpact
cd $MSK_IMPACT_DATA_HOME;$HG_BINARY commit -m "Latest MSK-IMPACT Dataset: Darwin"

echo "fetching Darwin heme data"
$JAVA_HOME/bin/java -Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=27182 -jar $PORTAL_HOME/lib/darwin_fetcher.jar -d $MSK_HEMEPACT_DATA_HOME -s mskimpact_heme
cd $MSK_HEMEPACT_DATA_HOME;$HG_BINARY commit -m "Latest MSK-IMPACT Dataset: Darwin heme"

DB_VERSION_FAIL=0
IMPORT_STATUS=0
IMPORT_FAIL=0
VALIDATION_FAIL=0
DELETE_FAIL=0
RENAME_BACKUP_FAIL=0
RENAME_FAIL=0
GROUPS_FAIL=0
SUCCESS=0
MERGE_FAIL=0
MIXEDPACT_IMPORT_FAIL=0

# fetch new/updated IMPACT samples using CVR Web service   (must come after mercurial fetching) 
echo "fetching samples from CVR Web service  ..."
$JAVA_HOME/bin/java -Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=27182 -jar $PORTAL_HOME/lib/cvr_fetcher.jar -d $MSK_IMPACT_DATA_HOME -i mskimpact
if [ $? -gt 0 ]
then
	echo "CVR fetch failed!"
	cd $MSK_IMPACT_DATA_HOME;$HG_BINARY revert --all --no-backup;rm *.orig
	IMPORT_STATUS=1
else
	echo "committing cvr data"
	cd $MSK_IMPACT_DATA_HOME;$HG_BINARY commit -m "Latest MSK-IMPACT Dataset: CVR"
fi

# fetch new/updated IMPACT germline samples using CVR Web service   (must come after normal cvr fetching) 
echo "fetching CVR GML data ..."
$JAVA_HOME/bin/java -Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=27182 -jar $PORTAL_HOME/lib/cvr_fetcher.jar -d $MSK_IMPACT_DATA_HOME -g -i mskimpact
if [ $? -gt 0 ]
then
	echo "CVR Germline fetch failed!"
	cd $MSK_IMPACT_DATA_HOME;$HG_BINARY revert --all --no-backup;rm *.orig
	IMPORT_STATUS=1
else
	echo "committing CVR germline data"
	cd $MSK_IMPACT_DATA_HOME;$HG_BINARY commit -m "Latest MSK-IMPACT Dataset: CVR Germline"
fi

# fetch new/updated raindance samples using CVR Web service (must come after mercurial fetching). The -s flag skips segment data fetching
$JAVA_HOME/bin/java -Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=27182 -jar $PORTAL_HOME/lib/cvr_fetcher.jar -d $MSK_RAINDANCE_DATA_HOME -s -i mskraindance
if [ $? -gt 0 ]
then
	echo "CVR raindance fetch failed!"
	echo "This will not affect importing of mskimpact"
	cd $MSK_RAINDANCE_DATA_HOME;$HG_BINARY revert --all --no-backup;rm *.orig
else
	# raindance does not provide copy number or fusions data.
	echo "removing unused files"
	cd $MSK_RAINDANCE_DATA_HOME; rm data_CNA.txt; rm data_fusions.txt; rm data_SV.txt; rm mskraindance_data_cna_hg19.seg;
	cd $MSK_RAINDANCE_DATA_HOME;$HG_BINARY commit -m "Latest Raindance dataset"
fi

echo "fetching CVR heme data ..."
$JAVA_HOME/bin/java -Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=27182 -jar $PORTAL_HOME/lib/cvr_fetcher.jar -d $MSK_HEMEPACT_DATA_HOME -i mskimpact_heme
if [ $? -gt 0 ]
then
      echo "CVR heme fetch failed!"
      echo "This will not affect importing of mskimpact"
      cd $MSK_HEMEPACT_DATA_HOME;$HG_BINARY revert --all --no-backup;rm *.orig
else
      cd $MSK_HEMEPACT_DATA_HOME;$HG_BINARY commit -m "Latest heme dataset"
fi

# fetch new/updated archer samples using CVR Web service (must come after mercurial fetching).
#$JAVA_HOME/bin/java -Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=27182 -jar $PORTAL_HOME/lib/cvr_fetcher.jar -d $MSK_ARCHER_DATA_HOME -i mskarcher
#if [ $? -gt 0 ]
#then
	#echo "CVR Archer fetch failed!"
	#echo "This will not affect importing of mskimpact"
	#cd $MSK_IMPACT_DATA_HOME;$HG_BINARY revert --all --no-backup;rm *.orig
#else
	#cd $MSK_IMPACT_DATA_HOME;$HG_BINARY commit -m "Latest archer dataset"
#fi

# create case lists by cancer type
rm $MSK_IMPACT_DATA_HOME/case_lists/*
$PYTHON_BINARY $PORTAL_HOME/scripts/oncotree_code_converter.py --oncotree-url "http://oncotree.mskcc.org/oncotree/api/tumor_types.txt" --clinical-file $MSK_IMPACT_DATA_HOME/data_clinical.txt
$PYTHON_BINARY $PORTAL_HOME/scripts/create_case_lists_by_cancer_type.py --clinical-file="$MSK_IMPACT_DATA_HOME"/data_clinical.txt --output-directory="$MSK_IMPACT_DATA_HOME"/case_lists --study-id=mskimpact
cd $MSK_IMPACT_DATA_HOME;$HG_BINARY add;$HG_BINARY commit -m "Latest MSK-IMPACT Dataset: Case Lists"

$PYTHON_BINARY $PORTAL_HOME/scripts/impact_timeline.py --hgrepo=$MSK_IMPACT_DATA_HOME
sed -i '/^\s*$/d' $MSK_IMPACT_DATA_HOME/data_clinical_supp_date.txt
cd $MSK_IMPACT_DATA_HOME; rm *.orig
rm $MSK_IMPACT_DATA_HOME/case_lists/*.orig
cd $MSK_IMPACT_DATA_HOME;$HG_BINARY add;$HG_BINARY commit -m "Latest MSK-IMPACT Dataset: Sample Date Clinical File"


# check database version before importing anything
echo "Checking if database version is compatible"
$JAVA_HOME/bin/java -Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=27182 -ea -Dspring.profiles.active=dbcp -Djava.io.tmpdir="$tmp" -cp $PORTAL_HOME/lib/msk-dmp-importer.jar org.mskcc.cbio.importer.Admin --check-db-version
if [ $? -gt 0 ]
then
    echo "Database version expected by portal does not match version in database!"
    DB_VERSION_FAIL=1
    IMPORT_STATUS=1
fi    

if [ $DB_VERSION_FAIL -eq 0 ]
then 
    # import into portal database
    echo "importing cancer type updates into msk portal database..."
    $JAVA_HOME/bin/java -Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=27182 -ea -Dspring.profiles.active=dbcp -Djava.io.tmpdir="$tmp" -cp $PORTAL_HOME/lib/msk-dmp-importer.jar org.mskcc.cbio.importer.Admin --import-types-of-cancer
fi


## use flag to import under temporary id

if [ $IMPORT_STATUS -eq 0 ]
then
	echo "Importing temporary mskimpact..."
	$JAVA_HOME/bin/java -Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=27182 -Xmx64g -ea -Dspring.profiles.active=dbcp -Djava.io.tmpdir="$tmp" -cp $PORTAL_HOME/lib/msk-dmp-importer.jar org.mskcc.cbio.importer.Admin --update-study-data --portal mskimpact-portal --notification-file "$mskimpact_notification_file" --temporary-id temporary_mskimpact
	if [ -f "$tmp/num_studies_updated.txt" ]
	then
		num_studies_updated=`cat $tmp/num_studies_updated.txt`
	else
		num_studies_updated=0
	fi
	if [ "$num_studies_updated" -ne 1 ]
	then
		echo "Failed to import mskimpact!"
		IMPORT_FAIL=1
	else
		# validate
		echo "validating..."
		$JAVA_HOME/bin/java -Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=27182 -Xmx64g -ea -Dspring.profiles.active=dbcp -Djava.io.tmpdir="$tmp" -cp $PORTAL_HOME/lib/msk-dmp-importer.jar org.mskcc.cbio.importer.Admin --validate-temp-study --temp-study-id temporary_mskimpact --original-study-id mskimpact
		if [ $? -gt 0 ]
		then
			echo "Failed to validate - deleting temp study"
			$JAVA_HOME/bin/java -Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=27182 -Xmx64g -ea -Dspring.profiles.active=dbcp -Djava.io.tmpdir="$tmp" -cp $PORTAL_HOME/lib/msk-dmp-importer.jar org.mskcc.cbio.importer.Admin --delete-cancer-study --cancer-study-ids temporary_mskimpact
			VALIDATION_FAIL=1
		else
			echo "Successful validation - renaming mskimpact and temp studies"
			$JAVA_HOME/bin/java -Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=27182 -Xmx64g -ea -Dspring.profiles.active=dbcp -Djava.io.tmpdir="$tmp" -cp $PORTAL_HOME/lib/msk-dmp-importer.jar org.mskcc.cbio.importer.Admin --delete-cancer-study --cancer-study-ids yesterday_mskimpact
			if [ $? -gt 0 ]
			then
				echo "Failed to delete cancer study yesterday_mskimpact!"
				DELETE_FAIL=1
			else
				echo "Renaming mksimpact to yesterday_mskimpact"
				$JAVA_HOME/bin/java -Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=27182 -Xmx64g -ea -Dspring.profiles.active=dbcp -Djava.io.tmpdir="$tmp" -cp $PORTAL_HOME/lib/msk-dmp-importer.jar org.mskcc.cbio.importer.Admin --rename-cancer-study --new-study-id yesterday_mskimpact --original-study-id mskimpact
				if [ $? -gt 0 ]
				then
					echo "Failed to rename old mskimpact study to yesterday!"
					RENAME_BACKUP_FAIL=1
				else
					echo "Updating groups of yesterday_mskimpact to KSBACKUP"
					$JAVA_HOME/bin/java -Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=27182 -Xmx64g -ea -Dspring.profiles.active=dbcp -Djava.io.tmpdir="$tmp" -cp $PORTAL_HOME/lib/msk-dmp-importer.jar org.mskcc.cbio.importer.Admin --update-groups --cancer-study-ids yesterday_mskimpact --groups "KSBACKUP"
					if [ $? -gt 0 ]
					then
						echo "Failed to change groups!"
						GROUPS_FAIL=1
					fi
					echo "renaming temporary_mskimpact to mskimpact"
					$JAVA_HOME/bin/java -Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=27182 -Xmx64g -ea -Dspring.profiles.active=dbcp -Djava.io.tmpdir="$tmp" -cp $PORTAL_HOME/lib/msk-dmp-importer.jar org.mskcc.cbio.importer.Admin --rename-cancer-study --new-study-id mskimpact --original-study-id temporary_mskimpact
					SUCCESS=1
					if [ $? -gt 0 ]
					then
						echo "Failed to rename temp study to mskimpact!"
						RENAME_FAIL=1
					else
						echo "Consuming samples from cvr"
						$JAVA_HOME/bin/java -Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=27182 -jar $PORTAL_HOME/lib/cvr_fetcher.jar -c $MSK_IMPACT_DATA_HOME/cvr_data.json
					fi
				fi
			fi
		fi
	fi

else
    if [ $DB_VERSION_FAIL -gt 0 ]
    then
        echo "Not importing - database version is not compatible"
    else
    	echo "Not importing - something went wrong with a fetch"
    fi
fi

echo "Importing mskimpact heme..."
$JAVA_HOME/bin/java -Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=27182 -Xmx64g -ea -Dspring.profiles.active=dbcp -Djava.io.tmpdir="$tmp" -cp $PORTAL_HOME/lib/msk-dmp-importer.jar org.mskcc.cbio.importer.Admin --update-study-data --portal mskheme-portal --notification-file "$mskheme_notification_file"
#echo "Importing mskimpact raindance..."
#$JAVA_HOME/bin/java -Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=27182 -Xmx64g -ea -Dspring.profiles.active=dbcp -Djava.io.tmpdir="$tmp" -cp $PORTAL_HOME/lib/msk-dmp-importer.jar org.mskcc.cbio.importer.Admin --update-study-data --portal mskraindance-portal --notification-file "$mskraindance_notification_file"


## MSK-IMPACT, HEMEPACT, and RAINDANCE merge
echo "Beginning merge of MSK-IMPACT, HEMEPACT, RAINDANCE data..."

# first touch meta_clinical.txt and meta_SV.txt in each directory if not already exists
if [ ! -f $MSK_IMPACT_DATA_HOME/meta_clinical.txt ]; then
    touch $MSK_IMPACT_DATA_HOME/meta_clinical.txt
fi

if [ ! -f $MSK_IMPACT_DATA_HOME/meta_SV.txt ]; then
    touch $MSK_IMPACT_DATA_HOME/meta_SV.txt
fi

if [ ! -f $MSK_HEMEPACT_DATA_HOME/meta_clinical.txt ]; then
    touch $MSK_HEMEPACT_DATA_HOME/meta_clinical.txt
fi

if [ ! -f $MSK_HEMEPACT_DATA_HOME/meta_SV.txt ]; then
    touch $MSK_HEMEPACT_DATA_HOME/meta_SV.txt
fi

# raindance doesn't have SV data so no need to touch that meta file
if [ ! -f $MSK_RAINDANCE_DATA_HOME/meta_clinical.txt ]; then
    touch $MSK_RAINDANCE_DATA_HOME/meta_clinical.txt
fi

# merge data from both directories and check exit code
$PYTHON_BINARY $PORTAL_HOME/scripts/merge.py -d $MSK_MIXEDPACT_DATA_HOME -i mixedpact -m "true" $MSK_IMPACT_DATA_HOME $MSK_HEMEPACT_DATA_HOME $MSK_RAINDANCE_DATA_HOME
if [ $? -gt 0 ]; then
    echo "MIXEDPACT merge failed! Study will not be updated in the portal."
    MERGE_FAIL=1
else
    # if merge successful then copy case lists from MSK-IMPACT directory and change stable id
    echo "MIXEDPACT merge successful! Creating cancer type case lists..."
    rm $MSK_MIXEDPACT_DATA_HOME/case_lists/*
    $PYTHON_BINARY $PORTAL_HOME/scripts/oncotree_code_converter.py --oncotree-url "http://oncotree.mskcc.org/oncotree/api/tumor_types.txt" --clinical-file $MSK_MIXEDPACT_DATA_HOME/data_clinical.txt
    $PYTHON_BINARY $PORTAL_HOME/scripts/create_case_lists_by_cancer_type.py --clinical-file="$MSK_MIXEDPACT_DATA_HOME"/data_clinical.txt --output-directory="$MSK_MIXEDPACT_DATA_HOME"/case_lists --study-id=mixedpact
    
fi

# check that meta_clinical.txt and meta_SV.txt are actually empty files before deleting from IMPACT, HEME, and RAINDANCE studies
if [ $(wc -l < $MSK_IMPACT_DATA_HOME/meta_clinical.txt) -eq 0 ]; then
    rm $MSK_IMPACT_DATA_HOME/meta_clinical.txt
fi

if [ $(wc -l < $MSK_IMPACT_DATA_HOME/meta_SV.txt) -eq 0 ]; then
    rm $MSK_IMPACT_DATA_HOME/meta_SV.txt
fi

if [ $(wc -l < $MSK_HEMEPACT_DATA_HOME/meta_clinical.txt) -eq 0 ]; then
    rm $MSK_HEMEPACT_DATA_HOME/meta_clinical.txt
fi

if [ $(wc -l < $MSK_HEMEPACT_DATA_HOME/meta_SV.txt) -eq 0 ]; then
    rm $MSK_HEMEPACT_DATA_HOME/meta_SV.txt
fi

if [ $(wc -l < $MSK_RAINDANCE_DATA_HOME/meta_clinical.txt) -eq 0 ]; then
    rm $MSK_RAINDANCE_DATA_HOME/meta_clinical.txt
fi

# update MIXEDPACT in portal only if merge and case list updates were succesful
if [ $MERGE_FAIL -eq 0 ]; then
    echo "Importing MIXEDPACT study..."
	$JAVA_HOME/bin/java -Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=27182 -Xmx64g -ea -Dspring.profiles.active=dbcp -Djava.io.tmpdir="$tmp" -cp $PORTAL_HOME/lib/msk-dmp-importer.jar org.mskcc.cbio.importer.Admin --update-study-data --portal mixedpact-portal --notification-file "$mixedpact_notification_file" 
    if [ $? -gt 0 ]; then 
        echo "MIXEDPACT import failed!"
        MIXEDPACT_IMPORT_FAIL=1
    fi
else
    MIXEDPACT_IMPORT_FAIL=1
fi

# commit or revert changes for MIXEDPACT
if [ $MIXEDPACT_IMPORT_FAIL -gt 0 ]; then
    echo "MIXEDPACT merge and/or updates failed! Reverting data to last commit."
    cd $MSK_MIXEDPACT_DATA_HOME;$HG_BINARY revert --all --no-backup;
    rm $MSK_MIXEDPACT_DATA_HOME/*.orig
    rm $MSK_MIXEDPACT_DATA_HOME/case_lists/*.orig
else
    echo "Committing MIXEDPACT data"
    cd $MSK_MIXEDPACT_DATA_HOME;$HG_BINARY add;$HG_BINARY commit -m "Latest MIXEDPACT dataset"
fi
## END MSK-IMPACT, HEMEPACT, and RAINDANCE merge

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

EMAIL_BODY="The MSKIMPACT database version is incompatible. Imports will be skipped until database is updated."
# send email if db version isn't compatible
if [ $DB_VERSION_FAIL -gt 0 ]
then
    echo -e "Sending email $EMAIL_BODY"
    echo -e "$EMAIL_BODY" | mail -s "MSKIMPACT Update Failure: DB version is incompatible" $email_list
fi

EMAIL_BODY="The MSKIMPACT study failed fetch. The original study will remain on the portal."
# send email if fetch fails
if [ $IMPORT_FAIL -gt 0 ]
then
	echo -e "Sending email $EMAIL_BODY"
	echo -e "$EMAIL_BODY" | mail -s "MSKIMPACT Fetch Failure: Import" $email_list
fi

EMAIL_BODY="The MSKIMPACT study failed import. The original study will remain on the portal."
# send email if import fails
if [ $IMPORT_FAIL -gt 0 ]
then
	echo -e "Sending email $EMAIL_BODY"
	echo -e "$EMAIL_BODY" | mail -s "MSKIMPACT Update Failure: Import" $email_list
fi

EMAIL_BODY="The MSKIMPACT study failed to pass the validation step in import process. The original study will remain on the portal."
# send email if validation fails
if [ $VALIDATION_FAIL -gt 0 ]
then
	echo -e "Sending email $EMAIL_BODY"
	echo -e "$EMAIL_BODY" | mail -s "MSKIMPACT Update Failure: Validation" $email_list
fi

EMAIL_BODY="The yesterday_mskimpact study failed to delete. MSKIMPACT study did not finish updating."
if [ $DELETE_FAIL -gt 0 ]
then
	echo -e "Sending email $EMAIL_BODY"
	echo -e "$EMAIL_BODY" | mail -s "MSKIMPACT Update Failure: Deletion" $email_list
fi


EMAIL_BODY="Failed to backup mskimpact to yesterday_mskimpact via renaming. MSKIMPACT study did not finish updating."
if [ $RENAME_BACKUP_FAIL -gt 0 ]
then
	echo -e "Sending email $EMAIL_BODY"
	echo -e "$EMAIL_BODY" | mail -s "MSKIMPACT Update Failure: Renaming backup" $email_list
fi

EMAIL_BODY="Failed to rename temp study temporary_mskimpact to mskimpact. MSKIMPACT study did not finish updating."
if [ $RENAME_FAIL -gt 0 ]
then
	echo -e "Sending email $EMAIL_BODY"
	echo -e "$EMAIL_BODY" | mail -s "MSKIMPACT Update Failure: CRITICAL!! Renaming" $email_list
fi

EMAIL_BODY="Failed to update groups for backup study."
if [ $GROUPS_FAIL -gt 0 ]
then
	echo -e "Sending email $EMAIL_BODY"
	echo -e "$EMAIL_BODY" | mail -s "MSKIMPACT Update Failure: Groups update" $email_list
fi

EMAIL_BODY="Failed to merge MSK-IMPACT and HEMEPACT data. Merged study will not be updated."
if [ $MERGE_FAIL -gt 0 ]; then
    echo -e "Sending email $EMAIL_BODY"
    echo -e "$EMAIL_BODY" |  mail -s "MIXEDPACT Merge Failure: Study will not be updated." $email_list
fi

EMAIL_BODY="Failed to import MIXEDPACT study."
if [ $MIXEDPACT_IMPORT_FAIL -gt 0 ]; then
    echo -e "Sending email $EMAIL_BODY"
    echo -e "$EMAIL_BODY" | mail -s "MIXEDPACT Import Failure: Study will not be updated." $email_list
fi

$JAVA_HOME/bin/java -Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=27182 -Xmx16g -ea -Dspring.profiles.active=dbcp -Djava.io.tmpdir="$tmp" -cp $PORTAL_HOME/lib/msk-dmp-importer.jar org.mskcc.cbio.importer.Admin --send-update-notification --portal mskimpact-portal --notification-file "$mskimpact_notification_file"
#$JAVA_HOME/bin/java -Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=27182 -Xmx16g -ea -Dspring.profiles.active=dbcp -Djava.io.tmpdir="$tmp" -cp $PORTAL_HOME/lib/msk-dmp-importer.jar org.mskcc.cbio.importer.Admin --send-update-notification --portal mskraindance-portal --notification-file $mskraindance_notification_file
$JAVA_HOME/bin/java -Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=27182 -Xmx16g -ea -Dspring.profiles.active=dbcp -Djava.io.tmpdir="$tmp" -cp $PORTAL_HOME/lib/msk-dmp-importer.jar org.mskcc.cbio.importer.Admin --send-update-notification --portal mskheme-portal --notification-file $mskheme_notification_file
$JAVA_HOME/bin/java -Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=27182 -Xmx16g -ea -Dspring.profiles.active=dbcp -Djava.io.tmpdir="$tmp" -cp $PORTAL_HOME/lib/msk-dmp-importer.jar org.mskcc.cbio.importer.Admin --send-update-notification --portal mixedpact-portal --notification-file "$mixedpact_notification_file"

if [[ -d "$tmp" && "$tmp" != "/" ]]; then
	rm -rf "$tmp"/*
fi
