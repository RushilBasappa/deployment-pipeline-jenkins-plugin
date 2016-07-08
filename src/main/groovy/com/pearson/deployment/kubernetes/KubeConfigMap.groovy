package com.pearson.deployment.kubernetes

class KubeConfigMap extends KubeResource {

  String type
  String name

  KubeConfigMap(def namespace, def c, String type) {
    super('configmap', namespace, c)
    this.type = type
  }

  def compareTo(def other) {
    def this_app = this.config.name
    def other_app = other.config.name
    // Not sure if we can compare values yet
  }

  def configToSpec(def s) {
    def svc = s

    if (s == null ) {
      svc = config
    }

    if (type != null) {
      this.name = svc.name + '-' + type
    } else {
      this.name = svc.name
      type = 'env'
    }

    def j = [:]
    svc."${type}".each {
      j[it.name] = it.value
    }

    [
      "apiVersion": "v1",
      "kind": "ConfigMap",
      "metadata" : [
        "name": name,
        "namespace": namespace,
        "labels": [
          "creator": "pipeline",
          "name": name
        ]
      ],
      "data": j
    ]
  }

  def specToConfig(def spec) {
    [
      "name": spec.metadata.name
    ]
  }
}
