package com.pearson.deployment

class EnvironmentConfig extends ConfigReader implements Serializable {
  EnvironmentConfig(String filename) {
    readConfig(filename)

    validate {
      required = [ "project" ]
      attributes = attributes
    }
    
    attributes.environments?.each {
      validate_environment(it)
    }
  }

  private def validate_environment(def env) {
    validate {
      required = [
          "name",
          "namespace",
          "deployment.method",
          "services"
      ]
      attributes = env
    }

    env.tests?.each {
      validate_test it
    }

    env.services?.each {
      validate_service it
    }
  }

  private def validate_test(def tst) {
    validate {
      required = [
        "name",
        "repository",
        "commands"
      ]
      attributes = tst
    }
  }

  private def validate_service(def svc) {
    validate {
      required = [
        "name"
      ]
      attributes = svc
    }
  }

}
