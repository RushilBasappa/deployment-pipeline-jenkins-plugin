package com.pearson.deployment.config.kubernetes


class KubeThirdPartyGenericResource extends AbstractKubeResource {
  String apiVersion
  String kind
  LinkedHashMap spec
  LinkedHashMap metadata
  // public static String kind = "thirdpartyresource"

  KubeThirdPartyGenericResource(LinkedHashMap o) {
    apiVersion = o.apiVersion
    spec = o.spec
    metadata = o.metadata
    this.kind = o.kind
  }

  @Override
  boolean equals(Object o) {
    if (o == null) {
      return false
    }

    if (!KubeThirdPartyGenericResource.class.isAssignableFrom(o.class)) {
      return false
    }
    (o.apiVersion == apiVersion) && (o.spec == spec) && (o.metadata == metadata)
  }

  def asMap() {
    [
      apiVersion: apiVersion,
      kind: kind,
      metadata: metadata,
      spec: spec
    ]
  }

  def getNamespace() {
    metadata.namespace
  }

  def getName() {
    metadata.name
  }

  def getLabels() {
    if (metadata.labels) {
      return metadata.labels
    }
    []
  }


}

// kind: svc.type,
// metadata: [
//   name: svc.name,
//   namespace: svc.namespace
// ],
// spec: [
//   version: svc.version,
//   options: svc.options
// ]
