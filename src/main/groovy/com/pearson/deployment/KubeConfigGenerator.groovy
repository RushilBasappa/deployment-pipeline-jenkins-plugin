package com.pearson.deployment

import org.yaml.snakeyaml.Yaml
import org.codehaus.groovy.runtime.StackTraceUtils

import java.security.SecureRandom
import java.math.BigInteger

import com.pearson.deployment.kubernetes.*

class KubeConfigGenerator {
  String filename
  LinkedHashMap config
  String project
  String docker_registry
  private SecureRandom random

  KubeConfigGenerator(String project, LinkedHashMap cfg) {
    this.config   = cfg
    this.project = project
    this.docker_registry = System.getenv().DOCKER_REGISTRY ?: "bitesize-registry.default.svc.cluster.local:5000"
    this.random = new SecureRandom()
    // this.namespace = namespace
  }

  def setup() {
    config.services.each { service ->
      service.project = project
      service.docker_registry = docker_registry

      // Check if it's a thirdparty service definition
      if (service.type != null) {
        // probably we need to extract service info and fill
        // in new LinkedHashMap
        def randomstring = new BigInteger(64, random).toString(32)
        def rscConf = [
          "template_filename": "${service.type}.template".toString(),
          "parameter_filename": "${service.type}.parameter".toString(),
          "version": "${service.version}".toString(),
          "namespace": "${config.namespace}".toString(),
          "name": "${service.type}-${service.name}.${service.namespace}.prsn.io".toString(),
          "type": "${service.type}".toString(),
          "stack_name": "${service.namespace}-${service.type}-${randomstring}".toString()
        ]
        def rsc = new KubeThirdPartyResource(config.namespace, rscConf)
        rsc.createOrUpdate()

      } else {
        // def rc = new KubeController(config.namespace, service)
        // rc.createOrUpdate()
        def deployment = new KubeDeployment(config.namespace, service)
        if (deployment.exist(service.name) ) {
          if (deployment.config.version == "latest" || deployment.config.version == null) {
            def old = deployment.get(service.name)
            deployment.config.version = old.config.version
          }
          deployment.createOrUpdate()
        }

        def svc = new KubeService(config.namespace, service)
        svc.createOrUpdate()
        if (service.external_url != null) {
          def ingress = new KubeIngress(config.namespace, service)
          ingress.createOrUpdate()
        }
      }
    }

  }
}
