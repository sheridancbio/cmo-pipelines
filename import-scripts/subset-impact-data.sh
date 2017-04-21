# UTILITY FOR SUBSETTING IMPACT DATA

# (1): study id
# (2): output directory 
# (3): mskimpact data directory 
# (4): data filter criteria to subset IMPACT data with (either SEQUENCING_DATE or <ATTRIBUTE_NAME>=[ATTRIBUTE_VAL1,ATTRIBUTE_VAL2,...])
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
    *)
      # default option
      echo "This option does not exist!  " "${i#*=}" 
    ;;
esac
done
echo "Input arguments: "
echo "\tSTUDY_ID="$STUDY_ID
echo "\tOUTPUT_DIRECTORY="$OUTPUT_DIRECTORY
echo "\tMSK_IMPACT_DATA_DIRECTORY="$MSK_IMPACT_DATA_DIRECTORY
echo "\tFILTER_CRITERIA="$FILTER_CRITERIA
echo "\tSUBSET_FILENAME="$SUBSET_FILENAME

if [ $STUDY_ID == "genie" ]; then
	# once cvr pipeline captures SEQ_DATE, we can get rid of this if/else block
	if [[ $(head -1 $MSK_IMPACT_DATA_DIRECTORY/data_clinical.txt) == *"SEQ_DATE"* ]]; then
		CLINICAL_SUPP_FILE="$MSK_IMPACT_DATA_DIRECTORY/data_clinical.txt"
	else:
		CLINICAL_SUPP_FILE="$MSK_IMPACT_DATA_DIRECTORY/genie_sequencing_date_dump.txt"
	fi

	# in the case of genie data, the input data directory must be the msk-impact data home, where we expect to see darwin_genie_sample.txt and darwin_genie_patient.txt as well
	# copy the darwin genie files to the output directory with different filenames
	cp $MSK_IMPACT_DATA_DIRECTORY/darwin_genie_patient.txt $OUTPUT_DIRECTORY/data_clinical_supp_patient.txt
	cp $MSK_IMPACT_DATA_DIRECTORY/darwin_genie_sample.txt $OUTPUT_DIRECTORY/data_clinical_supp_sample.txt
	
	# run the generate clinical subset script to generate list of sample ids to subset from impact data - subset of sample ids will be written to given $SUBSET_FILENAME
	$PYTHON_BINARY $PORTAL_HOME/scripts/generate-clinical-subset.py --study-id="genie" --clinical-file="$OUTPUT_DIRECTORY/data_clinical_supp_sample.txt" --clinical-supp-file="$CLINICAL_SUPP_FILE" --filter-criteria="$FILTER_CRITERIA" --subset-filename="$SUBSET_FILENAME" --anonymize-date='true'
	# expand data_clinical_supp_sample.txt with ONCOTREE_CODE, SAMPLE_TYPE from data_clinical.txt
	$PYTHON_BINARY $PORTAL_HOME/scripts/expand-clinical-data.py --study-id="genie" --clinical-file="$OUTPUT_DIRECTORY/data_clinical_supp_sample.txt" --clinical-supp-file="$MSK_IMPACT_DATA_DIRECTORY/data_clinical.txt" --fields="ONCOTREE_CODE,SAMPLE_TYPE"
	# generate subset of impact data using the subset file generated above
	$PYTHON_BINARY $PORTAL_HOME/scripts/merge.py  -d $OUTPUT_DIRECTORY -i "genie" -s "$SUBSET_FILENAME" -x "true" -$MSK_IMPACT_DATA_DIRECTORY
else
	# generate subset list of sample ids based on filter criteria and subset MSK-IMPACT using generated list in $SUBSET_FILENAME
	$PYTHON_BINARY $PORTAL_HOME/scripts/generate-clinical-subset.py --study-id="$STUDY_ID" --clinical-file="$OUTPUT_DIRECTORY/data_clinical_supp_sample.txt" --clinical-supp-file="$CLINICAL_SUPP_FILE" --filter-criteria="$FILTER_CRITERIA" --subset-filename="$SUBSET_FILENAME"
	$PYTHON_BINARY $PORTAL_HOME/scripts/merge.py  -d $OUTPUT_DIRECTORY -i "$STUDY_ID" -s "$SUBSET_FILENAME" -x "true" $MSK_IMPACT_DATA_DIRECTORY
fi