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
  JenkinsAPI jenkins

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
    def e = System.getenv()

    // Do cleanup first
    kube.deleteNamespaces(requiredNamespaces)
    kube.createNamespaces(requiredNamespaces)
    // Deploy fake git repository with sample-app bitesize files
    kube.deployGogs(requiredNamespaces.first())
    kube.deployJenkins(requiredNamespaces.first())

    def adminUser = e.JENKINS_ADMIN_USER ?: 'admin'
    def adminPassword = e.JENKINS_ADMIN_PASSWORD ?: 'pass'
    def adminUrl = e.JENKINS_URL ?: 'http://jenkins.sample-app.io/'

    jenkins = new JenkinsAPI(adminUrl, adminUser, adminPassword)
    // Deploy jenkins
  }

  def cleanup() {
    kube.cleanup()
  }

  def "check Jenkins credentials" () {
    def response = jenkins.getHomePage()
    response.status == 200
  }

}
