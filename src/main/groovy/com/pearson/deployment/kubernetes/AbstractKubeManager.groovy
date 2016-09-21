package com.pearson.deployment.kubernetes

import com.pearson.deployment.config.bitesize.DeploymentMethod
import com.pearson.deployment.config.bitesize.Service
import com.pearson.deployment.helpers.*

abstract class AbstractKubeManager {
  AbstractKubeManager() {        
  }

  abstract boolean manage()

  static AbstractKubeManager getManagerForDeployment(DeploymentMethod deployment, KubeAPI client, Service svc, OutputStream log) {
    if (deployment.isBlueGreen() ) {
      return KubeBlueGreenServiceManager(client, svc, log)
    } else {
      return KubeServiceManager(client, svc, log)
    }
  }

  static List<KubeResourceHandler> collectResources(DeploymentMethod deployment, KubeAPI client, Service svc, OutputStream log) {
      List<KubeResourceHandler> retval = []

      // TODO: This will need to be refactored
      if (deployment.isBlueGreen()) {
          ["blue", "green"].each { color ->
            def s = svc.clone()
            s.name = "${s.name}-${color}"
            s.backend = "${s.name}"

            if (s.external_url) {
              s.external_url = Helper.addHostPrefix(s.external_url, "-${color}")
            }

            // ingress = new KubeIngress(svc)
            // resources.add ingress
            // deployment = new KubeDeployment(svc)
            // resources.add deployment

            if (s.isThirdParty()) {
              retval.add(new KubeThirdpartyHandler(client, s, log))
            } else {
              retval.add(new KubeDeploymentHandler(client, s, log))
              retval.add(new KubeServiceHandler(client, s, log))
              retval.add(new KubeIngressHandler(client, s ,log))
            }
          }
          if (!svc.isThirdParty()) {
            retval.add(new KubeIngressHandler(client, svc, log))
          }
      } else {
        if (svc.isThirdParty()) {
          retval.add(new KubeThirdpartyHandler(client, svc, log))
        } else {
          retval.add(new KubeDeploymentHandler(client, svc, log))
          retval.add(new KubeServiceHandler(client, svc, log))
          retval.add(new KubeIngressHandler(client, svc ,log))
        }
      }
      retval
  }
}