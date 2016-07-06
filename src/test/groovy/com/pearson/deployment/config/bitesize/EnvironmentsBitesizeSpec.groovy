package com.pearson.deployment.config.bitesize

import spock.lang.*
import groovy.mock.interceptor.MockFor
import org.junit.Rule
import org.junit.rules.TemporaryFolder

import java.io.*
import java.lang.*

import org.yaml.snakeyaml.constructor.ConstructorException

class EnvironmentsBitesizeSpec extends Specification {
    String e
    String eInvalid
    String eSampleApp

    def setup() {
        e = """
        project: sample
        environments:
          - name: development
            namespace: development
            deployment:
              method: rolling-rolling
              timeout: 3000
            services:
              - name: myservice
                ssl: true
                external_url: www.google.co.uk 
        """

        eSampleApp = """
project: example
environments:
  - name: Development
    namespace: sample-app-dev
    next_environment: Staging
    deployment:
      method: rolling-upgrade
      timeout: 300
    services:
      - name: test-service
        application: sample-app
        external_url: test-service-dev.pearson.com
        #ssl: true
        port: 80
        env:
          - name: NODE_ENV
            value: production
          - name: SOMETHING
            value: new_value
          - name: BOO
            value: ok
          - name: tos_url
            value: nononono
      - name: second-service
        application: sample-app
        external_url: test-service2-dev.pearson.com
        port: 80
      - name: awesomedb
        type: mysql
        version: 5.6
    tests:
      - name: Sample test
        repository: git@github.com/sample-test.git
        branch: master
        commands:
          - shell: rake test
  - name: Staging
    namespace: sample-app-stage
    deployment:
      method: rolling-upgrade
      mode: manual
      timeout: 300
    services:
      - type: mysql
        name: db
        version: 0.1
      - type: mongo
        name: mongodb
        version: 0.1
      - name: test-service
        application: sample-app
        external_url: test-service-stg.pearson.com
        replicas: 2
        port: 80
        env:
          - name: NODE_ENV
            value: staging
    tests:
      - name: Sample test
        repository: git@github.com/sample-test.git
        commands:
          - shell: ls
        """

        eInvalid = """
        project: sss
        environments:
          -name: development
          namespace: ooo
        """
    }

    def "valid config" () {
        when:
        def cfg = EnvironmentsBitesize.readConfigFromString(e)
        then:
        cfg.project == "sample"
        cfg.environments.size() == 1
        cfg.environments[0].services[0].port == 80
        cfg.environments[0].services[0].application == 'myservice'
        cfg.environments[0].services[0].ssl == true

    }

    def "invalid config" () {
        when:
        def cfg = EnvironmentsBitesize.readConfigFromString(eInvalid)
        then:
        ConstructorException ex = thrown()
    }

    def "sample app test" () {
        when:
        def cfg = EnvironmentsBitesize.readConfigFromString(eSampleApp)
        then:
        cfg.project == "example"
    }
}