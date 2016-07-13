package com.pearson.deployment.config.bitesize

import spock.lang.*
import groovy.mock.interceptor.MockFor
import org.junit.Rule
import org.junit.rules.TemporaryFolder

import java.io.*
import java.lang.*

import org.yaml.snakeyaml.constructor.ConstructorException

class ApplicationBitesizeSpec extends Specification {
    String e
    String eInvalid

    def setup() {
        e = new File("src/test/resources/config/application.bitesize").text
        eInvalid = """
        project: sss
        applications:
          -name: development
          namespace: ooo
        """
    }

    def "valid config" () {
      when:
        def cfg = ApplicationBitesize.readConfigFromString(e)

      then:
        cfg.project == "example"
        cfg.applications.size() == 2
        
        def first_app = cfg.applications.first()
        first_app.runtime == "nginx"
        first_app.dependencies.size() == 2
        first_app.command == 'nginx -g "daemon off;"'
    }

    def "invalid config" () {
        when:
        def cfg = ApplicationBitesize.readConfigFromString(eInvalid)
        then:
        ConstructorException ex = thrown()
    }
}