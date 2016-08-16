package com.pearson.deployment.config.bitesize

import spock.lang.*

import org.yaml.snakeyaml.constructor.ConstructorException

class BuildBitesizeSpec extends Specification {
  String e
  String eInvalid
  String eSampleApp

  def setup() {
      e = new File("src/test/resources/config/build.bitesize").text

      eInvalid = """
      project: sss
      components:
        -name: development
        namespace: ooo
      """
  }

  def "valid config" () {
    when:
      def cfg = BuildBitesize.readConfigFromString(e)

    then:
      cfg.project == "example"
      cfg.components.size() == 2

      def firstComponent = cfg.components.first()
      firstComponent.name == "static-content"
      firstComponent.version == "1.1.2"
  }

  def "invalid config" () {
    when:
      def cfg = BuildBitesize.readConfigFromString(eInvalid)
    then:
      ConstructorException ex = thrown()
  }
}