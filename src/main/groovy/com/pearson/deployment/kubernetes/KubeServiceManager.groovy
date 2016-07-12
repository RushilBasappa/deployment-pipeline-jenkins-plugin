package com.pearson.deployment.kubernetes

import com.pearson.deployment.config.bitesize.Service

// KubeServiceManager is the main class managing
// resources in the namespace
class KubeServiceManager implements Serializable {
    Service svc // config.service object as serialized from environments.bitesize
    KubeAPI client
    protected KubeIngressHandler ingress
    protected KubeDeploymentHandler deployment
    protected KubeServiceHandler service
    protected KubeThirdpartyHandler thirdparty
    private OutputStream log

    KubeServiceManager(KubeAPI client, Service svc, OutputStream log=System.out) {
        this.svc        = svc
        this.client     = client
        // KubeAPI client  = new KubeWrapper(svc.namespace) 
        this.ingress    = new KubeIngressHandler(client, svc, log)
        this.deployment = new KubeDeploymentHandler(client, svc, log)
        this.service    = new KubeServiceHandler(client, svc, log)
        this.thirdparty = new KubeThirdpartyHandler(client, svc, log)
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