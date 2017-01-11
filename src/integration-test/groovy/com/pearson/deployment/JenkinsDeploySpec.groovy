package com.pearson.deployment


import spock.lang.*
import io.fabric8.kubernetes.client.*
import geb.spock.GebReportingSpec

import io.fabric8.kubernetes.api.model.*

// import org.yaml.snakeyaml.Yaml
// import groovy.json.*

// import org.junit.rules.TemporaryFolder

import com.pearson.deployment.util.*

// This class makes sure Jenkins is deployed with latest version
// and then upgraded to the newer version. Make sure nothing breaks
// in the process

class JenkinsDeploySpec extends GebReportingSpec {

  def kubeClient
  def jenkinsChartFolder
  KubeUtils kube

  private def requiredNamespaces = [
    "sample-app",
    "sample-app-dev",
    "sample-app-stg",
    "sample-app-prd"
  ]

  // def destroyNamespaces() {
  //   requiredNamespaces.each { nsName ->
  //     kubeClient.namespaces().withName(nsName).delete()
  //   }
  // }

  def setup() {
    kube = new KubeUtils()
    kube.createNamespaces(requiredNamespaces)
    // Deploy fake git repository with sample-app bitesize files
    kube.deployGogs(requiredNamespaces.first())
    kube.deployJenkins(requiredNamespaces.first())
    // Deploy jenkins
  }

  def cleanup() {
    kube.cleanup()
  }

  // Check that Jenkins credentials work

  // Check that seed-job created and runs

  // Check the list of generated jobs

  def "some weird stuff" () {
    when:
      def a = 1
    then:
      1 == 2
  }

}
