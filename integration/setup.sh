#!/bin/bash

# render_template changes %%VAR%% in
# the supplied file with $VAR local variables
function render_template() {
  tmpl=$(cat $1 |sed -e 's/%%\([^%]*\)%%/${\1}/g;s///;s/"/\\"/g')
  eval "echo \"$tmpl\""
}


#------------------------------
# TEST-APP related functions
#------------------------------

# Creates test-app pre-requisites in cluster
function test_app::prerequisites() {
  for ns in sample-app-dev sample-app-stg sample-app-prd ; do
    kubectl create ns ${ns}
  done
}

#------------------------------
# Jenkins related functions
#------------------------------
function jenkins::create() {
  cd ${CHARTS_NAME}/jenkins/templates
	NAMESPACE="sample-app"
	JENKINS_ADMIN_USER="admin"
	JENKINS_ADMIN_PASSWORD="password"
	SEED_JOBS_REPO="git@bitbucket.org:pearson-techops/sample-app.git"
	GIT_PRIVATE_KEY="-----BEGIN RSA PRIVATE KEY-----MIIEowIBAAKCAQEAplT2LljLSNoHSVKufGrULbCkiXfjO54kA1Gr4Zqcr3vOxgXs504qi3UiuYqTskEiFo4J0WKkgh7az2CyuoWX0BlLABh4/3qIy6FKlAslsP0WFIozq0k4YFZrgeZYW4a/VNaCJaxJErGoX1L/rvUA4vdm5oTAS9+0lbEQqHUf/xlUziOuWDoGZtp+s+mSwIpwvSx5CwRFhGu+zEcFGnBu50B+GA4Tk3eU+yKLfV+VVluUd036ioOipBUTMzq0Kk77ds/oC7q5UFGZFrow1mqm51QPC7KlQfg6PR4wgBOAt3U5Yh092UBb/VNnHtF6ANDY7q0DcZLToba2QaPCcBdZHQIDAQABAoIBABmFn+Csr0SWb9fIYA2nNrdsC1MsmiljspLvzTfoyXIDxU6NSjSTweN0Lcfag58ub1SWptsCD8wHf/uiEsqcBRdhSijRmH9bBfJ45UCiFT57FxA/xH3b1lKx7/0WoygoHOOfpoixTuUXbYZy7diir2gkHh3w8LCzurndWXj/v9EcinjdFei+qPujtgnjb2JVJeXYZJnl3UhgBsE1O7VDZojEcYsJJErZ2CBQTFr7uQuwAb/1+8xsdjwUiWTVhHLmcdwiQlNUBXb7ovH5IaPj12LpHuG+1QI6IE+c/fm43VMGvMGeHIJN2uvkfNJDPuEUc2gmWi95YYE8NdRhPPbbweECgYEA3DsB1kQ+jNQZLVWKTjkD4mADSbC5t9TKKbSxob2qNLM/REzYhBcrIo6WqeSum0V9IpEuscVpmwIY8D35v7iLU+ND59fV94gz/ZChf9WWODPJ7hk90flbDNkLMhRhSKjnjstKtzIGNOuuzI+MT0TaWbcHxRBNk8v/APSNEGjob9sCgYEAwVjll5K+8ayAGqU1SAxmXz4LY25kCCFXePSn/+m6ucqjE4Tg2vEK/xkJA1IE/FI3gmVqiuXiXPPJFRMEByuhudcOlfQws6cYQ2Zs6hqOFiQmflqVpW7sijciVGXRz3sAIOgCbwSljULxkaFk6KNwX/C6KUE0hjwDSilEsDFSiGcCgYBcS/aHH+6/ApzSnQjro4V3OBnopkIPQwS+2dWgHWbZ001uiG4tXbBcOTn3Vbm/pS2+cO4K3ttVdVmSRccHjIYH0DVDUvd/V/vCBv1IH/Fm+H1mZfm8qC56gfPpNxVWaEMzWwujHznur9+AJ6D8D7Ua6FQ/SIfaqyHh+pusButzBQKBgEjqCHN34NBtGEZ0JTrNilHCqTLPL6QtRrqTsUN+Vm+4bnVu+Ak5O8dmHpME/GJXcx2Wt244MXySZzOuXyeBGdVmt9ZvJ2qoyiqo1swWRZ0t9uJRsPuZcFTTY/vmBhCztyGxvTE21GQIaedcsRxWfot/0gS61V8GBMuT8pZrk09jAoGBAJPHLRTz07yBBog2e1MfUOvQQF44VrYbnegIfbzlVp3P112SsO4sUqjT6lI00UAUunXfG79FoUSmPQuCfJ9iQrwFYnBTw5X8p8T2TzCrdNluXrp53Tbfp02JFEX9xOEZMZH9Ykp0gt1OTNjVBz00Thaj+jnuec5vITDc6HqJSJQq-----END RSA PRIVATE KEY-----"
	JENKINS_HOST="jenkins.sample-app.io"

  JENKINS_IMAGE="${DOCKER_REGISTRY}/geribatai/jenkins:$1"

  ### POPULATE kubernetes yaml files from yaml.tmpl ###
  for i in jenkins-deployment jenkins-ingress jenkins-svc; do
    render_template ${i}.yaml.tmpl > ${i}.yaml
  done

  kubectl apply -f .
  cd -
}

# INPUTS:
# JENKINS_PREVIOUS_VERSION - Jenkins version to build
# JENKINS_CURRENT_IMAGE - Jenkins image to deploy

CHARTS_NAME="kubernetes-charts"
CHARTS_REPO="git@github.com:pearsontechnology/${CHARTS_NAME}.git"
DOCKER_REGISTRY="bitesize-registry.default.svc.cluster.local:5000"


if [ $# -ne 2 ] ; then
  echo "Usage: $0 <previous_jenkins_version> <new_jenkins_version>"
  exit 1
fi

[ -d kubernetes-charts ] && rm -rf kubernetes-charts

# Download kubernetes-charts/jenkins templates from github
git clone ${CHARTS_REPO}
test_app::prerequisites
jenkins::create $1

# --- wait for test-app to be deployed

# --- deploy $2 jenkins image

# --- run whatever tests you need to run

# Create pre-requisites for test-app/sample-app project in the cluster
# Build charts templates with the latest jenkins build and deploy it to kubernetes cluster
# -- setup geribatai/jenkins:${current}
# cd ../test-app && kubectl apply -f . # -- what are pre-requisites?
# Run through integration test scenarios
