package com.pearson.deployment

class ApplicationBuilder {
  def appDefinition
  def project

  ApplicationBuilder(String configPath) {
    this.appDefinition = new ApplicationConfig(configPath)
    this.project = appDefinition.attributes.project
  }

  def getApplication(String name) {
    appDefinition.attributes.applications?.find { it.name == name }
  }

  def getDockerImage(String app) {
    def application = getApplication(app)
    "${dockerRegistry()}/${project}/${application.name}:${application.version}"
  }

  def dockerRegistry() {
    return System.getenv().DOCKER_REGISTRY ?: "bitesize-registry.default.svc.cluster.local:5000"
  }
}
