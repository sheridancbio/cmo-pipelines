import sys
import optparse
import csv
import os

ERROR_FILE = sys.stderr
OUTPUT_FILE = sys.stdout

SUPPLEMENTAL_SAMPLE_CLINICAL_DATA = {}
FILTERED_SAMPLE_IDS = set()

def expand_clinical_data_main(clinical_filename, fields):
	""" Loads supplemental clinical data from data_clinical.txt and writes data to output directory. """
	# add supp fields to header if not already exists
	header = get_file_header(clinical_filename)
	for supp_field in fields:
		if not supp_field in header:
			header.append(supp_field)

	# load data from clinical_filename and write data to output directory
	data_file = open(clinical_filename, 'rU')
	data_reader = csv.DictReader(data_file, dialect = 'excel-tab')
	output_data = ['\t'.join(header)]
	for line in data_reader:
		# update line with supplemental sample clinical data and format data as string for output file
		line.update(SUPPLEMENTAL_SAMPLE_CLINICAL_DATA.get(line['SAMPLE_ID'].strip(), {}))
		data = map(lambda v: line.get(v,''), header)
		output_data.append('\t'.join(data))
	data_file.close()	

	# write data to output file
	output_file = open(clinical_filename, 'w')
	output_file.write('\n'.join(output_data))
	output_file.close()
	print >> OUTPUT_FILE, 'Updated clinical file `' + clinical_filename + '` with supp fields: ' + ', '.join(fields)

def load_supplemental_clinical_data(supplemental_clinical_filename, supplemental_fields, study_id):
	""" Loads supplemental clinical data from supplemental_clinical_filename into SUPPLEMENTAL_SAMPLE_CLINICAL_DATA. """
	data_file = open(supplemental_clinical_filename, 'rU')
	data_reader = csv.DictReader(data_file, dialect = 'excel-tab')
	for line in data_reader:
		if study_id == 'genie':
			normalize_genie_sample_type(line)
		SUPPLEMENTAL_SAMPLE_CLINICAL_DATA[line['SAMPLE_ID'].strip()] = dict({(k,v) for k,v in line.items() if k in supplemental_fields})
	data_file.close()

def normalize_genie_sample_type(data):
	if data['SAMPLE_TYPE'].strip() == 'Metastasis':
		data['SAMPLE_TYPE'] = '4'
	elif data['SAMPLE_TYPE'].strip() == 'Primary':
		data['SAMPLE_TYPE'] = '1'
	return data


def get_file_header(filename):
	""" Returns the file header. """
	data_file = open(filename, 'rU')
	filedata = [x for x in data_file.readlines() if not x.startswith('#')]
	header = map(str.strip, filedata[0].split('\t'))
	data_file.close()
	return header

def usage():
	print >> OUTPUT_FILE, "expand-clinical-data.py --study-id [study id] --clinical-file [path to clinical file] --clinical-supp-file [path to clinical supp file] --fields [comma-delimited list of fields to expand the clinical file]"
	sys.exit(2)

def main():
	# get command line stuff
	parser = optparse.OptionParser()
	parser.add_option('-c', '--clinical-file', action = 'store', dest = 'clinfile')
	parser.add_option('-s', '--clinical-supp-file', action = 'store', dest = 'clinsuppfile')
	parser.add_option('-f', '--fields', action = 'store', dest = 'fields')
	parser.add_option('-i', '--study-id', action = 'store', dest = 'studyid')

	(options, args) = parser.parse_args()
	clin_file = options.clinfile
	clin_supp_file = options.clinsuppfile
	fields = options.fields.split(',')
	study_id = options.studyid

	# check arguments
	if not clin_file or not clin_supp_file or not fields or not study_id:
		print >> ERROR_FILE, "Clinical file, clinical supp file, fields, and study id must be provided!"
		usage()
	if not os.path.exists(clin_file):
		print >> ERROR_FILE, "No such file: " + clin_file
		usage()
	if not os.path.exists(clin_supp_file):
		print >> ERROR_FILE, "No such file: " + clin_supp_file
		usage()

	# load supplemental data from the clinical supp file
	load_supplemental_clinical_data(clin_supp_file, fields, study_id)
	expand_clinical_data_main(clin_file, fields)



if __name__ == '__main__':
	main()