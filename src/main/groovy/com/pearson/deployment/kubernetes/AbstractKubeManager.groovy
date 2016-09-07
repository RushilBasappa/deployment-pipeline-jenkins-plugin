package com.pearson.deployment.kubernetes

import com.pearson.deployment.config.bitesize.DeploymentMethod
import com.pearson.deployment.config.bitesize.Service

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
            s = svc.clone()
            s.name = "${s.name}-${color}"
            if (s.external_url) {
              s.external_url = Helper.addHostPrefix(s.external_url, "-${color}")
            }

            if (s.isThirdParty()) {
              retval.add(new KubeThirdpartyHandler(client, svc, log))
            } else {
              retval.add(new KubeDeploymentHandler(client, svc,log))
              retval.add(new KubeServiceHandler(client, svc, log))
              retval.add(new KubeIngressHandler(client, svc ,log))
            }
          }
          retval.add(new KubeIngressHandler(client, svc, log))
      } else {
        if (svc.isThirdParty) {
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