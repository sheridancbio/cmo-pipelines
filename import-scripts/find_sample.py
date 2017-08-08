import sys
import os
import subprocess
import time
import getopt

def create_date_dict(log_lines):
	changeset_date_dict = {}
	for i, line in enumerate(log_lines):
		if 'date:' in line:
			changeset = log_lines[i-1].split(':')[1].strip()
			date = time.strptime(":".join(line.split(':')[1:]).split('-')[0].strip(), "%a %b %d %H:%M:%S %Y")
			date = time.strftime("%Y/%m/%d", date)
			changeset_date_dict[changeset] = date
	changesets_by_day = {}

	for cs, d in changeset_date_dict.iteritems():
		if d in changesets_by_day:
			changesets_by_day[d].append(cs)
		else:
			changesets_by_day[d] = [cs]
	
	to_include = []

	for same_day_changesets in changesets_by_day.values():
		to_include.append(max(map(int, same_day_changesets)))
	filtered_changeset_date_dict = {int(k):v for (k, v) in changeset_date_dict.iteritems() if k in map(str,to_include)}
	return filtered_changeset_date_dict

def find_sample(directory, sample):
	log = subprocess.check_output(['hg', 'log', '--cwd', directory])
	log_lines = log.split('\n')
	log_lines = [l for l in log_lines if 'changeset:' in l or 'date:' in l]


	changeset_date_dict = create_date_dict(log_lines)

	for cs, d in reversed(sorted(changeset_date_dict.items())):
		print 'On CS: ' + str(cs)
		subprocess.call(['hg', 'revert', '-r', str(cs), 'msk-impact/cvr_data.json', '--cwd', directory])
		json_file = ''
		try:
			json_file = open(os.path.join(directory, 'msk-impact/cvr_data.json'), 'r')
		except IOError:
			print 'No json file in changeset ' + str(cs)
		for line in json_file:
			if sample in line:
				print 'Found the sample! Changeset ' + str(cs)
				print sample
				print line
				sys.exit()

def usage():
	print 'find_sample.py --directory </path/to/msk-impact> --sample <sample_id>'	

def main():
	try:
		opts, args = getopt.getopt(sys.argv[1:], '', ['directory=', 'sample='])
	except getopt.error, msg:
		print msg
		usage()
		sys.exit(2)
	directory = ''
	sample = ''

	for o, a in opts:
		if o == '--directory':
			directory = a
		elif o == '--sample':
			sample = a
	
	if not os.path.isdir(directory):
		print directory + ' is not a directory'
		usage()
		sys.exit(2)
	
	find_sample(directory, sample)

if __name__ == '__main__':
	main()
