package com.pearson.deployment.kubernetes

import com.pearson.deployment.config.bitesize.DeploymentMethod
import com.pearson.deployment.config.bitesize.Service
import com.pearson.deployment.config.kubernetes.*
import com.pearson.deployment.helpers.*

abstract class AbstractKubeManager {
  AbstractKubeManager() {        
  }

  abstract boolean manage()

  static List<AbstractKubeWrapper> collectResources(DeploymentMethod deployment, KubeAPI client, Service svc, OutputStream log) {
      List<AbstractKubeWrapper> retval = []
      def handler

      if (deployment.isBlueGreen()) {
          ["blue", "green"].each { color ->
            def s = svc.clone()
            s.name = "${s.name}-${color}"
            s.backend = "${s.name}"

            if (s.external_url) {
              s.external_url = Helper.addHostPrefix(s.external_url, "-${color}")
            }
            retval += serviceHandlers(client, s)
          }
          if (!svc.isThirdParty()) {
            handler = getHandler(client, svc, KubeIngressWrapper)
            handler && retval << handler
          }
      } else {
        retval += serviceHandlers(client, svc)        
      }
      retval
  }

  AbstractKubeWrapper getHandler(KubeAPI client, ManagedResource rsc, Class klass) {
    def e = client.get klass.resource.class, rsc.name
    def existing = klass.newInstance(client, e)
    def nevv = klass.newInstance(client, rsc)
    if (nevv != existing) {
      return nevv
    }
    return null
  }

  List<AbstractKubeWrapper> serviceHandlers(KubeAPI client, Service svc) {
    def retval = []
    if (svc.isThirdParty()) {
      handler = getHandler(client, svc, KubeThirdPartyResourceWrapper)
      handler && retval << handler          
    } else {
      svc.volumes.each {
        handler = getHandler(client, it, KubePersistentVolumeClaimWrapper)
        handler && retval << handler
      }
      [KubeDeploymentWrapper, KubeServiceWrapper, KubeIngressWrapper].each {
        handler = getHandler(client, svc, it)
        handler && retval << handler
      }
    }
    retval
  }
}