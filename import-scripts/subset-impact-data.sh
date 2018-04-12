# UTILITY FOR SUBSETTING IMPACT DATA

# (1): study id
# (2): output directory
# (3): input data directory
# (4): data filter criteria to subset IMPACT data with (either SEQ_DATE or <ATTRIBUTE_NAME>=[ATTRIBUTE_VAL1,ATTRIBUTE_VAL2,...])
# (5): output subset filename
# (6): data_clinical filename containing attribute being filtered in (4)

for i in "$@"; do
case $i in
    -i=*|--study-id=*)
    STUDY_ID="${i#*=}"
    shift # past argument=value
    ;;
    -o=*|--output-directory=*)
    OUTPUT_DIRECTORY="${i#*=}"
    shift # past argument=value
    ;;
    -d=*|--input-directory=*)
    INPUT_DIRECTORY="${i#*=}"
    shift # past argument=value
    ;;
    -f=*|--filter-criteria=*)
    FILTER_CRITERIA="${i#*=}"
    shift # past argument=value
    ;;
    -s=*|--subset-filename=*)
    SUBSET_FILENAME="${i#*=}"
    shift # past argument=value
    ;;
    -p=*|--portal-scripts-directory=*)
    PORTAL_SCRIPTS_DIRECTORY="${i#*=}"
    shift # past argument=value
    ;;
    -c=*|--clinical-filename=*)
    CLINICAL_FILENAME="${i#*=}"
    shift # past argument=value
    ;;
    *)
      # default option
      echo "This option does not exist!  " "${i#*=}"
    ;;
esac
done
echo "Input arguments: "
echo -e "\tSTUDY_ID="$STUDY_ID
echo -e "\tOUTPUT_DIRECTORY="$OUTPUT_DIRECTORY
echo -e "\tINPUT_DIRECTORY="$INPUT_DIRECTORY
echo -e "\tFILTER_CRITERIA="$FILTER_CRITERIA
echo -e "\tSUBSET_FILENAME="$SUBSET_FILENAME
echo -e "\tCLINICAL_FILENAME="$CLINICAL_FILENAME
if [ -z $PORTAL_SCRIPTS_DIRECTORY ]; then
    PORTAL_SCRIPTS_DIRECTORY="$PORTAL_HOME/scripts"
fi
echo -e "\tPORTAL_SCRIPTS_DIRECTORY="$PORTAL_SCRIPTS_DIRECTORY

# status flags
GEN_SUBSET_LIST_FAILURE=0
MERGE_SCRIPT_FAILURE=0
ADD_METADATA_HEADERS_FAILURE=0

if [ $STUDY_ID == "genie" ]; then
    CLINICAL_SUPP_FILE="$INPUT_DIRECTORY/cvr/seq_date.txt"

    # in the case of genie data, the input data directory must be the msk-impact data home, where we expect to see darwin_naaccr.txt
    # copy the darwin genie files to the output directory with different filenames
    cp $INPUT_DIRECTORY/darwin/darwin_naaccr.txt $OUTPUT_DIRECTORY/data_clinical_supp_patient.txt
    cut -f1,2 $CLINICAL_FILENAME > $OUTPUT_DIRECTORY/data_clinical_supp_sample.txt

    # run the generate clinical subset script to generate list of sample ids to subset from impact data - subset of sample ids will be written to given $SUBSET_FILENAME
    echo "Generating subset list from $INPUT_DIRECTORY/$CLINICAL_FILENAME using filter criteria $FILTER_CRITERIA..."
    $PYTHON_BINARY $PORTAL_SCRIPTS_DIRECTORY/generate-clinical-subset.py --study-id="genie" --clinical-file="$OUTPUT_DIRECTORY/data_clinical_supp_sample.txt" --clinical-supp-file="$CLINICAL_SUPP_FILE" --filter-criteria="$FILTER_CRITERIA" --subset-filename="$SUBSET_FILENAME" --anonymize-date='true' --clinical-patient-file="$OUTPUT_DIRECTORY/data_clinical_supp_patient.txt"
    if [ $? -gt 0 ] ; then
        GEN_SUBSET_LIST_FAILURE=1
    else
        # expand data_clinical_supp_sample.txt with ONCOTREE_CODE, SAMPLE_TYPE, GENE_PANEL from data_clinical.txt
        $PYTHON_BINARY $PORTAL_SCRIPTS_DIRECTORY/expand-clinical-data.py --study-id="genie" --clinical-file="$OUTPUT_DIRECTORY/data_clinical_supp_sample.txt" --clinical-supp-file="$CLINICAL_FILENAME" --fields="ONCOTREE_CODE,SAMPLE_TYPE,GENE_PANEL" --identifier-column-name="SAMPLE_ID"
        $PYTHON_BINARY $PORTAL_SCRIPTS_DIRECTORY/add-age-at-seq-report.py --clinical-file="$OUTPUT_DIRECTORY/data_clinical_supp_sample.txt" --seq-date-file="$INPUT_DIRECTORY/cvr/seq_date.txt" --age-file="$INPUT_DIRECTORY/darwin/darwin_age.txt" --convert-to-days="true"
        # expand data_clinical_supp_patient.txt with AGE_AT_DEATH, AGE_AT_LAST_FOLLOWUP, OS_STATUS
        $PYTHON_BINARY $PORTAL_SCRIPTS_DIRECTORY/expand-clinical-data.py --study-id="genie" --clinical-file="$OUTPUT_DIRECTORY/data_clinical_supp_patient.txt" --clinical-supp-file="$INPUT_DIRECTORY/data_clinical_supp_darwin_demographics.txt" --fields="OS_STATUS" --identifier-column-name="PATIENT_ID"
        $PYTHON_BINARY $PORTAL_SCRIPTS_DIRECTORY/expand-clinical-data.py --study-id="genie" --clinical-file="$OUTPUT_DIRECTORY/data_clinical_supp_patient.txt" --clinical-supp-file="$INPUT_DIRECTORY/darwin/darwin_vital_status.txt" --fields="AGE_AT_DEATH,AGE_AT_LAST_FOLLOWUP" --identifier-column-name="PATIENT_ID"

        # rename GENE_PANEL to SEQ_ASSAY_ID in data_clinical_supp_sample.txt
        sed -i -e 's/GENE_PANEL/SEQ_ASSAY_ID/' $OUTPUT_DIRECTORY/data_clinical_supp_sample.txt

        # rename OS_STATUS to VITAL_STATUS in data_clinical_supp_patient.txt
        sed -i -e 's/OS_STATUS/VITAL_STATUS/' $OUTPUT_DIRECTORY/data_clinical_supp_patient.txt

        # generate subset of impact data using the subset file generated above
        echo "Subsetting data from $INPUT_DIRECTORY..."
        $PYTHON_BINARY $PORTAL_SCRIPTS_DIRECTORY/merge.py  -d $OUTPUT_DIRECTORY -i "genie" -s "$SUBSET_FILENAME" -x "true" $INPUT_DIRECTORY
        if [ $? -gt 0 ] ; then
            MERGE_SCRIPT_FAILURE=1
        else
            # remove germline mutations from maf
            grep -v 'GERMLINE' $OUTPUT_DIRECTORY/data_mutations_extended.txt > $OUTPUT_DIRECTORY/data_mutations_extended.txt.tmp
            mv $OUTPUT_DIRECTORY/data_mutations_extended.txt.tmp $OUTPUT_DIRECTORY/data_mutations_extended.txt
        fi
    fi
else
    # touch meta clinical if not already exists only if input dir has data_clinical.txt
    if [[ -f $INPUT_DIRECTORY/data_clinical.txt && ! -f $INPUT_DIRECTORY/meta_clinical.txt ]] ; then
        touch $INPUT_DIRECTORY/meta_clinical.txt
    fi

    # generate subset list of sample ids based on filter criteria and subset MSK-IMPACT using generated list in $SUBSET_FILENAME
    echo "Generating subset list from $CLINICAL_FILENAME using filter criteria $FILTER_CRITERIA..."
    $PYTHON_BINARY $PORTAL_SCRIPTS_DIRECTORY/generate-clinical-subset.py --study-id="$STUDY_ID" --clinical-file="$CLINICAL_FILENAME" --filter-criteria="$FILTER_CRITERIA" --subset-filename="$SUBSET_FILENAME"
    if [ $? -gt 0 ]; then
        GEN_SUBSET_LIST_FAILURE=1
    else
        echo "Subsetting data from $INPUT_DIRECTORY..."
        $PYTHON_BINARY $PORTAL_SCRIPTS_DIRECTORY/merge.py -d $OUTPUT_DIRECTORY -i "$STUDY_ID" -s "$SUBSET_FILENAME" -x "true" -m "true" $INPUT_DIRECTORY
        if [ $? -gt 0 ]; then
            MERGE_SCRIPT_FAILURE=1
        else
            # add clinical meta data headers if clinical sample file exists
            if [ -f $OUTPUT_DIRECTORY/data_clinical_sample.txt ]; then
                echo "Adding clinical attribute meta data headers..."
                $PYTHON_BINARY $PORTAL_SCRIPTS_DIRECTORY/add_clinical_attribute_metadata_headers.py -f $OUTPUT_DIRECTORY/data_clinical*
                if [ $? -gt 0 ]; then
                    ADD_METADATA_HEADERS_FAILURE=1
                fi
            fi
        fi
    fi

    # remove temp meta_clinical.txt if created
    if [[ -f $INPUT_DIRECTORY/meta_clinical.txt && $(wc -l < $INPUT_DIRECTORY/meta_clinical.txt) -eq 0 ]] ; then
        rm $INPUT_DIRECTORY/meta_clinical.txt
    fi
fi
# report errors
if [ $GEN_SUBSET_LIST_FAILURE -ne 0 ] ; then
    echo "Error while attempting to generate subset of sample ids by filter criteria $FILTER_CRITERIA"
fi
if [ $MERGE_SCRIPT_FAILURE -ne 0 ] ; then
    echo "Error while trying to subset data from $INPUT_DIRECTORY using subset list from $SUBSET_FILENAME"
fi
if [ $ADD_METADATA_HEADERS_FAILURE -ne 0 ] ; then
    echo "Error while attempting to add clinical attribute meta data headers to $OUTPUT_DIRECTORY/data_clinical*"
fi
# exit accordingly
if [[ $GEN_SUBSET_LIST_FAILURE -eq 0 && $MERGE_SCRIPT_FAILURE -eq 0 && $ADD_METADATA_HEADERS_FAILURE -eq 0 ]] ; then
    echo "Successfully subset data from $INPUT_DIRECTORY by filter criteria $FILTER_CRITERIA"
    exit 0
else
    exit 1
fi
