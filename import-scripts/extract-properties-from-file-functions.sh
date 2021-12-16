#!/usr/bin/env bash

EXTRACT_PROPERTIES_SIMPLE_FILE_TYPE="simpleFile"
EXTRACT_PROPERTIES_YAML_FILE_TYPE="yamlFile"
string_trimmed_of_flanking_space=""
string_trimmed_of_quotation_marks=""

# function takes a single string argument, trims leading and trailing whitespace characters. returns result in variable 'string_trimmed_of_flanking_space'
# usage : trim_flanking_space_from_string "  THIS STRING    "
function trim_flanking_space_from_string() {
    string="$1"
    string_trimmed_of_flanking_space="$string"
    if [[ $string =~ ^[[:space:]]*(.*[^[:space:]])[[:space:]]*$ ]] ; then
        string_trimmed_of_flanking_space="${BASH_REMATCH[1]}"
        return 0
    fi
    return 1
}

# function removes one matched pair of quotation marks or apostrophes at the start and end positions in the string if present. returns result in variable 'string_trimmed_of_quotation_marks'
# usage : trim_enclosing_quotation_marks_from_string "'THIS STRING'"
function trim_enclosing_quotation_marks_from_string() {
    string="$1"
    string_trimmed_of_quotation_marks="$string"
    apostrophe_regex="^'(.*)'$"
    if [[ $string =~ $apostrophe_regex ]] ; then
        string_trimmed_of_quotation_marks="${BASH_REMATCH[1]}"
        return 0
    fi
    quotation_mark_regex='^"(.*)"$'
    if [[ $string =~ $quotation_mark_regex ]] ; then
        string_trimmed_of_quotation_marks="${BASH_REMATCH[1]}"
        return 0
    fi
    return 1
}

# function tests for lines which begin with the comment prefix '#' returns 0 if line is commented
# usage : property_file_line_is_commented "#commented line"
function property_file_line_is_commented() {
    COMMENT_PREFIX="#";
    line="$1"
    if [[ $line =~ ^$COMMENT_PREFIX ]] ; then
        return 0
    fi
    return 1
}

# function scans a property file (simple or yaml) for requested keys and returns a key/value map from the file. the file must be formatted with one setting per text line
# whitespace and comments are handled reasonably, and quoted strings in yaml files are de-quoted before values are extracted (will not work with strings with internal escaped characters).
# usage : extractPropertiesFromFileOrMap sourcefile.properties simpleFile|yamlFile propertyname1 propertyname2 propertyname3 ...
function extractPropertiesFromFileOrMap() {
    if [ $# -lt 2 ] ; then
        return 1 # error -- insufficient arguments
    fi
    PROPERTIES_FILENAME=$1
    shift 1
    if [ ! -r $PROPERTIES_FILENAME ] ; then
        return 2 # error -- cannot read file
    fi
    FILE_TYPE=$1
    shift 1
    if ! [ "$FILE_TYPE" == "$EXTRACT_PROPERTIES_SIMPLE_FILE_TYPE" ] && ! [ "$FILE_TYPE" == "$EXTRACT_PROPERTIES_YAML_FILE_TYPE" ] ; then
        return 4 # error -- improper filetype argument
    fi
    assignment_operator="="
    if [ "$FILE_TYPE" == "$EXTRACT_PROPERTIES_YAML_FILE_TYPE" ] ; then
        assignment_operator=":"
    fi
    # test and reset return array
    if ! declare -A | grep " extracted_properties=" > /dev/null 2>&1 ; then
        return 3 # error -- caller did not declare extracted_properties associative array
    fi
    for prop in "${!extracted_properties[@]}" ; do
        unset extracted_properties[$prop]
    done
    # initialize keys in return array (reads arguments in positions 3, 4, ...)
    for property_name ; do
        extracted_properties[$property_name]=""
    done
    while IFS="" read -r line ; do
        trim_flanking_space_from_string "$line"
        trimmed_line="$string_trimmed_of_flanking_space"
        if ! property_file_line_is_commented "$trimmed_line" ; then
            for prop in "${!extracted_properties[@]}" ; do
                if [[ $trimmed_line =~ ^$prop[[:space:]]*$assignment_operator(.*) ]] ; then
                    value=${BASH_REMATCH[1]}
                    trim_flanking_space_from_string "$value"
                    trimmed_value="$string_trimmed_of_flanking_space"
                    if [ "$FILE_TYPE" == "$EXTRACT_PROPERTIES_YAML_FILE_TYPE" ] ; then
                        trim_enclosing_quotation_marks_from_string "$trimmed_value"
                        trimmed_value="$string_trimmed_of_quotation_marks"
                    fi
                    extracted_properties[$prop]="$trimmed_value"
                fi
            done
        fi
    done < $PROPERTIES_FILENAME
    return 0
}

# Function to extract property settings from a simple properties file
# usage : extractPropertiesFromFile sourcefile.properties propertyname1 propertyname2 propertyname3 ...
# caller must 'declare -Ax extracted_properties' before calling this function
function extractPropertiesFromFile() {
    PROPERTIES_FILENAME=$1
    shift 1
    extractPropertiesFromFileOrMap $PROPERTIES_FILENAME $EXTRACT_PROPERTIES_SIMPLE_FILE_TYPE $@
    return $?
}

# Function to extract property settings from a kubernetes configmap yaml file
# usage : extractPropertiesFromConfigMapFile config_map_filename.yaml propertyname1 propertyname2 propertyname3 ...
# caller must 'declare -Ax extracted_properties' before calling this function
function extractPropertiesFromConfigMapFile() {
    PROPERTIES_FILENAME=$1
    shift 1
    extractPropertiesFromFileOrMap $PROPERTIES_FILENAME $EXTRACT_PROPERTIES_YAML_FILE_TYPE $@
    return $?
}
