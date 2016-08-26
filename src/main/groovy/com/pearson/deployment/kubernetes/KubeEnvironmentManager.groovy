package com.pearson.deployment.kubernetes

import com.pearson.deployment.config.bitesize.Environment
import com.pearson.deployment.config.bitesize.Service

class KubeEnvironmentManager {
  private Environment environment
  private OutputStream log
  private KubeAPI client

  private def changed
  private String project
  private Map<String, KubeServiceManager> serviceManagers

  KubeEnvironmentManager(KubeAPI client, String project, Environment environment, OutputStream log=System.out) {
    this.environment = environment
    this.client = client
    this.project = project
    this.serviceManagers = new LinkedHashMap()
    this.log = log
  }

  void manage() {
    if (environment.deployment.isValid()) {
      environment.services?.each { service ->
        if (environment.deployment.isBlueGreen()) {
          manageBlueGreenService(service)
        } else {
          manageService(service)
        }
      }
    } else {
      log.println "Skipping ${environment.name}: deployment misconfigured"
    }
  }

  private void setServiceProperties(Service service) {
    service.project = project
    service.namespace = environment.namespace
    if (environment.deployment.isBlueGreen()) {
      service.backend = "${service.name}-${environment.deployment.active}"
    }
  }

  private void manageService(Service svc) {
    setServiceProperties(svc)

    KubeServiceManager s = new KubeServiceManager(client, svc, log)
    s.manage()
    serviceManagers[svc.name] = s
  }

  private void manageBlueGreenService(Service svc) {
    setServiceProperties(svc)
    KubeBlueGreenServiceManager s = new KubeBlueGreenServiceManager(client, svc, log)
    s.manage()
    serviceManagers[svc.name] = s
  }

  AbstractKubeManager getService(String name) {
    serviceManagers[name]
  }

}