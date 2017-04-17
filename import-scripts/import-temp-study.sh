#!/bin/bash

# temp sudy import arguments
# (1): cancer study id [ mskimpact | mskimpact_heme | mskraindance | mskarcher | mixedpact ]
# (2): temp study id [ temporary_mskimpact | temporary_mskimpact_heme | temporary_mskraindance | temporary_mskarcher | temporary_mixedpact ]
# (3): backup study id [ yesterday_mskimpact | yesterday_mskimpact_heme | yesterday_mskraindance | yesterday_mskarcher | yesterday_mixedpact ]
# (4): portal name [ mskimpact-portal | mskheme-portal | mskraindance-portal | mskarcher-portal | mixedpact-portal ]
# (5): study path [ $MSK_IMPACT_DATA_HOME | $MSK_HEMEPACT_DATA_HOME | $MSK_RAINDANCE_DATA_HOME | $MSK_ARCHER_DATA_HOME | $MSK_MIXEDPACT_DATA_HOME ]
# (6): notification file [ $mskimpact_notification_file | $mskheme_notification_file | $mskraindance_notification_file | $mixedpact_notification_file ]
# (7): tmp directory
# (8): email list
# (9): importer jar

# Non-zero exit code status indication
# There are several flags that are checked during the execution of the temporary study import. If any flags are non-zero at the end 
# of execution then an email is sent out to the email list provided for each non-zero flag and the script exits with a non-zero status.
# Flags:
# 	IMPORT_FAIL			if non-zero indicates that the study failed to import under a temporary id
# 	VALIDATION_FAIL		if non-zero indicates that the temporary study failed validation against the original study
# 	DELETE_FAIL			if non-zero indicates that the backup study failed to delete 
# 	RENAME_BACKUP_FAIL	if non-zero indicates that the original study failed to rename to the backup study id 
# 	RENAME_FAIL 		if non-zero indicates that the temporary study failed to rename to the original study id 

function usage {
	echo "import-temp-study.sh"
	echo -e "\t-i | --study-id              cancer study identifier"
	echo -e "\t-t | --temp-study-id         temp study identifier"
	echo -e "\t-b | --backup-study-id       backup study identifier"
	echo -e "\t-p | --portal-name           portal name"
	echo -e "\t-s | --study-path            study path"
	echo -e "\t-n | --notification-file     notification file"
	echo -e "\t-d | --tmp-directory         tmp directory"
	echo -e "\t-e | --email-list            email list"
	echo -e "\t-j | --importer-jar          importer jar"
}

for i in "$@"; do
case $i in
    -i=*|--study-id=*)
    CANCER_STUDY_IDENTIFIER="${i#*=}"
    echo "study id=$CANCER_STUDY_IDENTIFIER"
    shift
    ;;
    -t=*|--temp-study-id=*)
    TEMP_CANCER_STUDY_IDENTIFIER="${i#*=}"
    echo "temp id=$TEMP_CANCER_STUDY_IDENTIFIER"
    shift
    ;;
    -b=*|--backup-study-id=*)
    BACKUP_CANCER_STUDY_IDENTIFIER="${i#*=}"
    echo "backup id=$BACKUP_CANCER_STUDY_IDENTIFIER"
    shift
    ;;
    -p=*|--portal-name=*)
    PORTAL_NAME="${i#*=}"
    echo "portal name=$PORTAL_NAME"
    shift
    ;;
    -s=*|--study-path=*)
    STUDY_PATH="${i#*=}"
    echo "study path=$STUDY_PATH"
    shift
    ;;
    -n=*|--notification-file=*)
    NOTIFICATION_FILE="${i#*=}"
    echo "notifcation file=$NOTIFICATION_FILE"
    shift
    ;;
    -d=*|--tmp-directory=*)
    TMP_DIRECTORY="${i#*=}"
    echo "tmp dir=$TMP_DIRECTORY"
    shift
    ;;
    -e=*|--email-list=*)
    EMAIL_LIST="${i#*=}"
    echo "email list=$EMAIL_LIST"
    shift
    ;;
    -j=*|--importer-jar=*)
    IMPORTER_JAR="${i#*=}"
    echo "importer jar=$IMPORTER_JAR"
    shift
    ;;
    *)
    ;;
esac
done

if [[ -z $CANCER_STUDY_IDENTIFIER || -z $TEMP_CANCER_STUDY_IDENTIFIER || -z $BACKUP_CANCER_STUDY_IDENTIFIER || -z $PORTAL_NAME || -z $STUDY_PATH || -z $NOTIFICATION_FILE || -z $TMP_DIRECTORY || -z $EMAIL_LIST || -z $IMPORTER_JAR ]]; then
	usage
	exit 1
fi

# variables for import temp study status
IMPORT_FAIL=0
VALIDATION_FAIL=0
DELETE_FAIL=0
RENAME_BACKUP_FAIL=0
RENAME_FAIL=0
GROUPS_FAIL=0

# import study using temp id
echo "Importing study '$CANCER_STUDY_IDENTIFIER' as temporary study '$TEMP_CANCER_STUDY_IDENTIFIER'"
$JAVA_HOME/bin/java -Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=27182 -Xmx64g -ea -Dspring.profiles.active=dbcp -Djava.io.tmpdir="$TMP_DIRECTORY" -cp $IMPORTER_JAR org.mskcc.cbio.importer.Admin --update-study-data --portal "$PORTAL_NAME" --notification-file "$NOTIFICATION_FILE" --temporary-id "$TEMP_CANCER_STUDY_IDENTIFIER"

# check number of studies updated before continuing
if [ -f "$TMP_DIRECTORY/num_studies_updated.txt" ]; then 
	num_studies_updated=`cat $TMP_DIRECTORY/num_studies_updated.txt`
else
	num_studies_updated=0
fi

if [ "$num_studies_updated" -ne 1 ]; then
	echo "Failed to import study '$CANCER_STUDY_IDENTIFIER'"
	IMPORT_FAIL=1
else
	# validate
	echo "Validating import..."
	$JAVA_HOME/bin/java -Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=27182 -Xmx64g -ea -Dspring.profiles.active=dbcp -Djava.io.tmpdir="$TMP_DIRECTORY" -cp $IMPORTER_JAR org.mskcc.cbio.importer.Admin --validate-temp-study --temp-study-id $TEMP_CANCER_STUDY_IDENTIFIER --original-study-id $CANCER_STUDY_IDENTIFIER
	if [ $? -gt 0 ]; then
		echo "Failed to validate - deleting temp study '$TEMP_CANCER_STUDY_IDENTIFIER'"
		$JAVA_HOME/bin/java -Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=27182 -Xmx64g -ea -Dspring.profiles.active=dbcp -Djava.io.tmpdir="$TMP_DIRECTORY" -cp $IMPORTER_JAR org.mskcc.cbio.importer.Admin --delete-cancer-study --cancer-study-ids $TEMP_CANCER_STUDY_IDENTIFIER
		VALIDATION_FAIL=1
	else
		echo "Successful validation - renaming '$CANCER_STUDY_IDENTIFIER' and temp study '$TEMP_CANCER_STUDY_IDENTIFIER'"
		$JAVA_HOME/bin/java -Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=27182 -Xmx64g -ea -Dspring.profiles.active=dbcp -Djava.io.tmpdir="$TMP_DIRECTORY" -cp $IMPORTER_JAR org.mskcc.cbio.importer.Admin --delete-cancer-study --cancer-study-ids $BACKUP_CANCER_STUDY_IDENTIFIER
		if [ $? -gt 0 ]; then
			echo "Failed to delete backup study '$BACKUP_CANCER_STUDY_IDENTIFIER'!"
			DELETE_FAIL=1
		else
			echo "Renaming '$CANCER_STUDY_IDENTIFIER' to '$BACKUP_CANCER_STUDY_IDENTIFIER'"
			$JAVA_HOME/bin/java -Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=27182 -Xmx64g -ea -Dspring.profiles.active=dbcp -Djava.io.tmpdir="$TMP_DIRECTORY" -cp $IMPORTER_JAR org.mskcc.cbio.importer.Admin --rename-cancer-study --new-study-id $BACKUP_CANCER_STUDY_IDENTIFIER --original-study-id $CANCER_STUDY_IDENTIFIER
			if [ $? -gt 0 ]; then
				echo "Failed to rename existing '$CANCER_STUDY_IDENTIFIER' to backup study '$BACKUP_CANCER_STUDY_IDENTIFIER'!"
				RENAME_BACKUP_FAIL=1
			else
				echo "Updating groups of study '$BACKUP_CANCER_STUDY_IDENTIFIER' to KSBACKUP"
				$JAVA_HOME/bin/java -Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=27182 -Xmx64g -ea -Dspring.profiles.active=dbcp -Djava.io.tmpdir="$TMP_DIRECTORY" -cp $IMPORTER_JAR org.mskcc.cbio.importer.Admin --update-groups --cancer-study-ids $BACKUP_CANCER_STUDY_IDENTIFIER --groups "KSBACKUP"
				if [ $? -gt 0 ]; then
					echo "Failed to change groups for backup study '$BACKUP_CANCER_STUDY_IDENTIFIER!"
					GROUPS_FAIL=1
				fi
				echo "Renaming temporary study '$TEMP_CANCER_STUDY_IDENTIFIER' to '$CANCER_STUDY_IDENTIFIER'"
				$JAVA_HOME/bin/java -Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=27182 -Xmx64g -ea -Dspring.profiles.active=dbcp -Djava.io.tmpdir="$TMP_DIRECTORY" -cp $IMPORTER_JAR org.mskcc.cbio.importer.Admin --rename-cancer-study --new-study-id $CANCER_STUDY_IDENTIFIER --original-study-id $TEMP_CANCER_STUDY_IDENTIFIER
				if [ $? -gt 0 ]; then
					echo "Failed to rename temporary study '$TEMP_CANCER_STUDY_IDENTIFIER' to '$CANCER_STUDY_IDENTIFIER!"
					RENAME_FAIL=1
				else
					# only consume samples if study is mskimpact
					if [ $CANCER_STUDY_IDENTIFIER == "mskimpact" ]; then 
						echo "Consuming mskimpact samples from cvr"
						$JAVA_HOME/bin/java -Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=27182 -jar $PORTAL_HOME/lib/cvr_fetcher.jar -c $MSK_IMPACT_DATA_HOME/cvr_data.json
					fi
				fi
			fi
		fi
	fi
fi

### FAILURE EMAIL ###

EMAIL_BODY="The $CANCER_STUDY_IDENTIFIER study failed import. The original study will remain on the portal."
# send email if import fails
if [ $IMPORT_FAIL -gt 0 ]
then
	echo -e "Sending email $EMAIL_BODY"
	echo -e "$EMAIL_BODY" | mail -s "$CANCER_STUDY_IDENTIFIER Update Failure: Import" $EMAIL_LIST
fi

EMAIL_BODY="The $CANCER_STUDY_IDENTIFIER study failed to pass the validation step in import process. The original study will remain on the portal."
# send email if validation fails
if [ $VALIDATION_FAIL -gt 0 ]
then
	echo -e "Sending email $EMAIL_BODY"
	echo -e "$EMAIL_BODY" | mail -s "$CANCER_STUDY_IDENTIFIER Update Failure: Validation" $EMAIL_LIST
fi

EMAIL_BODY="The $BACKUP_CANCER_STUDY_IDENTIFIER study failed to delete. $CANCER_STUDY_IDENTIFIER study did not finish updating."
if [ $DELETE_FAIL -gt 0 ]
then
	echo -e "Sending email $EMAIL_BODY"
	echo -e "$EMAIL_BODY" | mail -s "$CANCER_STUDY_IDENTIFIER Update Failure: Deletion" $EMAIL_LIST
fi

EMAIL_BODY="Failed to backup $CANCER_STUDY_IDENTIFIER to $BACKUP_CANCER_STUDY_IDENTIFIER via renaming. $CANCER_STUDY_IDENTIFIER study did not finish updating."
if [ $RENAME_BACKUP_FAIL -gt 0 ]
then
	echo -e "Sending email $EMAIL_BODY"
	echo -e "$EMAIL_BODY" | mail -s "$CANCER_STUDY_IDENTIFIER Update Failure: Renaming backup" $EMAIL_LIST
fi

EMAIL_BODY="Failed to rename temp study $TEMP_CANCER_STUDY_IDENTIFIER to $CANCER_STUDY_IDENTIFIER. $CANCER_STUDY_IDENTIFIER study did not finish updating."
if [ $RENAME_FAIL -gt 0 ]
then
	echo -e "Sending email $EMAIL_BODY"
	echo -e "$EMAIL_BODY" | mail -s "$CANCER_STUDY_IDENTIFIER Update Failure: CRITICAL!! Renaming" $EMAIL_LIST
fi

EMAIL_BODY="Failed to update groups for backup study $BACKUP_CANCER_STUDY_IDENTIFIER."
if [ $GROUPS_FAIL -gt 0 ]
then
	echo -e "Sending email $EMAIL_BODY"
	echo -e "$EMAIL_BODY" | mail -s "$CANCER_STUDY_IDENTIFIER Update Failure: Groups update" $EMAIL_LIST
fi


# determine if we need to exit with error code
if [[ $IMPORT_FAIL -ne 0 || $VALIDATION_FAIL -ne 0 || $DELETE_FAIL -ne 0 || $RENAME_BACKUP_FAIL -ne 0 || $RENAME_FAIL -ne 0 || $GROUPS_FAIL -ne 0 ]]; then
	echo "Update failed for study '$CANCER_STUDY_IDENTIFIER"
	exit 1
else
	echo "Update successful for study '$CANCER_STUDY_IDENTIFIER'"
fi