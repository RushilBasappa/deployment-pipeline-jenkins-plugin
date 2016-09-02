package com.pearson.deployment.kubernetes

import com.pearson.deployment.config.bitesize.*
import com.pearson.deployment.helpers.*

class KubeBlueGreenServiceManager extends AbstractKubeManager implements Serializable {
  Service blueSvc
  Service greenSvc
  KubeAPI client
  protected KubeIngressHandler globalIngress
  private OutputStream log

  KubeBlueGreenServiceManager(KubeAPI client, Service svc, OutputStream log=System.out) {
    this.blueSvc        = svc.clone()
    this.greenSvc       = svc.clone()

    this.blueSvc.name = "${this.blueSvc.name}-blue"
    this.greenSvc.name = "${this.greenSvc.name}-green"

    if (svc.external_url) {
      this.blueSvc.external_url = Helper.addHostPrefix(svc.external_url, "-blue")
      this.blueSvc.backend = this.blueSvc.name
      this.greenSvc.external_url = Helper.addHostPrefix(svc.external_url, "-green")
      this.greenSvc.backend = this.greenSvc.name
    }
    this.client           = client
    this.log              = log
    this.globalIngress    = new KubeIngressHandler(client, svc, log)
  }

  boolean manage() {
    def ch = false
    ch = manageSvc(blueSvc) ? true : ch
    ch = manageSvc(greenSvc) ? true : ch
    if (!blueSvc.isThirdParty()) {
      ch = globalIngress.createOrUpdate() ? true : ch
    }
    ch
  }

  private def manageSvc(Service svc) {
    boolean ch = false
    log.println "BlueGreen manager: manage ${svc.name}"
    
    if (svc.isThirdParty()) {
      def p = new KubeThirdpartyHandler(client, svc, log)
      ch = p.createOrUpdate()
    } else {
      def deployment = new KubeDeploymentHandler(client, svc, log)
      ch = deployment.createOrUpdate() ? true : ch

      def service = new KubeServiceHandler(client, svc, log)
      ch = service.createOrUpdate() ? true : ch

      def ingress = new KubeIngressHandler(client, svc ,log)
      ch = ingress.createOrUpdate() ? true : ch

    }
    ch
  }
}