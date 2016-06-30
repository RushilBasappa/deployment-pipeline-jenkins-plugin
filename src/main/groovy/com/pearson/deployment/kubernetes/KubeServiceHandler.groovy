package com.pearson.deployment.kubernetes

import com.pearson.deployment.config.bitesize.Service

import org.yaml.snakeyaml.Yaml

class KubeServiceHandler extends KubeResourceHandler implements Comparable {

    KubeServiceHandler(Service svc, OutputStream log=System.out) {
        super(svc, log)
        this.handlerType = 'service'
        this.client = new KubeWrapper(handlerType, svc.namespace)
    }

    KubeServiceHandler(String spec, OutputStream log=System.out) {
        this.handlerType = 'service'
        this.log = log
        
        Yaml yaml = new Yaml()
        LinkedHashMap contents = yaml.load(spec)

        svc = new Service()
        svc.name = contents.metadata.name
        svc.namespace = contents.metadata.namespace
        svc.port = contents.spec.ports[0].port
        this.client = new KubeWrapper(handlerType, svc.namespace)
    }

    int compareTo(KubeServiceHandler other) {
        if ((svc.name == other.svc.name) &&
        (svc.namespace == other.svc.namespace) &&
        (svc.port == other.svc.port)) {
            return 0
        } else {
            return 1
        }
    }

    private KubeServiceHandler getHandler(String name) {
        try {
            String s = client.fetch(name)
            return new KubeServiceHandler(s, log)
        } catch (all) {
            return null
        }
    }

    private LinkedHashMap kubeSpec() {
        [
            "apiVersion": "v1",
            "kind": "Service",
            "metadata" : [
                "name": svc.name,
                "namespace": svc.namespace,
                "labels": [
                    "creator": "pipeline",
                    "name": svc.name
                ]
            ],
            "spec": [
                "ports": [
                    [
                    "port": svc.port,
                    "protocol": "TCP",
                    "name": "tcp-port",
                    "targetPort": svc.port
                    ]
                ],
                "selector": [
                    "name": svc.name
                ]
            ]
        ]
    }
}