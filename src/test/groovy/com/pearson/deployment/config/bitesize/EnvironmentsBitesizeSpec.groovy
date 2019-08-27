package com.pearson.deployment.config.bitesize

import spock.lang.*
import org.yaml.snakeyaml.constructor.ConstructorException

class EnvironmentsBitesizeSpec extends Specification {
    String e
    String eInvalid

    def setup() {
        e = new File("src/test/resources/config/environments.bitesize").text
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
        def stagingEnvironment = cfg.getEnvironment('Staging')

      then:
        cfg.project == "example"
        cfg.environments.size() == 2
        cfg.environments[0].services[0].port == "80"
        cfg.environments[0].services[0].application == 'sample-app'
        cfg.environments[0].services[0].ssl == true
        cfg.environments[1] == stagingEnvironment
        cfg.environments[1].services[2].health_check.command[0] == "/bin/cat"
        cfg.environments[1].services[2].ports == [80,81,82]

        if (cfg.environments[1].services[2].isBVTEnabled()) {
            cfg.environments[1].services[2].bvt_commands.commands[0] == "curl http://localhost:8080"
        }
        if (cfg.environments[1].services[2].isTableauEnabled()) {
            cfg.environments[1].services[2].tableau_commands.commands[1]  == "curl http://localhost:8080"
        }
     }

    def "invalid config" () {
      when:
        def cfg = EnvironmentsBitesize.readConfigFromString(eInvalid)

      then:
        ConstructorException ex = thrown()
    }

    def "we can get environment" () {
      given:
        def cfg = EnvironmentsBitesize.readConfigFromString(e)

      when:
        def staging = cfg.getEnvironment('Staging')
      then:
        cfg.environments[1] == staging

      when:
        def z = cfg.getEnvironment('Zoro')
      then:
        EnvironmentNotFoundException ex = thrown()
        ex.message == 'Environment Zoro not found'

    }

}
