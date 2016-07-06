package com.pearson.deployment.kubernetes

import com.pearson.deployment.config.bitesize.Service

import org.yaml.snakeyaml.Yaml

class KubeThirdpartyHandler extends KubeResourceHandler {
  KubeThirdpartyHandler(Service svc, OutputStream log=System.out) {
    super(svc, log)
    this.handlerType = 'thirdpartyresources'
    this.client = new KubeWrapper(handlerType, svc.namespace)
  }

  KubeThirdpartyHandler(LinkedHashMap resource, OutputStream log=System.out) {
    this.handlerType = 'thirdpartyresources'
    this.log = log

    svc = new Service()
    svc.name = resource.metadata.name
    svc.namespace = resource.metadata.namespace
    svc.template_filename = resource.metadata.labels?.template_filename
    svc.parameter_filename = resource.metadata.labels?.parameter_filename
    svc.stack_name = resource.metadata.labels?.stack_name
    svc.version = resource.metadata.labels?.version
    svc.type = resource.metadata.labels?.type
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
      LinkedHashMap thirdparty = client.fetch(name)
      return new KubeThirdpartyHandler(thirdparty, log)
    } catch (all) {
      throw new ResourceNotFoundException("ThirdPartyResource ${name} not found")
    }
  }

  private LinkedHashMap kuberesource() {
    [
      "apiVersion": "extensions/v1beta1",
      "kind": "ThirdPartyResource",
      "description": "A resourceification for ${svc.type}".toString(),
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