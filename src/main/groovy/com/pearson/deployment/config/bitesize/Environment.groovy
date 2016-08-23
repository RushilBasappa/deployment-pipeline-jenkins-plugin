package com.pearson.deployment.config.bitesize

class Environment implements Serializable {
  String name
  String namespace
  String next_environment // not used
  DeploymentMethod deployment
  List<Service> services
  List<EnvironmentTest> tests

  boolean isManualDeployment() {
    deployment?.mode == 'manual'
  }
}