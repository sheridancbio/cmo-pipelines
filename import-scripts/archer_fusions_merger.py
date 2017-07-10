import sys
import os
import argparse
import csv
import re
import fileinput

## remove from mixedpact so as to not double count

IMPACT_SAMPLE_PATTERN = re.compile('(P-\d*-T\d\d)-\S\S\d*')

def get_archer_to_impact(archer_clinical_filename):
	""" Get the mapping between archer -> impact samples """
	archer_to_impact = {}
	with open(archer_clinical_filename) as archer_clinical_file:
		reader = csv.DictReader(archer_clinical_file, dialect = 'excel-tab')
		for line in reader:
			if line['LINKED_MSKIMPACT_CASE'] is not 'NA':
				archer_to_impact[line['SAMPLE_ID'].strip()] = line['LINKED_MSKIMPACT_CASE'].strip()
	return archer_to_impact


def get_msk_fusions(msk_fusions_filename):				
	""" 
		Process the msk fusions file to get all current fusions, and to populate the seen_fusions set to prevent duplicates
		The columns that need to be looked at are the Tumor_Sample_Barcode, the Fusion, and Hugo_Symbol columns. If these are the same,
		the two fusions are identical from the perspective of the importer/portal
	"""
	header = []
	seen_fusions = set()
	msk_fusions = []
	for line in fileinput.input(msk_fusions_filename):
		if len(header) == 0:
			header = map(str.strip, line.split('\t'))
		data = map(str.strip, line.split('\t'))
		seen_fusions.add(data[header.index('Tumor_Sample_Barcode')] + '\t' + data[header.index('Fusion')] + '\t' + data[header.index('Hugo_Symbol')])
		msk_fusions.append(data)
	return msk_fusions, header, seen_fusions

def get_archer_fusions(archer_fusions_filename, header, msk_fusions, seen_fusions, archer_to_impact):
	"""
		As we process the archer fusions file, we want to associate the fusions with the corresponding impact id. 
		Lookup the sample in the sample map and add a fusion for each sample we need to.
	"""
	archer_samples = set()
	with open(archer_fusions_filename) as archer_fusions_file:
		reader = csv.DictReader(archer_fusions_file, dialect = 'excel-tab')
		for line in reader:
			if line['Tumor_Sample_Barcode'] in archer_to_impact:
				archer_samples.add(line['Tumor_Sample_Barcode'])
				archer_id = line['Tumor_Sample_Barcode']
				line['Tumor_Sample_Barcode'] = archer_to_impact[line['Tumor_Sample_Barcode']]
				line['Fusion'] = line['Fusion'] + ' - Archer'
				fusion = []
				for column in header:
					fusion.append(line.get(column, ''))
				if line['Tumor_Sample_Barcode'] + '\t' + line['Fusion'] + '\t' +line['Hugo_Symbol'] not in seen_fusions:
					msk_fusions.append(fusion)
					seen_fusions.add(line['Tumor_Sample_Barcode'] + '\t' + line['Fusion'] + '\t' + line['Hugo_Symbol'])
					archer_samples.add(archer_id.strip())
	return archer_samples

def write_fusions(msk_fusions_filename, header, msk_fusions):
	""" Write out the fusions to the msk fusions file """
	with open(msk_fusions_filename, 'w') as msk_fusions_file:
		msk_fusions_file.write('\t'.join(header))
		for msk_fusion in msk_fusions:
			msk_fusions_file.write('\n' + '\t'.join(msk_fusion))

def add_clinical_attribute_to_clinical(clinical_filename, archer_to_impact):
	""" Update the ARCHER clinical attribute """
	clinical_header = []
	for line in fileinput.input(clinical_filename, inplace = 1):
		data = map(str.strip, line.split('\t'))
		if len(clinical_header) == 0:
			clinical_header = data
		if data[clinical_header.index('SAMPLE_ID')] in archer_to_impact.values():
			data[clinical_header.index('ARCHER')] = 'YES'
		print '\t'.join(data)

def write_archer_samples_to_file(archer_samples_filename, archer_samples):
	""" Write out the archer sample ids to a filename for them to be excluded from the mixedpact study """

	with open(archer_samples_filename, 'w') as f:
		[f.write(sid + '\n') for sid in archer_samples]

def merge_fusions(archer_fusions_filename, msk_fusions_filename, linked_mskimpact_cases_filename, clinical_filename, archer_samples_filename):
	""" Driver function that calls helper functions to merge fusion records """
	archer_to_impact = get_archer_to_impact(linked_mskimpact_cases_filename)
	msk_fusions, header, seen_fusions = get_msk_fusions(msk_fusions_filename)
	archer_samples = get_archer_fusions(archer_fusions_filename, header, msk_fusions, seen_fusions, archer_to_impact)
	write_fusions(msk_fusions_filename, header, msk_fusions)
	add_clinical_attribute_to_clinical(clinical_filename, archer_to_impact)
	write_archer_samples_to_file(archer_samples_filename, archer_samples)

def main():
	parser = argparse.ArgumentParser()
	parser.add_argument('-a', '--archer-fusions', action = 'store', dest = 'archer_fusions_filename', required = True, help = 'data_fusions.txt from the Archer dataset')
	parser.add_argument('-l', '--linked-mskimpact-cases-filename', action = 'store', dest = 'linked_mskimpact_cases_filename' , required = True, help = 'linked_mskimpact_cases.txt from the Archer dataset')
	parser.add_argument('-m', '--msk-fusions', action = 'store', dest = 'msk_fusions_filename', required = True, help = 'data_fusions.txt from the mskimpact dataset')
	parser.add_argument('-c', '--clinical-filename', action = 'store', dest = 'clinical_filename', required = True, help = 'data_clinical.txt from the mskimpact dataset')
	parser.add_argument('-s', '--archer-samples-filename', action = 'store', dest = 'archer_samples_filename', required = True, help = 'Output file storing the archer ids that need to be removed from the mixedpact study')

	args = parser.parse_args()

	archer_fusions_filename = args.archer_fusions_filename
	msk_fusions_filename = args.msk_fusions_filename
	linked_mskimpact_cases_filename = args.linked_mskimpact_cases_filename
	clinical_filename = args.clinical_filename
	archer_samples_filename = args.archer_samples_filename
	
	if not os.path.exists(archer_fusions_filename):
		print 'Archer fusions file cannot be found ' + archer_fusions_filename
		sys.exit(2)
	if not os.path.exists(msk_fusions_filename):
		print 'Msk fusions file cannot be found ' + msk_fusions_filename
		sys.exit(2)
	if not os.path.exists(clinical_filename):
		print 'Clinical file cannot be found ' + clinical_filename
		sys.exit(2)
	if not os.path.exists(linked_mskimpact_cases_filename):
		print 'Linked mskimpact cases file cannot be found ' + linked_mskimpact_cases_filename
		sys.exit(2)

	merge_fusions(archer_fusions_filename, msk_fusions_filename, linked_mskimpact_cases_filename, clinical_filename, archer_samples_filename)

if __name__ == '__main__':
	main()