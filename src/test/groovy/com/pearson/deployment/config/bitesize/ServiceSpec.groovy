package com.pearson.deployment.config.bitesize

import spock.lang.*

class ServiceSpec extends Specification {

  @Unroll
  def "Service comparison" () {
    given:
      Service original = new Service(name : "ENV")

    expect:
      original.equals(matcher) == expected

    where:
      matcher                                       | expected
      new Service(name: "ENV")                      | true
      null                                          | false
      new Service(name: "OTHER")                    | false
      [ name: "ENV", value: "SOMEVALUE"]            | false
  }

  @Unroll
  def "Service attribute #key set to #attr value is #expected" () {
    when:
      def s = new Service()
      s."${key}" = attr

    then:
      s."${key}" == expected

    where:
      key         | attr  | expected
      "sslString" | null  | "false"
      "sslString" | "true"  | "true"
      "sslString" | "false" | "false"
  }


  def "isThirdparty method checks type" () {
    when:
      Service s = new Service( type: typeValue)

    then:
      s.isThirdParty() == expected

    where:
      typeValue | expected
      'custom'  | true
      null      | false
  }

  def "DeploymentMethod matches environments method" () {
    when:
      def e = new File("src/test/resources/config/environments.bitesize").text
      def cfg = EnvironmentsBitesize.readConfigFromString(e)
      def stagingEnvironment = cfg.getEnvironment('Staging')

      def testAppSvc = stagingEnvironment.services[2]
      testAppSvc.setupDeploymentMethod(stagingEnvironment)

    then:

      testAppSvc.deployment?.method == "bluegreen"
      testAppSvc.deployment?.mode == "manual"
      testAppSvc.deployment?.active == "blue"
  }

  def "DeploymentMethod matches service method" () {
    when:
      def e = new File("src/test/resources/config/environments.bitesize").text
      def cfg = EnvironmentsBitesize.readConfigFromString(e)
      def devEnvironment = cfg.getEnvironment('Development')

      def testAppSvc = devEnvironment.services[1]
      testAppSvc.setupDeploymentMethod(devEnvironment)

    then:

      testAppSvc.deployment?.method == "bluegreen"
      testAppSvc.deployment?.mode == "manual"
      testAppSvc.deployment?.active == "green"
  }

  def "DeploymentMethod overrides environments method" () {
    when:
      def e = new File("src/test/resources/config/environments.bitesize").text
      def cfg = EnvironmentsBitesize.readConfigFromString(e)
      def stagingEnvironment = cfg.getEnvironment('Staging')

      def customAppSvc = stagingEnvironment.services[3]
      customAppSvc.setupDeploymentMethod(stagingEnvironment)
    then:
      customAppSvc.deployment?.method == "bluegreen"
      customAppSvc.deployment?.mode == "auto"
      customAppSvc.deployment?.active == "green"

  }
}
