#!/bin/bash

USERSGENIELOGFILENAME="$PORTAL_HOME/logs/import-user-dashi-genie.log"
USERSDASHILOGFILENAME="$PORTAL_HOME/logs/import-user-dashi-gdac.log"
USERSDASHI2LOGFILENAME="$PORTAL_HOME/logs/import-user-dashi2.log"

echo "### Starting import" >> "$USERSDASHILOGFILENAME"
date >> "$USERSDASHILOGFILENAME"
$PYTHON_BINARY $PORTAL_HOME/scripts/importUsers.py --port 3306 --secrets-file $PORTAL_DATA_HOME/portal-configuration/google-docs/client_secrets.json --creds-file $PORTAL_DATA_HOME/portal-configuration/google-docs/creds.dat --properties-file $PORTAL_HOME/cbio-portal-data/portal-configuration/properties/import-users/portal.properties.dashi.gdac --send-email-confirm true >> "$USERSDASHILOGFILENAME" 2>&1

echo "### Starting import" >> "$USERSDASHI2LOGFILENAME"
date >> "$USERSDASHI2LOGFILENAME"
$PYTHON_BINARY $PORTAL_HOME/scripts/importUsers.py --port 3306 --secrets-file $PORTAL_DATA_HOME/portal-configuration/google-docs/client_secrets.json --creds-file $PORTAL_DATA_HOME/portal-configuration/google-docs/creds.dat --properties-file $PORTAL_HOME/cbio-portal-data/portal-configuration/properties/import-users/portal.properties.dashi2 --send-email-confirm true >> "$USERSDASHI2LOGFILENAME" 2>&1

echo "### Starting import" >> "$USERSGENIELOGFILENAME"
date >> "$USERSGENIELOGFILENAME"
$PYTHON_BINARY $PORTAL_HOME/scripts/importUsers.py --port 3306 --secrets-file $PORTAL_DATA_HOME/portal-configuration/google-docs/client_secrets.json --creds-file $PORTAL_DATA_HOME/portal-configuration/google-docs/creds.dat --properties-file $PORTAL_HOME/cbio-portal-data/portal-configuration/properties/import-users/portal.properties.dashi.genie --send-email-confirm true --sender GENIE >> "$USERSGENIELOGFILENAME" 2>&1

exit 0
