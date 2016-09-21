package com.pearson.deployment.kubernetes

import com.pearson.deployment.config.bitesize.Service

class KubeIngressHandler extends KubeResourceHandler {

  KubeIngressHandler(KubeAPI client, Service svc, OutputStream log=System.out) {
    super(client, svc, log)
    this.kind = 'ingress'

    this.resource = new KubeIngress(
      name: svc.name,
      namespace: svc.namespace,
      externalUrl: svc.external_url,
      httpsOnly: svc.httpsOnly,
      httpsBackend: svc.httpsBackend,
      ssl: svc.ssl,
      port: svc.port
    )
  }

  KubeIngressHandler(KubeAPI client, LinkedHashMap spec, OutputStream log=System.out) {
    super(client, new Service(), log)
    this.kind = 'ingress'
    
    svc.name = spec.metadata.name
    svc.namespace= spec.metadata.namespace
    svc.external_url = spec.spec.rules[0].host
    def backend = spec.spec.rules[0]?.http?.paths[0]?.backend

    svc.httpsOnlyString = spec.metadata.labels?.httpsOnly
    svc.httpsBackendString = spec.metadata.labels?.httpsBackend
    svc.sslString = spec.metadata.labels?.ssl
    
    svc.port = backend?.servicePort ?: 80 
  }

  @Override
  boolean equals(Object obj) {
    if (obj == null) {
      return false
    }

    if (!KubeIngressHandler.class.isAssignableFrom(obj.getClass())) {
      return false
    }
    
    KubeIngressHandler other = (KubeIngressHandler)obj

    (this.svc.name == other.svc.name) &&
    (this.svc.namespace == other.svc.namespace) &&
    (this.svc.external_url == other.svc.external_url) &&
    (this.svc.port == other.svc.port) &&
    (this.svc.ssl == other.svc.ssl) &&
    (this.svc.httpsBackend == other.svc.httpsBackend) &&
    (this.svc.httpsOnly == other.svc.httpsOnly)
  }

  private KubeIngressHandler getHandler(String name) {
    LinkedHashMap ingress = client.fetch(kind, name)
    return new KubeIngressHandler(client, ingress, log)
  }

  private LinkedHashMap resource() {
    [
      "apiVersion": "extensions/v1beta1",
      "kind": "Ingress",
      "metadata" : [
        "name": svc.name,
        "namespace": svc.namespace,
        "labels": [
          "creator": "pipeline",
          "name": svc.name,
          "ssl": svc.sslString,
          "httpsOnly": svc.httpsOnlyString,
          "httpsBackend": svc.httpsBackendString
        ]
      ],
      "spec": [
        "rules": [
          [
          "host": svc.external_url,
          "http": [
            "paths": [
              [
              "path": "/",
              "backend": [
                "serviceName": svc.backend,
                "servicePort": svc.port
              ]
              ]
            ]
          ]
          ]
        ]
      ]
    ]
  }
}