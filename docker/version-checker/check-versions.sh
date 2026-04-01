#!/usr/bin/env bash

#set -euo pipefail

source /etc/version-checker/projects.conf


echo "==== Version check $(date -Iseconds) ===="


get_token() {
    RESPONSE=$(
        curl -s -k \
            -X POST "${TOKEN_URL}" \
            -H "Content-Type: application/x-www-form-urlencoded" \
            -d "grant_type=password" \
            -d "client_id=${CLIENT_ID}" \
            -d "client_secret=${CLIENT_SECRET}" \
            -d "username=${USERNAME}" \
            -d "password=${PASSWORD}"
    )

    TOKEN=$(echo "${RESPONSE}" | jq -r '.access_token')

    echo "${TOKEN}"
}


get_github_version() {
    local repo=$1

    echo "Fetching Github release for ${repo}" >&2

    RESPONSE=$(curl -s \
        -H "Accept: application/vnd.github+json" \
        "https://api.github.com/repos/${repo}/releases/latest")

    VERSION=$(echo "${RESPONSE}" | jq -r '.tag_name // empty')

    if [ -z "${VERSION}" ]; then
        echo "Unable to retrieve Github version for ${repo}"
        return 1
    fi

    echo "${VERSION}"
}


get_current_version() {
    local project_name=$1

    curl -s -k \
        -H "Authorization: Bearer ${TOKEN}" \
        "${API_URL}/deployments/last?teamKey=${DEFAULT_TEAM}&projectName=${project_name}&environment=${DEFAULT_ENVIRONMENT}" \
        | jq -r '.content[0].version // empty'
}


update_version() {
    local project_org=$1
    local project_name=$2
    local version=$3

    echo "==== Updating deployment ===="
    echo "Team: ${DEFAULT_TEAM}"
    echo "Environment: ${DEFAULT_ENVIRONMENT}"
    echo "Project: ${project_name}"
    echo "Organization: ${project_org}"
    echo "Version: ${version}"
    echo "Client: ${DEFAULT_CLIENT}"
    echo "============================="

    RESPONSE=$(curl -s -k \
        -w "\nHTTP_STATUS:%{http_code}" \
        -X POST \
        "${API_URL}/deployments/stop" \
        -H "Authorization: Bearer ${TOKEN}" \
        -H "Content-Type: application/json" \
        -d "{
            \"teamKey\": \"${DEFAULT_TEAM}\",
            \"environment\": \"${DEFAULT_ENVIRONMENT}\",
            \"projectOrganization\": \"${project_org}\",
            \"projectName\": \"${project_name}\",
            \"version\": \"${version}\",
            \"client\": \"${DEFAULT_CLIENT}\",
            \"status\": \"COMPLETED\"
        }")

    echo "==== API response ===="
    echo "${RESPONSE}"
    echo "======================"
}


TOKEN=$(get_token)

if [ -z "${TOKEN}" ] || [ "${TOKEN}" = "null" ]; then
    echo "Unable to retrieve Keycloak token"
    exit 1
fi


for entry in ${PROJECTS}; do

    PROJECT=$(echo "$entry" | cut -d'|' -f1)
    REPO=$(echo "$entry" | cut -d'|' -f2)


    echo "Checking ${PROJECT}"


    LATEST=$(get_github_version "${REPO}")

    if [ "${LATEST}" = "null" ] || [ -z "${LATEST}" ]; then
        echo "Unable to retrieve Github version for ${REPO}"
        continue
    fi


    CURRENT=$(get_current_version "${PROJECT}")


    echo "Current=${CURRENT} Latest=${LATEST}"


    if [ "${CURRENT}" != "${LATEST}" ]; then

        echo "New version detected"

        update_version \
            "Github" \
            "${PROJECT}" \
            "${LATEST}"

    else
        echo "Already up to date"
    fi

done