package com.pearson.deployment.kubernetes

import com.pearson.deployment.config.bitesize.Service

// KubeManager is the main class managing
// resources in the namespace
class KubeManager implements Serializable {
    Service svc // config.service object as serialized from environments.bitesize
    KubeIngressHandler ingress
    KubeDeploymentHandler deployment
    KubeServiceHandler service
    KubeThirdpartyHandler thirdparty
    private OutputStream log

    KubeManager(Service svc, OutputStream log=System.out) {
        this.svc        = svc
        this.ingress    = new KubeIngressHandler(svc, log)
        this.deployment = new KubeDeploymentHandler(svc, log)
        this.service    = new KubeServiceHandler(svc, log)
        this.thirdparty = new KubeThirdpartyHandler(svc, log)
    }

    def manage() {
        def ch = false
        if (svc.type == null ) {
            ch = ingress.createOrUpdate() ? true : ch
            ch = deployment.createOrUpdate() ? true : ch
            ch = service.createOrUpdate() ? true : ch
        } else {
            ch = thirdparty.createOrUpdate()
        }
        return ch
    }

}