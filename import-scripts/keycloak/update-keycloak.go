// Utility to update roles or user roles via the Keycloak REST API.
//
// Mode 1: ROLE
//./update-keycloak -host="http://localhost:8080" -realm=<realm-name> -username=<user> -password=<pw> -datatype=ROLE <filename>
//
// where ROLE filename is of the form:
//
// ROLE1
// ROLE2
// ROLE3
// ...
// EOF
//
// Note: Composite roles are not supported
//
// Mode 2: USER
//./update-keycloak -host="http://localhost:8080" -realm=<realm-name> -username=<user> -password=<pw> -datatype=USER <filename>
//
// where USER filename is of the form:
//
// userid1\tROLE1:ROLE2
// userid2\tROLE1:...
// ...
// EOF
//
// Note: userid can appear on multiple rows with multiple roles.  Roles are added to existing user, existing roles are not replaced.
//
package main

import (
	"bufio"
	"bytes"
	"encoding/json"
	"flag"
	"fmt"
	"io"
	"io/ioutil"
	"log"
	"net/http"
	"net/url"
	"os"
	"strings"
)

type AccessTokenStruct struct {
	Access_token       string `json:"access_token"`
	Expires_in         int    `json:"expires_in"`
	Refresh_expires_in int    `json:"refresh_expires_in"`
	Refresh_token      string `json:"refresh_token"`
	Token_type         string `json:"token_type"`
	Not_before_policy  int    `json:"not-before-policy"`
	Session_state      string `json:"session_state"`
	Scope              string `json:"scope"`
}

type RoleStruct struct {
	ID          string            `json:"id"`
	Name        string            `json:"name"`
	Description string            `json:"description"`
	Composite   bool              `json:"composite"`
	ClientRole  bool              `json:"clientRole"`
	ContainerID string            `json:"containerId"`
	Attributes  map[string]string `json:"attributes"`
}

// only relevant fields
type UserStruct struct {
	ID       string `json:"id"`
	Username string `json:"username"`
}

func getAccessToken(keycloakURL *url.URL, clientID string, username string, password string) AccessTokenStruct {

	req_data := url.Values{}
	req_data.Set("grant_type", "password")
	req_data.Set("username", username)
	req_data.Set("password", password)
	req_data.Set("client_id", clientID)
	req, _ := http.NewRequest(http.MethodPost, keycloakURL.String(), strings.NewReader(req_data.Encode()))
	req.Header.Set("Accept", "application/json")
	req.Header.Set("Content-Type", "application/x-www-form-urlencoded")

	client := &http.Client{}
	resp, err := client.Do(req)

	if err != nil {
		log.Fatal(err)
	}

	defer resp.Body.Close()

	body, _ := ioutil.ReadAll(resp.Body)
	toReturn := AccessTokenStruct{}
	err = json.Unmarshal(body, &toReturn)
	if err != nil {
		log.Fatal(err)
	}
	return toReturn
}

func buildAdminAccessURL(keycloakURL *url.URL, realm string) *url.URL {

	adminURLString := fmt.Sprintf("%s/auth/realms/%s/protocol/openid-connect/token",
		keycloakURL.String(), realm)
	adminURL, err := url.Parse(adminURLString)
	if err != nil {
		log.Fatal(err)
	}

	return adminURL
}

func updateUserRoles(keycloakURL *url.URL, accessToken AccessTokenStruct, realm string, users map[string][]string) []int {

	tally := make([]int, 2)
	for key, value := range users {
		existingUser := UserStruct{Username: key}
		existingUser = getUser(keycloakURL, accessToken, realm, existingUser)
		if len(existingUser.Username) > 0 {
			roles := getUserRoles(keycloakURL, accessToken, realm, value)
			status := updateUserRole(keycloakURL, accessToken, realm, existingUser, roles)
			if status == "201 Created" || status == "204 No Content" {
				log.Printf("User roles successfully updated: %s\n", key)
				tally[0] += 1
			} else {
				log.Printf("Error updating user roles: %s\n", key)
				log.Printf("HTTP Status: %s\n", status)
				tally[1] += 1
			}
		} else {
			log.Printf("User does not exist, skipping: %s\n", key)
			tally[1] += 1
		}
	}
	return tally
}

func getUserRoles(keycloakURL *url.URL, accessToken AccessTokenStruct, realm string, roles []string) []RoleStruct {

	var toReturn []RoleStruct
	for _, role := range roles {
		roleStruct := getRole(keycloakURL, accessToken, realm, strings.TrimSpace(role))
		toReturn = append(toReturn, roleStruct)
	}

	return toReturn
}

func updateUserRole(keycloakURL *url.URL, accessToken AccessTokenStruct, realm string, user UserStruct, roles []RoleStruct) string {

	roleMappingURL := buildRoleMappingURL(keycloakURL, realm, user)
	requestBody, err := json.Marshal(roles)
	if err != nil {
		log.Fatal(err)
	}
	req, _ := http.NewRequest(http.MethodPost, roleMappingURL.String(), bytes.NewBuffer(requestBody))
	req.Header.Set("Content-Type", "application/json")
	authorization := fmt.Sprintf("Bearer %s", accessToken.Access_token)
	req.Header.Set("Authorization", authorization)

	httpClient := &http.Client{}
	resp, err := httpClient.Do(req)

	if err != nil {
		log.Fatal(err)
	}

	defer resp.Body.Close()
	return resp.Status
}

func getUser(keycloakURL *url.URL, accessToken AccessTokenStruct, realm string, user UserStruct) UserStruct {

	userURL := buildUserURL(keycloakURL, realm)
	req, _ := http.NewRequest(http.MethodGet, userURL.String(), nil)
	req.Header.Set("Accept", "application/json")
	authorization := fmt.Sprintf("Bearer %s", accessToken.Access_token)
	req.Header.Set("Authorization", authorization)
	query := req.URL.Query()
	query.Add("briefRepresentation", "true")
	query.Add("username", user.Username)
	req.URL.RawQuery = query.Encode()

	httpClient := &http.Client{}
	resp, err := httpClient.Do(req)

	if err != nil {
		log.Fatal(err)
	}

	defer resp.Body.Close()

	body, _ := ioutil.ReadAll(resp.Body)
	var toReturn []UserStruct
	err = json.Unmarshal(body, &toReturn)
	if err != nil {
		log.Fatal(err)
	}
	if len(toReturn) == 1 {
		return toReturn[0]
	} else {
		log.Printf("Multiple user records matched, skipping:%s\n", user.Username)
		existingUser := UserStruct{Username: ""}
		return existingUser
	}
}

func buildUserURL(keycloakURL *url.URL, realm string) *url.URL {

	userURLString := fmt.Sprintf("%s/auth/admin/realms/%s/users", keycloakURL.String(), realm)
	userURL, err := url.Parse(userURLString)
	if err != nil {
		log.Fatal(err)
	}

	return userURL
}

func buildRoleMappingURL(keycloakURL *url.URL, realm string, user UserStruct) *url.URL {

	roleMappingURLString := fmt.Sprintf("%s/auth/admin/realms/%s/users/%s/role-mappings/realm",
		keycloakURL.String(), realm, user.ID)
	roleMappingURL, err := url.Parse(roleMappingURLString)
	if err != nil {
		log.Fatal(err)
	}

	return roleMappingURL
}

func createRole(roleName string) RoleStruct {
	var role RoleStruct = RoleStruct{}
	role.Name = roleName
	return role
}

func addRoles(keycloakURL *url.URL, accessToken AccessTokenStruct, realm string, roles []string) []int {

	tally := make([]int, 2)
	for _, role := range roles {
		var existingRole RoleStruct = getRole(keycloakURL, accessToken, realm, role)
		if existingRole.Name == "" {
			var roleStruct RoleStruct = createRole(role)
			status := addRole(keycloakURL, accessToken, realm, roleStruct)
			if status == "201 Created" {
				log.Printf("Role successfully added: %s\n", role)
				tally[0] += 1
			} else {
				log.Printf("Error adding role: %s\n", role)
				log.Printf("HTTP Status: %s\n", status)
				tally[1] += 1
			}
		} else {
			log.Printf("Role exists, skipping: %s\n", role)
			tally[1] += 1
		}
	}
	return tally
}

func addRole(keycloakURL *url.URL, accessToken AccessTokenStruct, realm string, role RoleStruct) string {

	roleURL := buildRoleURL(keycloakURL, realm, "")
	requestBody, err := json.Marshal(role)
	if err != nil {
		log.Fatal(err)
	}
	req, _ := http.NewRequest(http.MethodPost, roleURL.String(), bytes.NewBuffer(requestBody))
	req.Header.Set("Content-Type", "application/json")
	authorization := fmt.Sprintf("Bearer %s", accessToken.Access_token)
	req.Header.Set("Authorization", authorization)

	httpClient := &http.Client{}
	resp, err := httpClient.Do(req)

	if err != nil {
		log.Fatal(err)
	}

	defer resp.Body.Close()
	return resp.Status
}

func getRole(keycloakURL *url.URL, accessToken AccessTokenStruct, realm string, role string) RoleStruct {

	roleURL := buildRoleURL(keycloakURL, realm, role)
	req, _ := http.NewRequest(http.MethodGet, roleURL.String(), nil)
	req.Header.Set("Accept", "application/json")
	authorization := fmt.Sprintf("Bearer %s", accessToken.Access_token)
	req.Header.Set("Authorization", authorization)

	httpClient := &http.Client{}
	resp, err := httpClient.Do(req)

	if err != nil {
		log.Fatal(err)
	}

	defer resp.Body.Close()

	body, _ := ioutil.ReadAll(resp.Body)
	toReturn := RoleStruct{}
	err = json.Unmarshal(body, &toReturn)
	if err != nil {
		log.Fatal(err)
	}
	return toReturn
}

func buildRoleURL(keycloakURL *url.URL, realm string, roleName string) *url.URL {

	roleURLString := fmt.Sprintf("%s/auth/admin/realms/%s/roles", keycloakURL.String(), realm)
	if len(roleName) > 0 {
		roleURLString += fmt.Sprintf("/%s", roleName)
	}
	roleURL, err := url.Parse(roleURLString)
	if err != nil {
		log.Fatal(err)
	}

	return roleURL
}

func parseRoleFile(filename string) []string {

	f, err := os.Open(filename)
	defer f.Close()
	if err != nil {
		log.Fatal(err)
	}
	rd := bufio.NewReader(f)

	var roles []string
	for {
		line, err := rd.ReadString('\n')
		if err == io.EOF {
			break
		} else if err != nil {
			log.Fatal(err)
		}
		role := strings.TrimSpace(line)
		if len(role) == 0 {
			continue
		}
		roles = append(roles, role)
	}
	return roles
}

func parseUserFile(filename string) map[string][]string {

	f, err := os.Open(filename)
	defer f.Close()
	if err != nil {
		log.Fatal(err)
	}
	rd := bufio.NewReader(f)
	var user_map = make(map[string][]string)
	for {
		line, err := rd.ReadString('\n')
		if err == io.EOF {
			break
		} else if err != nil {
			log.Fatal(err)
		}
		var parts []string = strings.Split(line, "\t")
		var user = strings.TrimSpace(parts[0])
		if len(parts) == 2 {
			var authorities []string = strings.Split(strings.TrimSpace(parts[1]), ";")
			// check for empty authorities
			if len(authorities) == 1 && len(authorities[0]) == 0 {
				continue
			}
			if user_map[user] == nil {
				user_map[user] = make([]string, 0)
			}
			user_map[user] = append(user_map[user], authorities...)
		}
	}
	return user_map
}

func checkArgs(osArgs []string) (*url.URL, string, string, string, string) {

	if len(osArgs) < 7 {
		fmt.Println("usage: ./update-keycloak [HOSTNAME] [REALM] [USERNAME] [PASSWORD] [DATATYPE] <datafile>")
		fmt.Println("(see https://github.com/knowledgesystems/cmo-pipelines/blob/master/import-scripts/keycloak/update-keycloak.go for more information)")
		os.Exit(1)
	}

	hostPtr := flag.String("host", "", "Keycloak url")
	realmPtr := flag.String("realm", "", "Keycloak realm")
	usernamePtr := flag.String("username", "", "Keycloak username")
	passwordPtr := flag.String("password", "", "Keycloak password")
	datatypePtr := flag.String("datatype", "", "ROLE or USER")

	flag.Parse()

	keycloakURL, err := url.Parse(*hostPtr)
	if err != nil {
		log.Fatal(err)
	}

	if *datatypePtr != "ROLE" && *datatypePtr != "USER" {
		fmt.Println("datatype argument must be ROLE or USER")
		os.Exit(1)
	}

	return keycloakURL, *realmPtr, *usernamePtr, *passwordPtr, *datatypePtr
}

func main() {

	keycloakURL, realm, username, password, datatype := checkArgs(os.Args)
	adminURL := buildAdminAccessURL(keycloakURL, realm)
	var accessToken AccessTokenStruct = getAccessToken(adminURL, "admin-cli", username, password)
	if datatype == "ROLE" {
		roles := parseRoleFile(os.Args[6])
		tally := addRoles(keycloakURL, accessToken, realm, roles)
		log.Printf("import roles finished: %v role(s) successfully imported, %v role(s) skipped or failed to import\n",
			tally[0], tally[1])
	} else {
		users := parseUserFile(os.Args[6])
		tally := updateUserRoles(keycloakURL, accessToken, realm, users)
		log.Printf("Update users finished: %v user(s) successfully updated, %v user(s) skipped or failed to update\n",
			tally[0], tally[1])
	}
}
