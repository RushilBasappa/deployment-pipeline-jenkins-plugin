package com.pearson.deployment.config.kubernetes


class KubeGenericobject extends AbstractKubeResource {
  String kind
  String apiVersion
  String name
  String namespace
  Map<String,String> labels
	Map<String,LinkedHashMap> spec

  KubeGenericobject(LinkedHashMap o) {
    kind = o.kind
    name = o.metadata.name
    namespace = o.metadata.namespace
    labels = o.metadata.labels
		spec = o.spec
  }

  LinkedHashMap asMap() {
    [
      apiVersion: apiVersion,
      kind: kind
    ]
  }
}