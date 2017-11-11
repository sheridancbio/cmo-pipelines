# UTILITY FOR SUBSETTING IMPACT DATA

# (1): study id
# (2): output directory
# (3): mskimpact data directory
# (4): data filter criteria to subset IMPACT data with (either SEQ_DATE or <ATTRIBUTE_NAME>=[ATTRIBUTE_VAL1,ATTRIBUTE_VAL2,...])
# (5): output subset filename

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
    -m=*|--mskimpact-directory=*)
    MSK_IMPACT_DATA_DIRECTORY="${i#*=}"
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
    *)
      # default option
      echo "This option does not exist!  " "${i#*=}"
    ;;
esac
done
echo "Input arguments: "
echo -e "\tSTUDY_ID="$STUDY_ID
echo -e "\tOUTPUT_DIRECTORY="$OUTPUT_DIRECTORY
echo -e "\tMSK_IMPACT_DATA_DIRECTORY="$MSK_IMPACT_DATA_DIRECTORY
echo -e "\tFILTER_CRITERIA="$FILTER_CRITERIA
echo -e "\tSUBSET_FILENAME="$SUBSET_FILENAME
if [ -z $PORTAL_SCRIPTS_DIRECTORY ]; then
    PORTAL_SCRIPTS_DIRECTORY="$PORTAL_HOME/scripts"
fi
echo -e "\tPORTAL_SCRIPTS_DIRECTORY="$PORTAL_SCRIPTS_DIRECTORY

if [ $STUDY_ID == "genie" ]; then
    CLINICAL_SUPP_FILE="$MSK_IMPACT_DATA_DIRECTORY/seq_date.txt"

    # in the case of genie data, the input data directory must be the msk-impact data home, where we expect to see darwin_genie_sample.txt and darwin_genie_patient.txt as well
    # copy the darwin genie files to the output directory with different filenames
    cp $MSK_IMPACT_DATA_DIRECTORY/darwin_naaccr.txt $OUTPUT_DIRECTORY/data_clinical_supp_patient.txt
    cut -f1,2 $MSK_IMPACT_DATA_DIRECTORY/data_clinical.txt > $OUTPUT_DIRECTORY/data_clinical_supp_sample.txt
    
    # run the generate clinical subset script to generate list of sample ids to subset from impact data - subset of sample ids will be written to given $SUBSET_FILENAME
    $PYTHON_BINARY $PORTAL_SCRIPTS_DIRECTORY/generate-clinical-subset.py --study-id="genie" --clinical-file="$OUTPUT_DIRECTORY/data_clinical_supp_sample.txt" --clinical-supp-file="$CLINICAL_SUPP_FILE" --filter-criteria="$FILTER_CRITERIA" --subset-filename="$SUBSET_FILENAME" --anonymize-date='true' --clinical-patient-file="$OUTPUT_DIRECTORY/data_clinical_supp_patient.txt"

    # expand data_clinical_supp_sample.txt with ONCOTREE_CODE, SAMPLE_TYPE, GENE_PANEL from data_clinical.txt
    $PYTHON_BINARY $PORTAL_SCRIPTS_DIRECTORY/expand-clinical-data.py --study-id="genie" --clinical-file="$OUTPUT_DIRECTORY/data_clinical_supp_sample.txt" --clinical-supp-file="$MSK_IMPACT_DATA_DIRECTORY/data_clinical.txt" --fields="ONCOTREE_CODE,SAMPLE_TYPE,GENE_PANEL" --identifier-column-name="SAMPLE_ID"
    $PYTHON_BINARY $PORTAL_SCRIPTS_DIRECTORY/add-age-at-seq-report.py --clinical-file="$OUTPUT_DIRECTORY/data_clinical_supp_sample.txt" --seq-date-file="$MSK_IMPACT_DATA_DIRECTORY/seq_date.txt" --age-file="$MSK_IMPACT_DATA_DIRECTORY/darwin_age.txt" --convert-to-days="true"
    # expand data_clinical_supp_patient.txt with AGE_AT_DEATH, AGE_AT_LAST_FOLLOWUP, OS_STATUS
    $PYTHON_BINARY $PORTAL_SCRIPTS_DIRECTORY/expand-clinical-data.py --study-id="genie" --clinical-file="$OUTPUT_DIRECTORY/data_clinical_supp_patient.txt" --clinical-supp-file="$MSK_IMPACT_DATA_DIRECTORY/data_clinical_supp_darwin_demographics.txt" --fields="OS_STATUS" --identifier-column-name="PATIENT_ID"
    $PYTHON_BINARY $PORTAL_SCRIPTS_DIRECTORY/expand-clinical-data.py --study-id="genie" --clinical-file="$OUTPUT_DIRECTORY/data_clinical_supp_patient.txt" --clinical-supp-file="$MSK_IMPACT_DATA_DIRECTORY/darwin_vital_status.txt" --fields="AGE_AT_DEATH,AGE_AT_LAST_FOLLOWUP" --identifier-column-name="PATIENT_ID"

    # rename GENE_PANEL to SEQ_ASSAY_ID in data_clinical_supp_sample.txt
    sed -i -e 's/GENE_PANEL/SEQ_ASSAY_ID/' $OUTPUT_DIRECTORY/data_clinical_supp_sample.txt

    # rename OS_STATUS to VITAL_STATUS in data_clinical_supp_patient.txt
    sed -i -e 's/OS_STATUS/VITAL_STATUS/' $OUTPUT_DIRECTORY/data_clinical_supp_patient.txt
    
    # touch meta clinical if not already exists
    if [ ! -f $MSK_IMPACT_DATA_DIRECTORY/meta_clinical.txt ]; then
        touch $MSK_IMPACT_DATA_DIRECTORY/meta_clinical.txt
    fi

    # generate subset of impact data using the subset file generated above
    $PYTHON_BINARY $PORTAL_SCRIPTS_DIRECTORY/merge.py  -d $OUTPUT_DIRECTORY -i "genie" -s "$SUBSET_FILENAME" -x "true" $MSK_IMPACT_DATA_DIRECTORY
    
    # remove germline mutations from maf
    grep -v 'GERMLINE' $OUTPUT_DIRECTORY/data_mutations_extended.txt > $OUTPUT_DIRECTORY/data_mutations_extended.txt.tmp
    mv $OUTPUT_DIRECTORY/data_mutations_extended.txt.tmp $OUTPUT_DIRECTORY/data_mutations_extended.txt

    # remove if file empty
    if [ $(wc -l < $MSK_IMPACT_DATA_HOME/meta_clinical.txt) -eq 0 ]; then
        rm $MSK_IMPACT_DATA_HOME/meta_clinical.txt
    fi
else
    # generate subset list of sample ids based on filter criteria and subset MSK-IMPACT using generated list in $SUBSET_FILENAME
    $PYTHON_BINARY $PORTAL_SCRIPTS_DIRECTORY/generate-clinical-subset.py --study-id="$STUDY_ID" --clinical-file="$MSK_IMPACT_DATA_DIRECTORY/data_clinical.txt" --filter-criteria="$FILTER_CRITERIA" --subset-filename="$SUBSET_FILENAME"
    $PYTHON_BINARY $PORTAL_SCRIPTS_DIRECTORY/merge.py -d $OUTPUT_DIRECTORY -i "$STUDY_ID" -s "$SUBSET_FILENAME" -x "true" -m "true" $MSK_IMPACT_DATA_DIRECTORY
fi
