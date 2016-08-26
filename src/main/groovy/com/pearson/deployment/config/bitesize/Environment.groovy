package com.pearson.deployment.config.bitesize

import org.yaml.snakeyaml.Yaml

class Environment implements Serializable {
  String name
  String namespace
  String next_environment // not used
  DeploymentMethod deployment
  List<Service> services
  List<EnvironmentTest> tests

  Environment() {
  }

  boolean isManualDeployment() {
    deployment?.mode == 'manual'
  }

  public static Environment readFromString(String value) {
    Yaml yaml = new Yaml()
    yaml.loadAs(value, Environment)
  }
}