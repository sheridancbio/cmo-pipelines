#! /usr/bin/env python

import argparse
import os
import sys
#---------------------------------------------------------------------------------------------------
# This script reads MAF and finds samples which have over-long allele strings. Output: list of barcodes
# If importer skips mutation record, the variant does not trigger inclusion in the list
# rules for importer skipping derived from pipelines/importer:
#   - Validation_Status == "Wildtype"
#   - Variant_Classification == "rna" or "IGR"
#   - Variant_Classification is in set {"Silent", "Intron", "3'UTR", "3'Flank", "5'UTR", "IGR"}
#   - Chromosome is not in set {1,2,...24,CHR1,...CHR24,X,Y,CHRX,CHRY,NA,MT}
#   - Entrez_Gene_Id is not empty and not parsable as a natural number (or is not in gene table, but we will not test that here)
#   - (the alleles are tested against a regular expression for valid content, but we will not test that here)
#   - Mutation_Status is in set {"None", "LOH", "Wildtype", "Redacted"}
#--------------------------------------------------------------------------------------------------
ERROR_FILE = sys.stderr
MAX_ALLELE_LENGTH = 255

# test allele length
def allele_is_too_long(allele):
    return len(allele.strip()) > MAX_ALLELE_LENGTH

# test allele lengths
def any_allele_is_too_long(reference_allele, tumor_seq_allele1, tumor_seq_allele2):
    return allele_is_too_long(reference_allele) or allele_is_too_long(tumor_seq_allele1) or allele_is_too_long(tumor_seq_allele2)

def truncate_allele_field(allele_field):
    if len(allele_field) <= MAX_ALLELE_LENGTH:
        return allele_field
    else:
        return allele_field[0:(MAX_ALLELE_LENGTH - 3)] + "..."

# parse mutations file and return a list of truncated records
def get_truncated_records_to_write(mutations_file):
    lines_to_write = []
    header_read = False
    reference_allele_index = -1
    tumor_seq_allele1_index = -1
    tumor_seq_allele2_index = -1
    with open(mutations_file) as f:
        for line in f:
            stripped_line = line.rstrip("\n")
            if stripped_line.startswith("#"):
                lines_to_write.append(stripped_line)
                continue
            field = stripped_line.split("\t")
            if not header_read:
                if stripped_line.startswith("Hugo_Symbol\t"):
                    try:
                        reference_allele_index = field.index("Reference_Allele")
                        tumor_seq_allele1_index = field.index("Tumor_Seq_Allele1")
                        tumor_seq_allele2_index = field.index("Tumor_Seq_Allele2")
                    except ValueError:
                        ERROR_FILE.write("Error: necessary header field is missing\n")
                        sys.exit(5)
                    header_read = True
                    lines_to_write.append("\t".join(field))
                else:
                    ERROR_FILE.write("Error: file does not appear to be in MAF format; missing header\n")
                    sys.exit(4)
            else:
                # header has been parsed
                field[reference_allele_index] = truncate_allele_field(field[reference_allele_index])
                field[tumor_seq_allele1_index] = truncate_allele_field(field[tumor_seq_allele1_index])
                field[tumor_seq_allele2_index] = truncate_allele_field(field[tumor_seq_allele2_index])
                lines_to_write.append("\t".join(field))
    
    return lines_to_write

def generate_message_body(dropped_samples_list):
    message_body = ""
    if len(dropped_samples_list) > 0:
        message_body = message_body + "Number of " + item_type + " dropped: " + str(len(dropped_samples_list)) + "\n"
    return message_body

def usage():
    print "python truncate_long_allele_strings.py --mutations-file [path/to/mutations-file]"
    sys.exit(2)

def main():
    usage_statement = '''truncate_long_allele_strings.py: --mutations-file <MUTATIONS_FILE>'''
    parser = argparse.ArgumentParser(usage = usage_statement)
    parser.add_argument("-f", "--mutations-file", help = "mutations file / MAF", required = True)
    args = parser.parse_args()
    mutations_file = args.mutations_file
    if not os.path.exists(mutations_file):
        ERROR_FILE.write("mutations file not found: " + mutations_file + "\n")
        usage()
    lines_to_write = get_truncated_records_to_write(mutations_file)
    with open(mutations_file + ".truncated", "w") as f:
        for line in lines_to_write:
            f.write(line + "\n")

if __name__ == '__main__':
    main()
