package com.pearson.deployment

class KubeIngress extends KubeResource {
  KubeIngress(def namespace, def c) {
    super('ing', namespace, c)
  }

  def compareTo(def other) {
    // not sure if this.config == other.config good enough
    (this.config.name == other.config.name ) &&
    (this.config.external_url == other.config.external_url) &&
    (this.config.port == other.config.port) &&
    (this.config.ssl == other.config.ssl)
  }


  def configToSpec(def s) {
    def svc = s
    if (s == null ) {
      svc = config
    }

    def enableSSL = svc.ssl ? "true" : "false"
    [
      "apiVersion": "extensions/v1beta1",
      "kind": "Ingress",
      "metadata" : [
        "name": svc.name,
        "labels": [
          "creator": "pipeline",
          "name": svc.name,
          "ssl": enableSSL
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

  def specToConfig(def spec) {
    [
      "name": spec.metadata.name,
      "external_url": spec.spec.rules[0]?.host,
      "port": spec.spec.rules[0]?.port ?: 80
    ]
  }
}
