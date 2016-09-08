package com.pearson.deployment.config.bitesize

import spock.lang.*
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
        
        def firstApp = cfg.applications.first()
        firstApp.runtime == "nginx"
        firstApp.dependencies.size() == 3
        firstApp.command == 'nginx -g "daemon off;"'
    }

    def "invalid config" () {
        when:
        def cfg = ApplicationBitesize.readConfigFromString(eInvalid)
        then:
        ConstructorException ex = thrown()
    }
}