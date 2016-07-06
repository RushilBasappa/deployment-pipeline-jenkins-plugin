package com.pearson.deployment.kubernetes

import com.pearson.deployment.helpers.Helper

class KubeConfigMap extends KubeResource {
  KubeConfigMap(def namespace, def c, def type) {
    super('configmap', namespace, c)
    this.helper = new Helper()
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
      def name = svc.name + '_' + type
    } else {
      def name = svc.name
      type = 'env'
    }
    [
      "apiVersion": "v1",
      "kind": "ConfigMap",
      "metadata" : [
        "name": name,
        "labels": [
          "creator": "pipeline",
          "name": name,
          "application": image_name
        ]
      ],
      "data": helper.yamlToJson(svc."${type}" ?: '')
    ]
  }

  def specToConfig(def spec) {
    [
      "name": spec.metadata.name
    ]
  }
}
