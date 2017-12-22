import os
import linecache

# returns header as list of attributes
def get_header(file):
    header = []
    with open(file, "r") as header_source:
        for line in header_source:
            if not line.startswith("#"):
                header = line.rstrip().split('\t')
                break
    return header

# old format is defined as a file containing 5 header lines (PATIENT and SAMPLE attributes in same file)
def is_old_format(file):
    return all([linecache.getline(file, header_line).startswith("#") for header_line in range(1,6)])

def get_metadata_mapping(file, attribute_line):
    metadata_mapping = {}
    metadata = linecache.getline(file, attribute_line).rstrip().replace("#", "").split('\t')
    attributes = get_header(file)
    for i in range(len(attributes)):
        metadata_mapping[attributes[i]] = metadata[i]
    return metadata_mapping

# get existing priority mapping in a given file
def get_description_mapping(file):
    return get_metadata_mapping(file, 2)

def get_datatype_mapping(file):
    return get_metadata_mapping(file, 3)

def get_display_name_mapping(file):
    return get_metadata_mapping(file, 1)

def get_priority_mapping(file):
    if is_old_format(file):
        return get_metadata_mapping(file, 5)
    else:
        return get_metadata_mapping(file, 4)

def get_attribute_type_mapping(file):
    if is_old_format(file):
        return get_metadata_mapping(file, 4)
    else:
        attribute_type_mapping = {}
        attributes = get_header(file)
        for attribute in attributes: 
            attribute_type_mapping[attribute] = "NA"
        return attribute_type_mapping

def get_all_metadata_mappings(file):
    all_metadata_mapping = {}
    all_metadata_mapping["DISPLAY_NAME"] = get_display_name_mapping(file)
    all_metadata_mapping["DESCRIPTION"] = get_description_mapping(file)
    all_metadata_mapping["DATATYPE"] = get_datatype_mapping(file)
    all_metadata_mapping["ATTRIBUTE_TYPE"] = get_attribute_type_mapping(file)
    all_metadata_mapping["PRIORITY"] = get_priority_mapping(file)
    return all_metadata_mapping
     
def write_header_line(line, output_file):
    os.write(output_file, '#')
    os.write(output_file, '\t'.join(line))
    os.write(output_file, '\n')

def write_data(file, output_file):
    with open(file) as source_file:
        for line in source_file:
            if not line.startswith("#"):
                os.write(output_file, line)
