#!/bin/bash

LDAPLOGFILENAME="$PORTAL_HOME/logs/purge-deactivated-ldap-users.log"
LDAPTMPDIRECTORY="$PORTAL_HOME/tmp/ldap"

echo "### Removing deactivated LDAP users from Google spreadsheet and database" >> "$LDAPLOGFILENAME"
date >> "$LDAPLOGFILENAME"
$PYTHON_BINARY $PORTAL_HOME/scripts/purge_deactivated_ldap_users.py --secrets-file $PORTAL_DATA_HOME/portal-configuration/google-docs/client_secrets.json --creds-file $PORTAL_DATA_HOME/portal-configuration/google-docs/creds.dat  --properties-file $PORTAL_HOME/cbio-portal-data/portal-configuration/properties/import-users/portal.properties.dashi.gdac.ldap --tmp-directory $LDAPTMPDIRECTORY >> "$LDAPLOGFILENAME" 2>&1

exit 0
