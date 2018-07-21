#!/usr/bin/env python

import argparse
import io
import os
import shutil
import sys
import tempfile

def exit_with_error_message(message, exit_status):
    sys.stderr.write(message)
    sys.exit(exit_status)

def find_header_row_index(column):
    """ returns the index of the located header row (first non-commented row) """
    header_row_index = None
    header_row_found = False
    for row_index, row_field in enumerate(column):
        field_is_commented = row_field.startswith('#')
        if field_is_commented and header_row_found:
            exit_with_error_message('ERROR: commented-out field found in data section (after header row): ' + row_field + '\n', 2)
        this_row_is_the_header_row = not header_row_found and not field_is_commented
        if this_row_is_the_header_row:
            header_row_found = True
            header_row_index = row_index
    if not header_row_found:
        exit_with_error_message('ERROR: could not find header row in file\n', 2)
    return header_row_index

def parse_file(filepath):
    """ reads a rectangular tab delimited file and groups values by column, returning a list of lists """
    with io.open(filepath, "r", encoding="utf-8") as f:
        columns = []
        for line in f:
            row_values = line.rstrip('\n').split('\t')
            if not row_values:
                exit_with_error_message('ERROR: empty row encountered', 2)
            if not columns:
                #initialize columns
                for value in row_values:
                    columns.append([ value ])
                continue
            if len(row_values) != len(columns):
                exit_with_error_message('ERROR: non-rectangular file format. Previous rows contained ' + str(len(columns)) +
                        ' fields, but new row contains ' + str(len(row_values)) + ' fields:\n' + line + '\n', 2)
            for column_index, value in enumerate(row_values):
                columns[column_index].append(value)
        return columns

def column_contains_meaningful_values(values_found_in_column):
    """ returns True if the set of values in a column contains any non-empty value """
    values_found_in_column.discard('NA')
    values_found_in_column.discard('N/A')
    values_found_in_column.discard('NOT_AVAILABLE')
    values_found_in_column.discard('')
    if values_found_in_column:
        return True
    return False

def remove_empty_columns(raw_file_contents, keep_column_set):
    """ takes list of lists [each element is a column, listing the values down the rows] and drops columns which are empty of content """
    if not raw_file_contents:
        exit_with_error_message('ERROR: file is empty\n', 2)
    filtered_file_contents = []
    processing_first_column = True
    header_row_index = find_header_row_index(raw_file_contents[0])
    for column in raw_file_contents:
        header_for_column = column[header_row_index]
        if header_for_column in keep_column_set:
            filtered_file_contents.append(column)
        else:
            values_found_in_column = set()
            for row_field in (column[header_row_index + 1:]):
                values_found_in_column.add(row_field.strip().upper())
            if column_contains_meaningful_values(values_found_in_column):
                filtered_file_contents.append(column)
            else:
                if processing_first_column:
                    exit_with_error_message('ERROR: first column contains only empty/NA data values - however, unable to filter due to headers\n', 2)
        processing_first_column = False
    return filtered_file_contents

def write_file(filepath, filtered_file_contents):
    """ writes out a rectangular matrix, taking a list of columns, each of which is a list of values down the rows """
    # create temp file to write to
    temp_file, temp_file_name = tempfile.mkstemp()
    number_of_rows = len(filtered_file_contents[0])
    for row_index in range(number_of_rows):
        output_row = [ column[row_index] for column in filtered_file_contents ]
        os.write(temp_file, ('\t'.join(output_row) + '\n').encode('utf-8'))
    os.close(temp_file)
    # replace original file with new file
    shutil.move(temp_file_name, filepath)

def parse_keep_columns(keep_column_list_string):
    if not keep_column_list_string or not keep_column_list_string.strip():
        return set()
    keep_column_list = [ unicode(column_name.strip()) for column_name in keep_column_list_string.split(',') ]
    return set(keep_column_list)

def exit_with_error_if_file_is_not_accessible(filepath):
    if not os.path.exists(filepath):
        exit_with_error_message('ERROR: file cannot be found: ' + filepath + '\n', 2)
    if not os.access(filepath, os.R_OK):
        exit_with_error_message('ERROR: file permissions do not allow reading: ' + filepath + '\n', 2)
    if not os.access(filepath, os.W_OK):
        exit_with_error_message('ERROR: file permissions do not allow writing: ' + filepath + '\n', 2)

def process_file(filepath, keep_column_set):
    """ find and filter columns with no content """
    raw_file_contents = parse_file(filepath)
    filtered_file_contents = remove_empty_columns(raw_file_contents, keep_column_set)
    write_file(filepath, filtered_file_contents)

def main():
    """ Scan values down columns and filter any columns with no content """
    parser = argparse.ArgumentParser()
    parser.add_argument('-f', '--file', action = 'store', dest = 'filepath', required = True, help = 'Path to the tab delimited file')
    parser.add_argument('-k', '--keep-column-list', action = 'store', dest = 'keep_column_list_string', required = False, help = 'Comma separated list of column headers to always keep')
    args = parser.parse_args()
    filepath = args.filepath
    keep_column_set = parse_keep_columns(args.keep_column_list_string)
    exit_with_error_if_file_is_not_accessible(filepath)
    process_file(filepath, keep_column_set)
    sys.exit(0)
if __name__ == '__main__':
    main()
