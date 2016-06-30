package com.pearson.deployment.kubernetes

import com.pearson.deployment.config.bitesize.Service

import org.yaml.snakeyaml.Yaml

class KubeThirdpartyHandler extends KubeResourceHandler {
    KubeThirdpartyHandler(Service svc, OutputStream log=System.out) {
        super(svc, log)
        this.handlerType = 'thirdpartyresources'
        this.client = new KubeWrapper(handlerType, svc.namespace)
    }

    KubeThirdpartyHandler(String spec, OutputStream log=System.out) {
        this.handlerType = 'thirdpartyresources'
        this.log = log
        
        Yaml yaml = new Yaml()
        LinkedHashMap contents = yaml.load(spec)

        svc = new Service()
        svc.name = contents.metadata.name
        svc.namespace = contents.metadata.namespace
        svc.template_filename = contents.metadata.labels?.template_filename
        svc.parameter_filename = contents.metadata.labels?.parameter_filename
        svc.stack_name = contents.metadata.labels?.stack_name
        svc.version = contents.metadata.labels?.version
        svc.type = contents.metadata.labels?.type
        this.client = new KubeWrapper(handlerType, svc.namespace)
    }

    int compareTo(KubeThirdpartyHandler other) {
        if ((svc.name == other.svc.name) &&
        (svc.namespace == other.svc.namespace) &&
        (svc.template_filename == other.svc.template_filename) &&
        (svc.parameter_filename == other.svc.parameter_filename) &&
        (svc.version == other.svc.version) &&
        (svc.type == other.svc.type)) {
            return 0
        } else { 
            return 1
        }
    }

    private KubeThirdpartyHandler getHandler(String name) {
        try {
            String s = client.fetch(name)
            return new KubeThirdpartyHandler(s, log)
        } catch (all) {
            return null
        }
    }

    private LinkedHashMap kubeSpec() {
        [
            "apiVersion": "extensions/v1beta1",
            "kind": "ThirdPartyResource",
            "description": "A specification for ${svc.type}".toString(),
            "metadata" : [
                "name":                 svc.name,
                "namespace":            svc.namespace,
                "labels": [
                    "creator":            "pipeline",
                    "name":               svc.name,
                    "type":               svc.type,
                    "version":            svc.version,
                    "template_filename":  svc.template_filename,
                    "parameter_filename": svc.parameter_filename,
                    "stack_name":         svc.stack_name
                ]
            ]
        ]
    }
}