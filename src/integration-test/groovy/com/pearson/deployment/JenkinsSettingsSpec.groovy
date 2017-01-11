package com.pearson.deployment

import spock.lang.*

import geb.spock.GebReportingSpec
import groovyx.net.http.RESTClient
import spock.lang.Shared
import spock.lang.Stepwise

import com.pearson.deployment.jenkins.JenkinsAPI

class JenkinsSettingsSpec extends GebReportingSpec {
  @Shared
  JenkinsAPI jenkins

  def setup() {
    def e = System.getenv()
    jenkins = new JenkinsAPI('http://jenkins.sample-app.io/', e.JENKINS_ADMIN_USER, e.JENKINS_ADMIN_PASSWORD)
    jenkins.waitForAvailable()
  }

  def  "seed-job passes" () {
    when:
      def response = jenkins.waitForBuildToFinish('seed-job')
    then:
      response.data.result == 'SUCCESS'
  }

  def "service-manage passes" () {
    when:
      def response = jenkins.waitForBuildToFinish('service-manage')
    then:
      response.data.result == 'SUCCESS'
  }

  def "Check that package is deployed to dev environment" () {
    // Select specific package
    // see that -build is ok
    //  see that -docker-image is ok
    //  see that -deploy is ok
    //  see that deployment is created in kubernetes cluster
  }

  // See that service-manage does not overwrite what it doesn't need to

  // We need a check to verify jenkins deployment to environment...

  // Check that .bitesize file change in git corresponds to build trigger & deploy

}
