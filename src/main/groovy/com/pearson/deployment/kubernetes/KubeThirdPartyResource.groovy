package com.pearson.deployment.kubernetes

class KubeThirdPartyResource extends KubeResource {

  KubeThirdPartyResource( def namespace, def config) {
    super('thirdpartyresource', namespace, config)
  }

  def compareTo(def other) {
    // thirdpartyresource is one-off, assume it didn't change if exist
    this.config.name == other.config.name
    // return true
  }

  def configToSpec(def s) {
    def svc = s

    if (s == null ) {
      svc = config
    }

    [
      "apiVersion": "extensions/v1beta1",
      "kind": "ThirdPartyResource",
      "description": "A specification for ${svc.type}".toString(),
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

  def specToConfig(def spec) {
    [
      "name": spec.metadata.name,
      "namespace": spec.metadata.namespace,
      "type": spec.metadata.labels?.type,
      "version": spec.metadata.labels?.version,
      "template_filename": spec.metadata.labels?.template_filename,
      "parameter_filename": spec.metadata.labels?.parameter_filename,
      "stack_name": spec.metadata.labels?.stack_name
    ]
  }
}
