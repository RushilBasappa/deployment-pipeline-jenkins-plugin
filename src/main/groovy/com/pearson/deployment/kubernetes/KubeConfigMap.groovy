package com.pearson.deployment.kubernetes

class KubeConfigMap extends KubeResource {

  String type
  String name

  KubeConfigMap(def namespace, def c, String type) {
    super('configmap', namespace, c)
    this.type = type
  }

  def compareTo(def other) {
    def this_name = this.config.name + '-' + this.type
    def other_name = other.config.name + '-' + this.type

    (this_name == other_name ) &&
    (this.config."${type}" == other.config."${type}")
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
