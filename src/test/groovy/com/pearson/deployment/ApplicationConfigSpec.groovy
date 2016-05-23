package com.pearson.deployment


import com.pearson.deployment.kubernetes.*
import com.pearson.deployment.config.*


import spock.lang.*
import groovy.mock.interceptor.MockFor

class ApplicationConfigSpec extends Specification {

  def "test validation" () {
    def config = """
    applications:
      - name: something
    """
    // missing version
  }
}
