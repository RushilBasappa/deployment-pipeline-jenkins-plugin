package com.pearson.deployment.kubernetes

import com.pearson.deployment.config.bitesize.Service

import org.yaml.snakeyaml.Yaml

class KubeIngressHandler extends KubeResourceHandler {

  KubeIngressHandler(Service svc, OutputStream log=System.out) {
    super(svc, log)
    this.handlerType = 'ingress'
    this.client = new KubeWrapper(handlerType, svc.namespace)
  }

  KubeIngressHandler(LinkedHashMap spec, OutputStream log=System.out) {
    this.handlerType = 'ingress'
    this.log = log
    
    svc = new Service()
    svc.name = spec.metadata.name
    svc.namespace= spec.metadata.namespace
    svc.external_url = spec.spec.rules[0].host
    def backend = spec.spec.rules[0]?.http?.paths[0]?.backend
    
    svc.port = backend?.servicePort ?: 80 

    this.client = new KubeWrapper(handlerType, svc.namespace)
  }

  int compareTo(KubeIngressHandler other) {
    if ((this.svc.name == other.svc.name) &&
    (this.svc.namespace == other.svc.namespace) &&
    (this.svc.external_url == other.svc.external_url) &&
    (this.svc.port == other.svc.port) &&
    (this.svc.httpsBackend == other.svc.httpsBackend) &&
    (this.svc.httpsOnly == other.svc.httpsOnly)) {
      return 0
    } else {
      return 1
    }
  }

  private KubeIngressHandler getHandler(String n) {
    try {
      LinkedHashMap ingress = client.fetch(n)
      return new KubeIngressHandler(ingress, log)
    } catch (all) {
      throw new ResourceNotFoundException("Ingress ${n} not found")
    }
  }

  private LinkedHashMap kubeSpec() {
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
                "serviceName": svc.name,
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