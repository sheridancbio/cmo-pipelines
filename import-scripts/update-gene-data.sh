#!/bin/bash

tmp=$PORTAL_HOME/tmp/update-gene-data
if [[ -d "$tmp" && "$tmp" != "/" ]]; then
	rm -rf "$tmp"/*
fi
email_list="heinsz@mskcc.org, sheridar@mskcc.org, grossb1@mskcc.org, ochoaa@mskcc.org, wilsonm2@mskcc.org"

echo "Updating portal configuration repository..."
cd $PORTAL_CONFIG_HOME; $HG_BINARY pull -u 

cd $tmp
echo "Downloading gene data resources from NCBI..."
wget ftp.ncbi.nih.gov:gene/DATA/GENE_INFO/Mammalia/Homo_sapiens.gene_info.gz
wget ftp.ncbi.nih.gov:/genomes/Homo_sapiens/GFF/ref_GRCh38.p7_top_level.gff3.gz
gunzip $tmp/Homo_sapiens.gene_info.gz
gunzip $tmp/ref_GRCh38.p7_top_level.gff3.gz
echo "Finished!"

sed -i 's/^#//' $tmp/Homo_sapiens.gene_info
if [[ !  -f $tmp/Homo_sapiens.gene_info || $(wc -l < $tmp/Homo_sapiens.gene_info) -eq 0 ]]; then
	echo "Error downloading Homo_sapiens.gene_info from NCBI. Exiting..."
	exit 1
fi

if [[ !  -f $tmp/ref_GRCh38.p7_top_level.gff3 || $(wc -l < $tmp/ref_GRCh38.p7_top_level.gff3) -eq 0 ]]; then
	echo "Error downloading ref_GRCh38.p7_top_level.gff3 from NCBI. Exiting..."
	exit 1
fi


function runGeneUpdatePipeline {
	DATABASE_NAME=$1
	if [ -z $DATABASE_NAME ]; then
		echo "Database name must be provided! Exiting..."
		exit 1
	fi
	export DATABASE_NAME=$DATABASE_NAME

	echo "Starting gene data update job on database: $DATABASE_NAME"
	$JAVA_HOME/bin/java -jar $PORTAL_HOME/lib/gene_data_updater.jar -d $tmp/Homo_sapiens.gene_info -l $tmp/ref_GRCh38.p7_top_level.gff3 -n $tmp/gene-update-notification.txt
	if [ $? -ne 0 ]; then 
		echo "Error updating gene data"
		echo -e "Error updating gene data." | mail -s "Gene Data Update Failure: $DATABASE_NAME" $email_list
	else
		echo "Success!"
		echo "Emailing results to email list..."
		if [[ -f $tmp/gene-update-notification.txt && $(wc -l < $tmp/gene-update-notification.txt) -gt 0 ]]; then
			cat $tmp/gene-update-notification.txt | mail -s "Gene Data Update Results: $DATABASE_NAME" $email_list
		else
			EMAIL_BODY="Error loading $tmp/gene-update-notification.txt"
			echo -e $EMAIL_BODY | mail -s "Gene Data Update Results: $DATABASE_NAME" $email_list
		fi
	fi
}

# run gene update pipeline for each database
export SPRING_CONFIG_LOCATION=$PORTAL_CONFIG_HOME/properties/update-gene/application.properties
runGeneUpdatePipeline "cgds_gdac"
runGeneUpdatePipeline "cgds_public"
runGeneUpdatePipeline "cgds_triage"
runGeneUpdatePipeline "cgds_genie"

export SPRING_CONFIG_LOCATION=$PORTAL_CONFIG_HOME/properties/update-gene/application-pancan.properties
runGeneUpdatePipeline "cgds_pancan"

echo "Restarting triage-tomcat server..."
/usr/bin/sudo /etc/init.d/triage-tomcat7 restart

echo "Restarting schultz-tomcat servers..."
ssh -i $HOME/.ssh/id_rsa_msk_tomcat_restarts_key cbioportal_importer@dashi.cbio.mskcc.org touch /srv/data/portal-cron/msk-tomcat-restart
ssh -i $HOME/.ssh/id_rsa_msk_tomcat_restarts_key cbioportal_importer@dashi2.cbio.mskcc.org touch /srv/data/portal-cron/msk-tomcat-restart

echo "Restarting public-tomcat servers..."
ssh -i $HOME/.ssh/id_rsa_public_tomcat_restarts_key cbioportal_importer@dashi.cbio.mskcc.org touch /srv/data/portal-cron/public-tomcat-restart
ssh -i $HOME/.ssh/id_rsa_public_tomcat_restarts_key cbioportal_importer@dashi2.cbio.mskcc.org touch /srv/data/portal-cron/public-tomcat-restart

echo "Restarting pancan-tomcat servers..."
ssh -i $HOME/.ssh/id_rsa_pancan_tomcat_restarts_key cbioportal_importer@dashi.cbio.mskcc.org touch /srv/data/portal-cron/pancan-tomcat-restart
ssh -i $HOME/.ssh/id_rsa_pancan_tomcat_restarts_key cbioportal_importer@dashi2.cbio.mskcc.org touch /srv/data/portal-cron/pancan-tomcat-restart

echo "Removing gene data files downloaded..."
rm $tmp/Homo_sapiens.gene_info
rm $tmp/ref_GRCh38.p7_top_level.gff3