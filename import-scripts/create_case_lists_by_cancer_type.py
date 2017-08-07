#! /usr/bin/env python

# author: Zack Heins

# ---------------------------------------------------------------
#  Script to create case lists per cancer type
# ---------------------------------------------------------------

# ---------------------------------------------------------------
# imports
import os
import sys
import getopt
import csv

# ---------------------------------------------------------------
# globals
ERROR_FILE = sys.stderr
OUTPUT_FILE = sys.stdout

# ---------------------------------------------------------------
# functions

# ---------------------------------------------------------------
# creates the case list dictionary
# key = cancer_type
# value = list of sids
def create_case_lists_map(clinical_file_name, attribute):
    clinical_file = open(clinical_file_name,'rU')
    clinical_file_map = {}
    reader = csv.DictReader(clinical_file,dialect='excel-tab')
    for row in reader:
        if row[attribute] not in clinical_file_map:
            clinical_file_map[row[attribute]] = [row['SAMPLE_ID']]
        else:
            clinical_file_map[row[attribute]].append(row['SAMPLE_ID'])

    clinical_file.close()

    return clinical_file_map

# ---------------------------------------------------------------
# writes the file to case_lists directory inside the directory
def write_case_list_files(clinical_file_map, output_directory, study_id, name, description, value_filter, filename = None):
    for attribute_value,ids in clinical_file_map.iteritems():
        if value_filter is None or value_filter == attribute_value:
            if filename is None:
                attribute_value_no_spaces = attribute_value.replace(' ','_').replace(',','').replace('/','')
            else:
                attribute_value_no_spaces = filename
                attribute_value = filename
            case_list_file = open(os.path.abspath(output_directory + '/' + 'case_list_' + attribute_value_no_spaces + '.txt'),'w')
            stable_id = study_id + '_' + attribute_value_no_spaces
            case_list_name = name + attribute_value
            case_list_description = description + attribute_value
            case_list_ids = '\t'.join(list(set(ids)))
            case_list_file.write('cancer_study_identifier: ' + study_id + '\n' +
                                    'stable_id: ' + stable_id + '\n' + 
                                    'case_list_name: ' + case_list_name + '\n' + 
                                    'case_list_description: ' + case_list_description + '\n' +
                                    'case_list_ids: ' + case_list_ids)        

# ---------------------------------------------------------------
# gets clin file and processes it 
def create_case_lists(clinical_file_name, output_directory, study_id, attribute):
    case_lists_map = create_case_lists_map(clinical_file_name, attribute)
    if attribute == 'CANCER_TYPE':
        # We do not want to filter off the value of the attribute in the case of case lists by cancer type - we want them all, so pass None as the filter
        write_case_list_files(case_lists_map, output_directory, study_id, 'Tumor Type: ', 'All tumors with cancer type ', None)
    else:
        # If we want germline case list, the value of the column '12_245_PART_C_CONSENTED' will be YES or NO. Samples with YES for that value are the ones to add to the list
        write_case_list_files(case_lists_map, output_directory, study_id, 'Tumors with germline data 12_245_PARTC_CONSENTED ', 'Tumors with germline data available 12_245_PARTC_CONSENTED ', 'YES', 'germline')

# ---------------------------------------------------------------
# displays usage of program
def usage():
    print >> OUTPUT_FILE, 'create_case_lists_by_cancer_type.py --clinical-file <path/to/clinical/file> --output-directory <path/to/output/directory> --study-id <cancer_study_identifier> --attribute <clinical column to base case list categories on>'

# ---------------------------------------------------------------
# the main
def main():
    # parse command line
    try:
        opts,args = getopt.getopt(sys.argv[1:],'',['clinical-file=', 'output-directory=', 'study-id=', 'attribute='])
    except getopt.error,msg:
        print >> ERROR_FILE,msg
        usage()
        sys.exit(2)

    clinical_file_name = ''
    output_directory = ''
    study_id = ''
    attribute = ''

    # process options
    for o, a in opts:
        if o == '--clinical-file':
            clinical_file_name = a
        elif o == '--output-directory':
            output_directory = a
        elif o == '--study-id':
            study_id = a
        elif o == '--attribute':
            attribute = a
    
    if clinical_file_name == '' or output_directory == '' or study_id == '':
        usage()
        sys.exit(2)

    # check existence of file
    if not os.path.exists(os.path.abspath(clinical_file_name)):
        print >> ERROR_FILE, 'clinical file cannot be found: ' + clinical_file_name
        sys.exit(2)
    if not os.path.isdir(os.path.abspath(output_directory)):
        print >> ERROR_FILE, 'directory cannot be found or is not a directory: ' + output_directory
        sys.exit(2)

    #do it all
    create_case_lists(clinical_file_name, output_directory, study_id, attribute)

# ---------------------------------------------------------------
# do a main
if __name__ == '__main__':
    main()
