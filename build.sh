#!/bin/bash

gradle clean jar jpi
cp build/libs/deployment-pipeline-plugin.hpi ../jenkins-bootstrap/docker_build/plugins/
cp build/libs/deployment-pipeline-plugin-0.0.1-SNAPSHOT.jar ../jenkins-bootstrap/docker_build/jarfiles
