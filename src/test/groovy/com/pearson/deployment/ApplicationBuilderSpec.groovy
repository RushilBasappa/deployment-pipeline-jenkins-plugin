package com.pearson.deployment

import spock.lang.*
import groovy.mock.interceptor.MockFor

class ApplicationBuilderSpec extends Specification {

  def dummyFile = new MockFor(File)

  def setup() {
    def applicationConfig = """
    project: sample
    applications:
      - name: sample-application
        runtime: nginx:1.9.11
        version: 0.0.1
    """

    dummyFile.demand.getText { applicationConfig }

  }

  def "test initialization" () {
    when:
    dummyFile.use {
      def n =  new ApplicationBuilder("/tmp/something")
      assert n.project == "sample"
      assert n.appDefinition.attributes.applications.size() == 1
    }
    then:
    dummyFile.expect.verify()
  }

  def "test getApplication()" () {
    when:
    dummyFile.use {
      def n = new ApplicationBuilder("/tmp/something")
      def app = n.getApplication('sample-application')
      assert app.version == '0.0.1'
    }
    then:
    dummyFile.expect.verify()
  }

  def "test dockerRegistry()" () {
    when:
    dummyFile.use {
      def n = new ApplicationBuilder("/tmp/something")
      assert n.dockerRegistry() == "bitesize-registry.default.svc.cluster.local:5000"
    }
    then:
    dummyFile.expect.verify()

  }
}
