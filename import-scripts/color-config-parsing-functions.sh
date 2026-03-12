# bash functions for parsing values from a color swap config file
#
# correct behavior requires the definition of environment variable YQ_BINARY containing a filepath to executable tool 'yq'

function exit_with_error_if_yq_unavailable() {
    if [ -z "$YQ_BINARY" ] ; then
        echo "Error : environment YQ_BINARY is undefined. This must contain a valid filepath to the yq executable."
        exit 1
    fi
    if ! [ -f "$YQ_BINARY" ] ; then
        echo "Error : environment YQ_BINARY does not contain a valid filepath to the yq executable."
        exit 1
    fi
    if ! [ -x "$YQ_BINARY" ] ; then
        echo "Error : environment YQ_BINARY contains a path to a file which is not executable."
        exit 1
    fi
}

function read_scalar_from_yaml() {
    local filepath="$1"
    local element_path="$2"
    exit_with_error_if_yq_unavailable
    local value=$("$YQ_BINARY" -r "$element_path" "$filepath")
    if [ "$value" == "null" ] || [ -z "$value" ] ; then
        echo "Error : missing required scalar '$element_path' in $filepath" >&2
        exit 1
    fi
    printf '%s\n' "$value"
}

function read_array_from_yaml() {
    local dest_var="$1"
    local filepath="$2"
    local element_path="$3"
    exit_with_error_if_yq_unavailable
    local array_path="${element_path}[]"
    local type=$("$YQ_BINARY" -r "$element_path | type" "$filepath")
    if [ "$type" != "!!seq" ] ; then
        echo "Error : expected array at '$element_path' in $filepath" >&2
        exit 1
    fi
    unset "$dest_var"
    declare -g -a "$dest_var"
    readarray -t "$dest_var" < <("$YQ_BINARY" -r "$array_path" "$filepath")
}

function read_map_from_yaml() {
    local dest_var="$1"
    local filepath="$2"
    local element_path="$3"
    exit_with_error_if_yq_unavailable
    local type=$("$YQ_BINARY" -r "$element_path | type" "$filepath")
    if [ "$type" != "!!map" ] ; then
        echo "Error : expected map at '$element_path' in $filepath" >&2
        exit 1
    fi
    unset "$dest_var"
    declare -gA "$dest_var"
    local has_entries=0
    while IFS=$'\t' read -r entry_key entry_value ; do
        printf -v "$dest_var[$entry_key]" '%s' "$entry_value"
        has_entries=1
    done < <("$YQ_BINARY" -r "$element_path | to_entries[] | [.key, .value] | @tsv" "$filepath")
    if [ "$has_entries" -eq 0 ] ; then
        echo "Error : map at '$element_path' in $filepath must contain at least one entry" >&2
        exit 1
    fi
}
