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
}