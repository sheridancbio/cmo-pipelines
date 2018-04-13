# Queries oncotree_latest_stable and then
# for each oncotree code, queries crosswalk for the NCI and UMLS codes.
# If any of the following are true, the oncotree code is added to the
# list of missing codes:
#   1. crosswalk returns a 404
#   2. there are no nci codes
#   3. there are no umls codes
#
# Generates a report of missing codes.
#
# Author: Manda Wilson
# Date: August 2, 2017

import sys
import requests
import json
import time
from collections import defaultdict

oncotree_versions = ("oncotree_candidate_release", "oncotree_latest_stable")
oncotree_base_uri = "http://oncotree.mskcc.org/api/tumorTypes?version=%s"
crosswalk_base_uri = "http://data.mskcc.org/ontologies/api/concept/crosswalk?vocabularyId=ONCOTREE&conceptId=%s"

oncotree_codes = set([])
oncotree_version_codes = defaultdict(set)

HEADER = ("ONCOTREE_CODE", "ONCOTREE_VERSIONS", "CROSSWALK_URL", "RESPONSE_CODE", "NCI_CODES", "UMLS_CODES")
print "\t".join(HEADER)

# get all oncotree codes
for oncotree_version in oncotree_versions:
    oncotree_uri = oncotree_base_uri % (oncotree_version)
    response = requests.get(oncotree_uri)
    if response.status_code != 200:
        print >> sys.stderr, "ERROR: %d status code getting oncotree codes with URI '%s'" % (response.status_code, oncotree_uri)
        sys.exit(1)
    
    if response.json():
        for oncotree_item in response.json():
            crosswalk_uri = "" 
            crosswalk_response_code = None 
            oncotree_code = oncotree_item["code"]
            oncotree_codes.add(oncotree_code)
            oncotree_version_codes[oncotree_version].add(oncotree_code)
    else:
        print >> sys.stderr, "ERROR: no oncotree codes found in JSON response to '%s'" % (oncotree_uri)

for oncotree_code in sorted(oncotree_codes):
    nci_codes = []
    umls_codes = []
    # query crosswalk
    crosswalk_uri = crosswalk_base_uri % (oncotree_code)
    response = requests.get(crosswalk_uri)
    crosswalk_response_code = response.status_code
    if crosswalk_response_code == 200 and "crosswalks" in response.json() and "NCI" in response.json()["crosswalks"]:
        for nci_code in response.json()["crosswalks"]["NCI"]:
            # found
            nci_codes.append(nci_code)

    if crosswalk_response_code == 200 and "conceptId" in response.json():
        for umls_code in response.json()["conceptId"]:
            # found
            umls_codes.append(umls_code)

    if len(nci_codes) > 0 and len(umls_codes) > 0:
        continue
  
    in_versions = [ oncotree_version for oncotree_version, oncotree_codes in oncotree_version_codes.iteritems() if oncotree_code in oncotree_codes]
 
    values = [oncotree_code, ",".join(in_versions), crosswalk_uri, str(crosswalk_response_code), ",".join(nci_codes), ",".join(umls_codes)]
    print "\t".join(values)
