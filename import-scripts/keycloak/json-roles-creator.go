// Takes a dump of the cancer study table (study identifier and group cols only)
// and creates a keycloak-roles json compatible file for import.
// Non-composite roles are created from cancer study identifiers
// and composite roles are created from cancer study groups.
//
// Usage:
// ./json-roles-creator.go [cancer study group file]
//
// Example records:
//
// padd_mskcc_basturk_2013\tBERGERM
// aml_mskcc_leviner_5457_c\tLEVINER;COMPONC;SHIHA;PARKS2;VUL
//
package main

import (
	"bufio"
	"fmt"
	"io"
	"log"
	"os"
	"strings"
)

func genRoleJSON(cancer_studies []string, groups map[string][]string) {
	header := `{
	"roles": {
		"realm": [
`
	fmt.Print(header)

	for _, study := range cancer_studies {
		fmt.Println("\t\t\t{")
		fmt.Printf("\t\t\t\t\"name\": \"%s\",\n", study)
		fmt.Println("\t\t\t\t\"composite\": false,")
		fmt.Println("\t\t\t\t\"clientRole\": false,")
		fmt.Println("\t\t\t\t\"attributes\": {}")
		fmt.Println("\t\t\t},")
	}
	var map_index = 0
	for key, value := range groups {
		var role_comma = ","
		map_index++
		if map_index == len(groups) {
			role_comma = ""
		}
		fmt.Println("\t\t\t{")
		fmt.Printf("\t\t\t\t\"name\": \"%s\",\n", key)
		fmt.Println("\t\t\t\t\"composite\": true,")
		fmt.Println("\t\t\t\t\"composites\": {")
		fmt.Println("\t\t\t\t\t\"realm\": [")
		for lc, study := range value {
			var study_comma = ","
			if lc == len(value)-1 {
				study_comma = ""
			}
			fmt.Printf("\t\t\t\t\t\t\"%s\"%s\n", study, study_comma)
		}
		fmt.Println("\t\t\t\t\t]")
		fmt.Println("\t\t\t\t},")
		fmt.Println("\t\t\t\t\"clientRole\": false,")
		fmt.Println("\t\t\t\t\"attributes\": {}")
		fmt.Printf("\t\t\t}%s\n", role_comma)
	}

	footer := `		]
	}
}
`
	fmt.Print(footer)
}

func main() {

	if len(os.Args) < 2 {
		fmt.Println("usage: ./json-roles-creator [cancer study group file]")
		fmt.Println("(see https://github.com/knowledgesystems/cmo-pipelines/blob/master/import-scripts/keycloak/json-roles-creator.go for more information)")
		os.Exit(1)
	}

	// open file into buffered reader
	f, err := os.Open(os.Args[1])
	defer f.Close()
	if err != nil {
		log.Fatal(err)
	}
	rd := bufio.NewReader(f)

	// read each line of file, create array of roles and map of group
	var cancer_studies []string
	var group_map = make(map[string][]string)
	for {
		line, err := rd.ReadString('\n')
		if err == io.EOF {
			break
		} else if err != nil {
			log.Fatal(err)
		}

		// do something with line
		var parts []string = strings.Split(line, "\t")
		var cancer_study = strings.TrimSpace(parts[0])
		cancer_studies = append(cancer_studies, cancer_study)
		if len(parts) == 2 {
			for _, group := range strings.Split(parts[1], ";") {
				group = strings.TrimSpace(group)
				if group_map[group] == nil {
					group_map[group] = make([]string, 0)
				}
				group_map[group] = append(group_map[group], cancer_study)
			}
		}
	}

	genRoleJSON(cancer_studies, group_map)
}
