package com.pearson.deployment.kubernetes

class KubeService extends KubeResource {
  KubeService( def namespace, def config) {
    super('service', namespace, config)
  }

  def compareTo(def other) {
    // not sure if this.config == other.config good enough
    (this.config.name == other.config.name ) &&
    (this.config.port == other.config.port)
  }


  def configToSpec(def s) {
    def svc = s
    if (s == null ) {
      svc = config
    }
    [
      "apiVersion": "v1",
      "kind": "Service",
      "metadata" : [
        "name": svc.name,
        "labels": [
          "creator": "pipeline",
          "name": svc.name
        ]
      ],
      "spec": [
        "ports": [
          [
            "port": 80,
            "protocol": "TCP",
            "targetPort": svc.port ?: 80
          ]
        ],
        "selector": [
          "name": svc.name
        ]
      ]
    ]
  }

  def specToConfig(def spec) {
    [
      "name": spec.metadata.name,
      "port": spec?.spec?.ports?.getAt(0)?.port ?: 80
    ]
  }
}
