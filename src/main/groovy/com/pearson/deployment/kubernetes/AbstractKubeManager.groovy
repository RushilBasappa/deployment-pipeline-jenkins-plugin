package com.pearson.deployment.kubernetes

import com.pearson.deployment.config.bitesize.DeploymentMethod
import com.pearson.deployment.config.bitesize.Service
import com.pearson.deployment.config.kubernetes.*
import com.pearson.deployment.config.bitesize.*
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
            s.application = s.application ?: s.name
            s.name = "${s.name}-${color}"
            s.backend = "${s.name}"
            s.deploymentMethod = "bluegreen"

            if (s.external_url) {
              s.external_url = Helper.addHostPrefix(s.external_url, "-${color}")
            }
            retval += serviceHandlers(client, s)
          }
          if (!svc.isThirdParty()) {
            //  We need
            handler = getHandler(client, svc, KubeIngressWrapper)
            handler && retval << handler
          }
      } else {
        retval += serviceHandlers(client, svc)
      }
      retval
  }

  static AbstractKubeWrapper getHandler(KubeAPI client, ManagedResource rsc, Class klass) {
    def existing
    try {
      def e = client.get klass.resourceClass, rsc.name
      existing = klass.newInstance(client, e)
    } catch (ResourceNotFoundException e) {
      // We don't create deployments with service-manage
      // if they don't exist
      if (klass == KubeDeploymentWrapper && rsc.version == null) {
        return null
      }
    }
    def nevv = klass.newInstance(client, rsc)
    if (nevv != existing) {
      return nevv
    }
    return null
  }

  static List<AbstractKubeWrapper> serviceHandlers(KubeAPI client, Service svc) {
    def retval = []
    def handler

    if (svc.isThirdParty()) {
      handler = getHandler(client, svc, KubeThirdPartyWrapper)
      handler && retval << handler
      //kubeVersion = client.getVersion()
      // We will need to take out this when kubernetes 1.5 will mature as
      // currently it's an ugly hack
      /*
      if (kubeVersion !~ /v1.2/ ) {
        handler = getHandler(client, svc, KubeThirdPartyWrapper)
        handler && retval << handler
      } else {
        switch(svc.type) {
          case "mongo":
              break
          case "mysql":
              break
          default:
              break
        }
      }*/
    } else {
      svc.volumes.each {
        it.namespace = svc.namespace
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
