#! /usr/bin/env python
# ------------------------------------------------------------------------------
# Utility script which takes a study directory 
# Resolves any cases where study directory contains both legacy and current clinical file formats (mixed, seperate)
# data_clinical.txt (mixed) is parsed and added to data_clinical_sample.txt/data_clinical_patient.txt based on CDD attribute type
# ------------------------------------------------------------------------------

import argparse
import json
import os
import requests
import shutil
import sys
import tempfile
from clinicalfile_utils import *

CDD_BASE_URL = "http://oncotree.mskcc.org/cdd/api/"
ATTRIBUTE_TYPE_FIELD = "attribute_type"
COLUMN_HEADER_FIELD = "column_header" 
DATATYPE_FIELD = "datatype"
DESCRIPTION_FIELD = "description"
DISPLAY_NAME_FIELD = "display_name"
PRIORITY_FIELD = "priority"

PATIENT_ID_FIELD = "PATIENT_ID"
SAMPLE_ID_FIELD = "SAMPLE_ID"
METADATA_HEADER_PREFIX = "#"

# return a dictionary of attributes in list to their attribute type {SAMPLE_ID:"SAMPLE", ...}
def get_header_to_attribute_type_map(study_id, header, base_cdd_url):
    metadata_mapping = {}
    response = requests.post(base_cdd_url + "?cancerStudy=" + study_id if study_id else base_cdd_url, json=list(header))
    check_response_returned(response)
    response_as_json = json.loads(response.text)
    column_headers = [attribute[COLUMN_HEADER_FIELD] for attribute in response_as_json]
    attribute_types = [attribute[ATTRIBUTE_TYPE_FIELD] for attribute in response_as_json]
    return dict(zip(column_headers, attribute_types))

# return a dictionary of attributes in list to all their metadata
def get_attribute_to_metadata_map(study_id, header, base_cdd_url):
    metadata_mapping = {}
    response = requests.post(base_cdd_url + "?cancerStudy=" + study_id if study_id else base_cdd_url, json=list(header))
    check_response_returned(response)
    response_as_json = json.loads(response.text)
    for attribute in response_as_json:
        metadata_mapping[attribute[COLUMN_HEADER_FIELD]] = attribute
    return metadata_mapping 

# check whether provided study id has overrides available/is valid
def has_overrides(study_id, base_cdd_url):
    response = requests.get(base_cdd_url + "cancerStudies")
    check_response_returned(response)
    response_as_json = json.loads(response.text)
    return study_id in [cancer_study["name"] for cancer_study in response_as_json]

def check_response_returned(response):
    if response.status_code != 200:
        print >> sys.stderr, "Error code returned from CDD, aborting..."
        sys.exit(2)

# returns a map (sample/patient id: {attr1:'value1', attr2:'value2',...})
# patient/sample id key is determined based on what type of clinical file is passed in
# data_clinical.txt (mixed) will use sample ID
def load_clinical_file_to_map(clinical_file):
    if not os.path.isfile(clinical_file):
        return [], {}
    header = get_header(clinical_file)
    headers_processed = False
    clinical_file_record_map = {}
    with open(clinical_file) as data_file:
        for line in data_file:
            if line.startswith(METADATA_HEADER_PREFIX) or len(line.rstrip()) == 0:
                continue
            if not headers_processed:
                headers_processed = True
                continue
            data = dict(zip(header, map(str.strip, line.split('\t'))))
            try:
                if data[SAMPLE_ID_FIELD] in clinical_file_record_map:
                    print >> sys.stderr, "Malformatted file: duplicate sample (%s) found in %s, exiting..." % (data[SAMPLE_ID_FIELD], clinical_file)
                    sys.exit(2)
                clinical_file_record_map[data[SAMPLE_ID_FIELD]] = data
            except KeyError as e:
                clinical_file_record_map[data[PATIENT_ID_FIELD]] = data
            # TODO: add index - preserve order of file?
    return header, clinical_file_record_map

def load_mixed_clinical_file_to_map(mixed_clinical_file, sample_header, clinical_sample_map, patient_header, clinical_patient_map, study_id, base_cdd_url):
    header, clinical_file_record_map = load_clinical_file_to_map(mixed_clinical_file)
    header_to_attribute_type_map = get_header_to_attribute_type_map(study_id, header, base_cdd_url)
    
    # get two seperate list - one of sample attributes in data_clinical.txt and one with patient attributes in data_clinical.txt
    sample_attributes = [sample_attribute for sample_attribute in header if header_to_attribute_type_map[sample_attribute] == "SAMPLE" or sample_attribute == "PATIENT_ID"]
    patient_attributes = [patient_attribute for patient_attribute in header if header_to_attribute_type_map[patient_attribute] == "PATIENT"]
 
    # extend header with any new attributes being pulled in from data_clinical.txt
    sample_header.extend([sample_attribute for sample_attribute in sample_attributes if sample_attribute not in sample_header])
    patient_header.extend([patient_attribute for patient_attribute in patient_attributes if patient_attribute not in patient_header]) 
    # iterate through every record in data_clinical.txt -
    # new samples/patients automatically added
    # for existing samples, extend existing record with new attributes - old attributes ignored
    # TODO: log cases where attributes are overwritten or switched
    for sample_id, record in clinical_file_record_map.items():
        if sample_id not in clinical_sample_map:
            clinical_sample_map[sample_id] = dict(zip(sample_attributes, [record[sample_attribute] for sample_attribute in sample_attributes]))
        else:
            for sample_attribute in sample_attributes:
                if sample_attribute not in clinical_sample_map[sample_id]:
                    clinical_sample_map[sample_id][sample_attribute] = record[sample_attribute]
        patient_id = record[PATIENT_ID_FIELD]    
        if patient_id not in clinical_patient_map:
            clinical_patient_map[patient_id] = dict(zip(patient_attributes, [record[patient_attribute] for patient_attribute in patient_attributes]))
        else:
            for patient_attribute in patient_attributes:
                if patient_attribute not in clinical_patient_map[patient_id]:
                    clinical_patient_map[patient_id][patient_attribute] = record[patient_attribute]
                elif is_null_type(clinical_patient_map[patient_id][patient_attribute]) and not is_null_type(record[patient_attribute]):
                    clinical_patient_map[patient_id][patient_attribute] = record[patient_attribute]
                else:
                    continue

def is_null_type(attribute_value):
    return (not attribute_value or attribute_value == "NA")

def overwrite_clinical_file(header, clinical_record_map, clinical_file, study_id, base_cdd_url):
    attribute_to_metadata_map = get_attribute_to_metadata_map(study_id, header, base_cdd_url)
    temp_file, temp_file_name = tempfile.mkstemp()
    os.close(temp_file)
    with open(temp_file_name, "w") as f:
        f.write('#' + '\t'.join(map(lambda x: attribute_to_metadata_map.get(x)[DISPLAY_NAME_FIELD], header)) + '\n')
        f.write('#' + '\t'.join(map(lambda x: attribute_to_metadata_map.get(x)[DESCRIPTION_FIELD], header)) + '\n')
        f.write('#' + '\t'.join(map(lambda x: attribute_to_metadata_map.get(x)[DATATYPE_FIELD], header)) + '\n')
        f.write('#' + '\t'.join(map(lambda x: attribute_to_metadata_map.get(x)[PRIORITY_FIELD], header)) + '\n')
        f.write('\t'.join(header) + '\n')
        for record in clinical_record_map.values():
            formatted_data = map(lambda x: record.get(x,''), header)
            f.write('\t'.join(formatted_data) + '\n')
    shutil.move(temp_file_name, clinical_file)

def main():
    parser = argparse.ArgumentParser()
    parser.add_argument("-d", "--directory", help = "file(s) to add metadata headers", required = True)
    parser.add_argument("-s", "--study-id", help = "study id for specific overrides", required = False)
    parser.add_argument("-c", "--cdd-url", help = "the url for the cdd web application, default is http://oncotree.mskcc.org/cdd/api/", required = False)
    args = parser.parse_args()

    cdd_url = args.cdd_url if args.cdd_url else CDD_BASE_URL
    directory = args.directory
    study_id = args.study_id if args.study_id and has_overrides(args.study_id, cdd_url) else ""

    clinical_sample_file = os.path.join(directory, "data_clinical_sample.txt")
    clinical_patient_file = os.path.join(directory, "data_clinical_patient.txt")
    legacy_clinical_file = os.path.join(directory, "data_clinical.txt")

    # only run the script if directory has both legacy and current clinical file versions
    if not (os.path.isfile(legacy_clinical_file) and (os.path.isfile(clinical_sample_file) or os.path.isfile(clinical_patient_file))): 
        print >> sys.stderr, "Directory (" + directory + ") does not have a mix of clinical file formats. Exiting..."
        sys.exit(1)

    sample_header, clinical_sample_map = load_clinical_file_to_map(clinical_sample_file) 
    patient_header, clinical_patient_map = load_clinical_file_to_map(clinical_patient_file)
    load_mixed_clinical_file_to_map(legacy_clinical_file, sample_header, clinical_sample_map, patient_header, clinical_patient_map, study_id, cdd_url)
    overwrite_clinical_file(sample_header, clinical_sample_map, clinical_sample_file, study_id, cdd_url)
    overwrite_clinical_file(patient_header, clinical_patient_map, clinical_patient_file, study_id, cdd_url)
    os.remove(legacy_clinical_file)

if __name__ == '__main__':
    main()
