package com.pearson.deployment.kubernetes

import com.pearson.deployment.config.bitesize.Environment

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
  }

  void manage() {
    // KubeAPI client = getKubeAPI(environment.namespace)

    environment.services?.each { service ->
      service.project = project
      service.namespace = environment.namespace

      def kube = new KubeServiceManager(client, service, log)
      serviceManagers[service.name] = kube

      def ch = kube.manage()
      changed = ch ?: changed
    }
    log.println "Changed: ${changed}"
  }

  KubeServiceManager getService(String name) {
    serviceManagers[name]
  }

}