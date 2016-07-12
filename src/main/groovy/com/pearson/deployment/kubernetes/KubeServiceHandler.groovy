package com.pearson.deployment.kubernetes

import com.pearson.deployment.config.bitesize.Service

import org.yaml.snakeyaml.Yaml

class KubeServiceHandler extends KubeResourceHandler {

  KubeServiceHandler(KubeAPI client, Service svc, OutputStream log=System.out) {
    super(client, svc, log)
    this.kind = 'service'
  }

  KubeServiceHandler(KubeAPI client, LinkedHashMap resource, OutputStream log=System.out) {
    super(client, new Service(), log)
    this.kind = 'service'
  
    svc.name = resource.metadata.name
    svc.namespace = resource.metadata.namespace
    svc.port = resource.spec.ports[0].port
  }

  @Override
  boolean equals(Object obj) {
    if (obj == null) {
      return false
    }

    if (!KubeServiceHandler.class.isAssignableFrom(obj.getClass())) {
      return false
    }
    
    KubeServiceHandler other = (KubeServiceHandler)obj

    if ((svc.name == other.svc.name) &&
      (svc.namespace == other.svc.namespace) &&
      (svc.port == other.svc.port)) {
      return true
    }
    return false
  }

  private KubeServiceHandler getHandler(String name) {
    LinkedHashMap service = client.fetch(kind, name)
    new KubeServiceHandler(client, service, log)
  }

  private LinkedHashMap resource() {
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