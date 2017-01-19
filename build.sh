#!/bin/bash

set -e

VERSION=${1:-0.0.1}
gradle test
gradle clean jar jpi
cp build/libs/deployment-pipeline-plugin.hpi ../jenkins-bootstrap/docker_build/plugins/
cp build/libs/deployment-pipeline-plugin-${VERSION}-SNAPSHOT.jar ../jenkins-bootstrap/docker_build/jarfiles
