import sys
import optparse
import csv
import os
from datetime import datetime

ERROR_FILE = sys.stderr
OUTPUT_FILE = sys.stdout

FILTERING_CRITERIA = {}
SUPPLEMENTAL_SAMPLE_CLINICAL_DATA = {}
FILTERED_SAMPLE_IDS = set()

def filter_samples_by_clinical_attributes(clinical_filename):
	""" Filters samples by clinical attribute and values. """
	data_file = open(clinical_filename)
	data_reader = csv.DictReader(data_file, dialect = 'excel-tab')
	for line in data_reader:
		if keep_sample(line):
			FILTERED_SAMPLE_IDS.add(line['SAMPLE_ID'].strip())
	data_file.close()

def keep_sample(sample_data):
	""" Returns true if at least one of the attribute values meets one of the filtering criteria. """
	filtered_sample_data = dict({(k,v) for k,v in sample_data.items() if k in FILTERING_CRITERIA.keys()})
	for k,v in filtered_sample_data.items():
		if v.strip() in FILTERING_CRITERIA[k]:
			return True
	return False

def update_data_with_sequencing_date(study_id, clinical_filename):
	""" Updates clinical data with sequencing date. """
	# add supp fields to header if not already exists
	header = get_file_header(clinical_filename)
	if not 'SEQ_DATE' in header:
		header.append('SEQ_DATE')

	# load data from clinical_filename and write data to output directory
	data_file = open(clinical_filename, 'rU')
	data_reader = csv.DictReader(data_file, dialect = 'excel-tab')
	output_data = ['\t'.join(header)]
	for line in data_reader:
		if not line['SAMPLE_ID'].strip() in FILTERED_SAMPLE_IDS:			
			continue
		# update line with supplemental sample clinical data and format data as string for output file
		line.update(SUPPLEMENTAL_SAMPLE_CLINICAL_DATA.get(line['SAMPLE_ID'].strip(), {}))
		data = map(lambda v: line.get(v,''), header)
		output_data.append('\t'.join(data))
	data_file.close()	

	# write data to output file
	output_file = open(clinical_filename, 'w')
	output_file.write('\n'.join(output_data))
	output_file.close()
	print >> OUTPUT_FILE, 'Input clinical data updated with sequencing date for study: ' + study_id

def filter_samples_by_sequencing_date(clinical_supp_filename, sequencing_date_limit, anonymize_date):
	""" Generates list of sample IDs from sequencing date file meeting the sequencing date limit criteria. """
	seq_date_limit = datetime.strptime(sequencing_date_limit, '%Y/%m/%d %H:%M:%S')

	data_file = open(clinical_supp_filename, 'rU')
	data_reader = csv.DictReader(data_file, dialect = 'excel-tab')
	for line in data_reader:
		# figure out which column to use
		if 'SeqDate' in line.keys():
			seq_date_val = line['SeqDate']
		else:
			seq_date_val = line['SEQ_DATE']
		raw_sample_seq_date = datetime.strptime(seq_date_val, '%Y-%m-%d %H:%M:%S')

		# if sample sequencing date meets criteria then anonymize the sequencing date
		if raw_sample_seq_date <= seq_date_limit:
			sample_seq_date = ''
			if anonymize_date:
				# anonymize the sample seq date and add to supplemental sample clinical data
				if raw_sample_seq_date.month <= 3:
					sample_seq_date = 'Jan-' + str(raw_sample_seq_date.year)
				elif raw_sample_seq_date.month > 3 and raw_sample_seq_date.month <= 6:
					sample_seq_date = 'June-' + str(raw_sample_seq_date.year)
				elif raw_sample_seq_date.month > 6 and raw_sample_seq_date.month <= 9:
					sample_seq_date = 'July-' + str(raw_sample_seq_date.year)
				else:
					sample_seq_date = 'Oct-' + str(raw_sample_seq_date.year)
			else:
				sample_seq_date = raw_sample_seq_date.strftime('%Y/%m')

			try:
				sample_id = line['SAMPLE_ID'].strip()
			except KeyError:
				sample_id = line['DMP_ASSAY_ID'].strip()

			SUPPLEMENTAL_SAMPLE_CLINICAL_DATA[sample_id] = {'SEQ_DATE':sample_seq_date}
			FILTERED_SAMPLE_IDS.add(sample_id)
	data_file.close()

def generate_sample_subset_file(subset_filename):
	""" Writes subset of sample ids to output directory. """
	output_file = open(subset_filename, 'w')
	output_file.write('\n'.join(list(FILTERED_SAMPLE_IDS)))
	output_file.close()
	print >> OUTPUT_FILE, 'Subset file written to: ' + subset_filename

def is_valid_sequencing_date(sequencing_date_limit):
	""" Determines whether provided sequencing date limit is in valid format. """
	try:
		seq_date_limit = datetime.strptime(sequencing_date_limit, '%Y/%m/%d')
	except ValueError:
			return False
	return True

def get_file_header(filename):
	""" Returns the file header. """
	data_file = open(filename, 'rU')
	filedata = [x for x in data_file.readlines() if not x.startswith('#')]
	header = map(str.strip, filedata[0].split('\t'))
	data_file.close()
	return header

def is_valid_clin_header(clinical_filename, is_seq_date):
	""" Determines whether one of the sequencing date columns is in supplemental clinical file. """
	header = get_file_header(clinical_filename)
	# if seq date filter then only SEQ_DATE or SeqDate need to be in file header, 
	# otherwise we want to make sure all filtering criteria columns are in header
	if is_seq_date:
		for seq_date_col in ['SEQ_DATE', 'SeqDate']:
			if seq_date_col in header:
				return True
		return False
	else:
		for column in FILTERING_CRITERIA.keys():
			if not column in header:
				return False
		return True

def parse_filter_criteria(filter_criteria):
	attr_name = filter_criteria.split('=')[0]
	attr_values = filter_criteria.split('=')[1].split(',')
	if attr_name == 'SEQUENCING_DATE':
		if len(attr_values) > 1:
			print >> ERROR_FILE, "Only ONE sequencing date can be provided for filtering."
			exit(2)
		if not is_valid_sequencing_date(attr_values[0]):
			print >> ERROR_FILE, 'Sequencing date must be in YYYY/MM/DD format'
			sys.exit(2)
		FILTERING_CRITERIA['SEQ_DATE'] = attr_values[0] + ' 23:59:59' # add max hr:min:sec for date
	else:
		# more than one attribute filter can be provided, parse all attributes and values
		filter_criteria_parts = filter_criteria.split(';')
		for fc in filter_criteria_parts:
			attr_name = fc.split('=')[0]
			attr_values = fc.split('=')[1].split(',')
			FILTERING_CRITERIA[attr_name] = attr_values

def usage():
	print >> OUTPUT_FILE, "generate-clinical-subset.py --study-id [study id] --clinical-file [path to clinical file] --clinical-supp-file [path to clinical supp file] --filter-criteria [ATTR1=[VALUE1,VALUE2...];ATTR2=[VALUE1,VALUE2...]] --subset-filename [path to subset filename]"
	sys.exit(2)

def main():
	# get command line stuff
	parser = optparse.OptionParser()
	parser.add_option('-i', '--study-id', action = 'store', dest = 'studyid')
	parser.add_option('-c', '--clinical-file', action = 'store', dest = 'clinfile')
	parser.add_option('-s', '--clinical-supp-file', action = 'store', dest = 'clinsuppfile')
	parser.add_option('-f', '--filter-criteria', action = 'store', dest = 'filtercriteria')
	parser.add_option('-a', '--anonymize-date', action = 'store', dest = 'anondate')
	parser.add_option('-o', '--subset-filename', action = 'store', dest = 'subsetfile')

	(options, args) = parser.parse_args()
	study_id = options.studyid
	clin_file = options.clinfile
	clin_supp_file = options.clinsuppfile
	filter_criteria = options.filtercriteria
	anonymize_date = options.anondate
	subset_filename = options.subsetfile

	# study id, clinical file, and clinical supp file must be provided
	if not study_id or not filter_criteria or not clin_file:
		print >> ERROR_FILE, 'Study ID, filtering criteria and clinical file must be provided!'
		usage()

	# make sure that clinical file and supp file exist
	if not os.path.exists(clin_file):
		print >> ERROR_FILE, 'No such file: ' + clin_file
		usage()
	if clin_supp_file and not os.path.exists(clin_supp_file):
		print >> ERROR_FILE, 'No such file: ' + clin_supp_file
		usage()

	# determine whether to anonymize date or not
	if not anonymize_date or anonymize_date.lower() == 'false':
		anonymize_date = False
	else:
		anonymize_date = True

	# parse the filtering criteria
	parse_filter_criteria(filter_criteria)
	if 'SEQ_DATE' in FILTERING_CRITERIA.keys():
		# if no supp file provided then check clin file for 
		if not clin_supp_file and is_valid_clin_header(clin_file, True):
			filter_samples_by_sequencing_date(clin_file, FILTERING_CRITERIA['SEQ_DATE'], anonymize_date)
		elif clin_supp_file and is_valid_clin_header(clin_supp_file, True):
			filter_samples_by_sequencing_date(clin_supp_file, FILTERING_CRITERIA['SEQ_DATE'], anonymize_date)
			update_data_with_sequencing_date(study_id, clin_file)
		else:
			if clin_supp_file:
				print >> ERROR_FILE, 'Clinical supp file must contain either `SEQ_DATE` or `SeqDate` in header!'
			else:
				print >> ERROR_FILE, 'Clinical file must contain either `SEQ_DATE` or `SeqDate` in header!'
			exit(2)		
	else:
		if not clin_supp_file and is_valid_clin_header(clin_file, False):
			filter_samples_by_clinical_attributes(clin_file)
		elif clin_supp_file and is_valid_clin_header(clin_supp_file, False):
			filter_samples_by_clinical_attributes(clin_supp_file)
	# generate file with subset of ids		
	generate_sample_subset_file(subset_filename)


if __name__ == '__main__':
	main()