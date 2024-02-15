#!/bin/sh

ENV=${1:-dev}
VAULT_TOKEN=${2:-$(cat "$HOME"/.vault-token)}

VAULT_ADDR="https://clotho.broadinstitute.org:8200"

VAULT_COMMAND="vault read"

# use SERVICE_OUTPUT_LOCATION to add any service specific secrets
SERVICE_OUTPUT_LOCATION="$(dirname "$0")/../service/src/main/resources/rendered"
INTEGRATION_OUTPUT_LOCATION="$(dirname "$0")/../integration/src/main/resources/rendered"

if ! [ -x "$(command -v vault)" ]; then
  VAULT_COMMAND="docker run --rm -e VAULT_TOKEN=$VAULT_TOKEN -e VAULT_ADDR=$VAULT_ADDR vault:1.7.3 $VAULT_COMMAND"
fi

$VAULT_COMMAND -field=data -format=json "secret/dsde/firecloud/$ENV/common/firecloud-account.json" >"$INTEGRATION_OUTPUT_LOCATION/user-delegated-sa.json"

# We use the perf testrunner account in all environments.
PERF_VAULT_PATH="secret/dsde/terra/kernel/perf/common"
$VAULT_COMMAND -field=key "$PERF_VAULT_PATH/testrunner/testrunner-sa" | base64 -d > "$INTEGRATION_OUTPUT_LOCATION/testrunner-perf.json"



SECRET_ENV_VARS_LOCATION="${SERVICE_OUTPUT_LOCATION}/secrets.env"
gcloud container clusters get-credentials --zone us-central1-a --project broad-dsde-dev terra-dev

# Secrets needed to talk to the Google Directory API
kubectl -n terra-dev get secret admin-one-sa-secret -o 'go-template={{index .data "admin-service-account-1.json"}}' | base64 --decode > ${SERVICE_OUTPUT_LOCATION}/admin-service-account-1.json
kubectl -n terra-dev get secret admin-two-sa-secret -o 'go-template={{index .data "admin-service-account-2.json"}}' | base64 --decode > ${SERVICE_OUTPUT_LOCATION}/admin-service-account-2.json
kubectl -n terra-dev get secret admin-three-sa-secret -o 'go-template={{index .data "admin-service-account-3.json"}}' | base64 --decode > ${SERVICE_OUTPUT_LOCATION}/admin-service-account-3.json
kubectl -n terra-dev get secret admin-four-sa-secret -o 'go-template={{index .data "admin-service-account-4.json"}}' | base64 --decode > ${SERVICE_OUTPUT_LOCATION}/admin-service-account-4.json
kubectl -n terra-dev get secret admin-five-sa-secret -o 'go-template={{index .data "admin-service-account-5.json"}}' | base64 --decode > ${SERVICE_OUTPUT_LOCATION}/admin-service-account-5.json
