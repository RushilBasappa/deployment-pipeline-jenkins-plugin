package com.pearson.deployment

import org.yaml.snakeyaml.Yaml
import org.codehaus.groovy.runtime.StackTraceUtils

class KubeConfigGenerator {
  String filename
  LinkedHashMap config
  String project
  String docker_registry

  KubeConfigGenerator(String project, LinkedHashMap cfg) {
    this.config   = cfg
    this.project = project
    this.docker_registry = System.getenv().DOCKER_REGISTRY ?: "bitesize-registry.default.svc.cluster.local:5000"
    // this.namespace = namespace
  }

  def setup() {
    config.services.each { service ->
      service.project = project
      service.docker_registry = docker_registry

      def rc = new KubeController(config.namespace, service)
      rc.createOrUpdate()

      def svc = new KubeService(config.namespace, service)
      svc.createOrUpdate()
      if (service.external_url != null) {
        def ingress = new KubeIngress(config.namespace, service)
        ingress.createOrUpdate()
      }
    }

  }
}
