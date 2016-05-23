package com.pearson.deployment

import com.pearson.deployment.kubernetes.*
import com.pearson.deployment.config.*

import spock.lang.*
import groovy.mock.interceptor.MockFor

class KubeResourceSpec extends Specification {

  def "test init" () {

  }

  def "test create" () {
    setup:
    def config = [
     applications: [
      [ name: 'sample', version: '1.0']
     ]
    ]

    def resource = new KubeResource('rc','default', config)
    when:
    resource.create()
    then:
    RuntimeException ex = thrown()
    ex.message =~ /No signature of method:/

    // missing version
  }

  def "test update" () {
    // success, failure

  }
}
