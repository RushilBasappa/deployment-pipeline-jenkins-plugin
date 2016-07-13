package com.pearson.deployment.config.bitesize

import spock.lang.*

class EnvVarSpec extends Specification {

  def "EnvVar comparison" () {
    given:
      EnvVar original = new EnvVar(name: "ENV", value: "SOMEVALUE")
    
    expect:
      original.equals(matcher) == expected

    where:
      matcher                                       | expected
      new EnvVar(name: "ENV", value: "SOMEVALUE")   | true
      null                                          | false
      new EnvVar(name: "ENV", value: "OTHER")       | false
      new EnvVar(name: "OTHER", value: "SOMEVALUE") | false
      [ name: "ENV", value: "SOMEVALUE"]            | false
  }
}