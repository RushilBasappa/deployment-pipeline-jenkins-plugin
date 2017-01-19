package com.pearson.deployment.kubernetes

import com.pearson.deployment.config.bitesize.Environment
import com.pearson.deployment.config.bitesize.Service

class KubeEnvironmentManager {
  private Environment environment
  private OutputStream log
  private KubeAPI client

  private def changed
  private String project
  List resources
  // private Map<String, KubeServiceManager> serviceManagers

  KubeEnvironmentManager(KubeAPI client, String project, Environment environment, OutputStream log=System.out) {
    this.environment = environment
    this.client = client
    this.project = project
    // this.serviceManagers = new LinkedHashMap()
    this.log = log
  }

  void manage() {
    if (!environment.deployment.isValid()) {
      log.println "Skipping ${environment.name}: deployment misconfigured"
      return
    }

    environment.services?.each { service ->
      manageService(service)
    }
  }

  private void setServiceProperties(Service service) {
    service.project = project
    service.namespace = environment.namespace
    service.setupDeploymentMethod(environment)

    if (!service.isThirdParty() && service.deployment.isBlueGreen()) {
      service.backend = "${service.name}-${environment.deployment.active}"
    }
  }

  private void manageService(Service svc) {
    setServiceProperties(svc)
    resources = AbstractKubeManager.collectResources(client, svc, log)
    resources.each { rsc ->
      client.apply rsc
    }
  }

}
