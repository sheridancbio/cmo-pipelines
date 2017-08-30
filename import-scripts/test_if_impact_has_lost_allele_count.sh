#!/bin/bash

email_list="cbioportal-pipelines@cbio.mskcc.org"
export tab=$'\t'
sourcefilename=/data/portal-cron/cbio-portal-data/msk-impact/msk-impact/data_mutations_extended.txt
#find header field indices
tempfilename=$(mktemp /tmp/mskimpact_mut_filed_test.XXXXXX)
head ${sourcefilename} |
		grep Mutation_Status |
		grep t_ref_count |
		grep t_alt_count |
		grep n_ref_count |
		grep n_alt_count > ${tempfilename}
headerlinecount=$(cat ${tempfilename} | wc -l)
if [ ${headerlinecount} -ne 1 ] ; then
	message="failed to complete mskimpact missing t_alt_count test because no header was found"
	echo ${message}
    	echo -e "${message}" | mail -s "MSKIMPACT scan for missing t_alt_count failure" $email_list
	rm -f ${tempfilename}
	exit 1
fi
mut_stat_index=0
t_ref_index=0
t_alt_index=0
n_ref_index=0
n_alt_index=0
cat ${tempfilename} | sed "s/${tab}/\n/g" > ${tempfilename}.fields
fieldcount=$(cat ${tempfilename}.fields | wc -l)
scanindex=0
while [ ${scanindex} -lt ${fieldcount} ] ; do
	scanindex=$(( ${scanindex} + 1 ))
	field=$(head -n ${scanindex} ${tempfilename}.fields| tail -n 1)
	if [ ${field} == "Mutation_Status" ] ; then mut_stat_index=${scanindex} ; fi
	if [ ${field} == "t_ref_count" ] ; then t_ref_index=${scanindex} ; fi
	if [ ${field} == "t_alt_count" ] ; then t_alt_index=${scanindex} ; fi
	if [ ${field} == "n_ref_count" ] ; then n_ref_index=${scanindex} ; fi
	if [ ${field} == "n_alt_count" ] ; then n_alt_index=${scanindex} ; fi
done
rm -f ${tempfilename}.fields
if [ ${mut_stat_index} -eq 0 ] || [ ${t_ref_index} -eq 0 ] || [ ${t_alt_index} -eq 0 ] || [ ${t_ref_index} -eq 0 ] || [ ${t_alt_index} -eq 0 ] ; then
	message="failed to complete mskimpact missing t_alt_count test because header was missing a necessary field"
	echo ${message}
    	echo -e "${message}" | mail -s "MSKIMPACT scan for missing t_alt_count failure" $email_list
	rm ${tempfilename}
	exit 2
fi
neededfields=${mut_stat_index},${t_ref_index},${t_alt_index},${n_ref_index},${n_alt_index}
headerfilter="grep -v \"T01-IM3\""
#count only records which are not germline (also ignore empty fields)
germlinefilter="grep SOMATIC\|UNKNOWN"
cut -f ${neededfields} ${sourcefilename} | ${headerfilter} | ${germlinefilter} | cut -f 2-5 > ${tempfilename}
sed "s/${tab}/,/g" ${tempfilename} | grep ",," > ${tempfilename}.blanks
recordswithblanks=$(cat ${tempfilename}.blanks | wc -l)
rm -f ${tempfilename} ${tempfilename}.blanks
if [ ${recordswithblanks} -gt 0 ] ; then
	message="mskimpact missing t_alt_count test failed : ${recordswithblanks} (somatic/unknown) records with blanks found in field t_ref_count, t_alt_count, n_ref_count, or n_alt_count"
	echo ${message}
    	echo -e "${message}" | mail -s "MSKIMPACT scan for missing t_alt_count failure" $email_list
	exit 3
fi
#test passed - no blanks found in somatic/unknown records
exit 0
