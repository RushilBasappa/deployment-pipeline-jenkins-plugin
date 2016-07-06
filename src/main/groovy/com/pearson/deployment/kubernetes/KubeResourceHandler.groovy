package com.pearson.deployment.kubernetes

import com.pearson.deployment.config.bitesize.Service

import org.yaml.snakeyaml.Yaml

// Parent class for all Resource Handlers
//   implements create, update resources
//   uses kubectl (KubeWrapper) to action against cluster

class KubeResourceHandler implements Comparable {
  Service svc
  KubeWrapper client
  protected String handlerType = "pod"
  protected OutputStream log

  KubeResourceHandler(Service svc, OutputStream log=System.out) {
    this.svc = svc
    this.log = log
    this.client = new KubeWrapper(handlerType, svc.namespace)
  }

  KubeResourceHandler() {
  }

  def createOrUpdate() {
    try {
      def existing = getHandler(svc.name)
      if (existing.compareTo(this)) {
        log.println "... > updating ${svc.namespace}/${handlerType}/${svc.name}"
        update()
      }
    } catch (ResourceNotFoundException e) {
      log.println("... > creating ${svc.namespace}/${handlerType}/${svc.name} ")
      create()
    }
  }

  int compareTo(def other) {
    // implemented in subclasses
    return 0
  }

  def update() {          
    client.apply kubeSpec()
  }

  def create() {
    client.create kubeSpec()
  }
}