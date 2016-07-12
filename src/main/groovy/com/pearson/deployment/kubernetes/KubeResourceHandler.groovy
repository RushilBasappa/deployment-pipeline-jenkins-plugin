package com.pearson.deployment.kubernetes

import com.pearson.deployment.config.bitesize.Service

import org.yaml.snakeyaml.Yaml

// Parent class for all Resource Handlers
//   implements create, update resources
//   uses kubectl (KubeWrapper) to action against cluster

class KubeResourceHandler {
  Service svc
  KubeAPI client
  protected String kind = "pod"
  protected OutputStream log

  KubeResourceHandler(KubeAPI client, Service svc, OutputStream log=System.out) {
    this.svc = svc
    this.log = log
    this.client = client
  }

  KubeResourceHandler() {
    this.svc = new Service()
    this.log = System.out
  }

  def createOrUpdate() {
    try {
      def existing = getHandler(svc.name)
      log.println existing
      
      if (existing != this) {
        log.println "... > updating ${svc.namespace}/${kind}/${svc.name}"
        update()
      }
    } catch (ResourceNotFoundException e) {
      log.println("... > creating ${svc.namespace}/${kind}/${svc.name} ")
      create()
    }
  }

  def update() {          
    client.apply kind, resource()
  }

  def create() {
    client.create kind, resource()
  }
}