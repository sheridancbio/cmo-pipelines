#!/bin/bash

LOGFILENAME="$PORTAL_HOME/logs/import-user-dashi-genie.log"

echo "### Starting import" >> "$LOGFILENAME"
date >> "$LOGFILENAME"
$PYTHON_BINARY $PORTAL_HOME/scripts/importUsers.py --port 3306 --secrets-file $PORTAL_DATA_HOME/portal-configuration/google-docs/client_secrets.json --creds-file $PORTAL_DATA_HOME/portal-configuration/google-docs/creds.dat --properties-file $PORTAL_HOME/cbio-portal-data/portal-configuration/properties/import-users/portal.properties.dashi.genie --send-email-confirm true --sender GENIE >> "$LOGFILENAME" 2>&1
