package com.pearson.deployment.config.kubernetes

import spock.lang.*

class KubePortSpec extends Specification {

  @Unroll
  def "comparison" () {
    given:
      def orig = new KubePort(containerPort: origPort)
      def mod  = new KubePort(containerPort: otherPort)
      
    expect:
     ( orig == mod ) == expectation

    where:
      origPort | otherPort | expectation
      80       | 81        | false
      81       | 81        | true
      null     | 80        | false
     
  }
}