#!/bin/bash
### ./import-hgnc.sh

# set umask so that all files which are created in the shared directories (e.g. TMP_DIR) are world writable / deletable
umask 0000

### Required environmental variables
PORTAL_HOME=/data/portal-cron
PORTAL_DATA_HOME=$PORTAL_HOME/cbio-portal-data
IMPORTER_JAR_FILENAME=$PORTAL_HOME/lib/cbioportal-importer.jar
LOG_FILENAME=$PORTAL_HOME/logs/hgnc-importer.log
TMP_DIR=$PORTAL_HOME/tmp/hgnc_import_tmp
AWS_SSL_TRUSTSTORE=$PORTAL_HOME/pipelines-credentials/AwsSsl.truststore
AWS_SSL_TRUSTSTORE_PASSWORD_FILE=$PORTAL_HOME/pipelines-credentials/AwsSsl.truststore.password
JAVA_HOME=/usr/lib/jvm/java-1.8.0-openjdk.x86_64
JAVA_BINARY=$JAVA_HOME/bin/java
JAVA_PROXY_ARGS="-Dhttp.proxyHost=jxi2.mskcc.org -Dhttp.proxyPort=8080 -Dhttp.nonProxyHosts=draco.mskcc.org|pidvudb1.mskcc.org|phcrdbd2.mskcc.org|dashi-dev.cbio.mskcc.org|pipelines.cbioportal.mskcc.org|localhost"
JAVA_SSL_ARGS="-Djavax.net.ssl.trustStore=$AWS_SSL_TRUSTSTORE -Djavax.net.ssl.trustStorePassword=`cat $AWS_SSL_TRUSTSTORE_PASSWORD_FILE`"
JAVA_IMPORTER_ARGS="$JAVA_PROXY_ARGS $JAVA_SSL_ARGS -Dspring.profiles.active=dbcp -Djava.io.tmpdir=$TMP_DIR -ea -cp $IMPORTER_JAR_FILENAME org.mskcc.cbio.importer.Admin"

if [ -z $PORTAL_DATA_HOME ] ; then
    echo "\$PORTAL_DATA_HOME is not set, will not be able to find data to import, exiting..."
    exit 1
fi 

if ! [ -d "$TMP_DIR" ] ; then
    if ! mkdir -p "$TMP_DIR" ; then
        echo "Error : could not create TMP_DIR directory '$TMP_DIR'" >&2
        exit 1
    fi
fi
if [[ -d "$TMP_DIR" && "$TMP_DIR" != "/" ]]; then
    rm -rf "$TMP_DIR"/*
fi

NOW=$(date "+%Y-%m-%d-%H-%M-%S")
NOTIFICATION_FILENAME=$(mktemp $TMP_DIR/hgnc-portal-update-notification.$NOW.XXXXXX)

if ! [ -z ${HGNC_IMPORT_FETCH_DATAHUB_REPOSITORY} ] ; then
    echo "Fetching datahub"
    $JAVA_BINARY $JAVA_IMPORTER_ARGS --fetch-data --data-source datahub --run-date latest
    FETCH_STATUS="$?"
    # chmod in order to allow others to update the fetched repositories after us
    chmod -R --quiet a+w "$PORTAL_DATA_HOME"
    if [ "$FETCH_STATUS" != "0" ] ; then
        "Unable to fetch data updates, exiting without import. Check '$LOG_FILENAME' for details"
        exit 1
    fi
fi

echo  "Starting import, looking for data inside $PORTAL_DATA_HOME..."
$JAVA_BINARY -Xmx32G $JAVA_IMPORTER_ARGS --update-study-data --portal hgnc-portal --notification-file "$NOTIFICATION_FILENAME" --oncotree-version oncotree_latest_stable --transcript-overrides-source uniprot
if [ $? -ne 0 ] ; then
    echo "Import failed, please check '$LOG_FILENAME' for more details"
    exit 1
fi

echo "Sending notification email. Please check it to confirm the study was successfully updated."
$JAVA_BINARY $JAVA_IMPORTER_ARGS --send-update-notification --portal hgnc-portal --notification-file "$NOTIFICATION_FILENAME"
