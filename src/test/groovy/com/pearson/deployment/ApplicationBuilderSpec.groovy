package com.pearson.deployment

import spock.lang.*
import groovy.mock.interceptor.MockFor
import org.junit.Rule
import org.junit.rules.TemporaryFolder


import com.pearson.deployment.kubernetes.*
import com.pearson.deployment.config.*

class ApplicationBuilderSpec extends Specification {

  def dummyFile = new MockFor(File)
  @Rule final TemporaryFolder configDir = new TemporaryFolder()

  File config
  ApplicationBuilder builder

  def setup() {
    def applicationConfig = """
    project: sample
    applications:
      - name: sample-application
        runtime: nginx:1.9.11
        version: 0.0.1
    """

    config = configDir.newFile('application.bitesize')
    config.write applicationConfig

    builder = new ApplicationBuilder(config.path)
  }

  def "initialization" () {
    expect:
    builder.project == "sample"
    builder.applications.size() == 1
  }

  def "getApplication() success" () {
    when:
    def app = builder.getApplication('sample-application')
    then:
    app.version == '0.0.1'
  }

  def "getApplication() fail" () {
    when:
    def app = builder.getApplication('nonexistent')
    then:
    app == null
  }
}
