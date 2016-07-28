package com.pearson.deployment.kubernetes

import com.pearson.deployment.config.bitesize.Service

// import org.yaml.snakeyaml.Yaml

class KubeThirdpartyHandler extends KubeResourceHandler {
  KubeThirdpartyHandler(KubeAPI client, Service svc, OutputStream log=System.out) {
    super(client, svc, log)
    this.kind = 'thirdpartyresource'
  }

  KubeThirdpartyHandler(KubeAPI client, LinkedHashMap resource, OutputStream log=System.out) {
    super(client, new Service(), log)
    this.kind = 'thirdpartyresource'

    svc.name = resource.metadata.name
    // svc.namespace = resource.metadata.namespace
    svc.template_filename = resource.metadata.labels?.template_filename
    svc.parameter_filename = resource.metadata.labels?.parameter_filename
    svc.stack_name = resource.metadata.labels?.stack_name
    svc.version = resource.metadata.labels?.version
    svc.type = resource.metadata.labels?.type
  }

  @Override
  boolean equals(Object obj) {
    if (obj == null) {
      return false
    }

    if (!KubeThirdpartyHandler.class.isAssignableFrom(obj.getClass())) {
      return false
    }
    
    KubeThirdpartyHandler other = (KubeThirdpartyHandler)obj

    (svc.name == other.svc.name) &&
    // (svc.namespace == other.svc.namespace) &&
    (svc.template_filename == other.svc.template_filename) &&
    (svc.parameter_filename == other.svc.parameter_filename) &&
    (svc.stack_name == other.svc.stack_name) &&
    (svc.version == other.svc.version) &&
    (svc.type == other.svc.type)
  }

  private KubeThirdpartyHandler getHandler(String name) {    
    LinkedHashMap thirdparty = client.fetch(kind, name)
    new KubeThirdpartyHandler(client, thirdparty, log)
  }

  private LinkedHashMap resource() {
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