package com.pearson.deployment.config

// import hudson.model.*


class EnvironmentConfig extends ConfigReader implements Serializable {
  EnvironmentConfig(String filename) {
    readConfig(filename)

    attributes.environments?.each {
      validate_environment(it)
    }
  }

  EnvironmentConfig(String contents, boolean dummy) {
    this.attributes = loadYaml(contents)

    attributes.environments?.each {
      validate_environment(it)
    }

  }

  def getEnvironment(String name) {
    attributes?.environments?.find{ it.name == name }
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
