package com.pearson.deployment.config.kubernetes


// This thirdpartyresource represents <1.4 kubernetes object.
// In 1.4 and above, we will use KubeGenericObject
// which can set arbitrary fields and "kind"
class KubeThirdPartyResource extends AbstractKubeResource {

	String name
	String namespace
  String description
  Map<String,String> labels
	Map<String,LinkedHashMap> spec

	public static final String kind = "thirdpartyresource"

  KubeThirdPartyResource(LinkedHashMap o) {
    name = o.metadata.name
    namespace = o.metadata.namespace
		description = o.description
    labels = o.metadata.labels
		spec = o.spec
	
  }

	@Override
	boolean equals(Object o) {
		if (o == null) {
			return false
		}

		if (!KubeThirdPartyResource.class.isAssignableFrom(o.class)) {
			return false
		}

		def obj = (KubeThirdPartyResource)o

		(name == obj.name) &&
		(namespace == obj.namespace) &&
		(description == obj.description) &&
		(labels == obj.labels)
	}

  LinkedHashMap asMap() {
		def r = [
			"apiVersion":  "extensions/v1beta1",
			"kind":        "ThirdPartyResource",
			"description": description,
			"metadata": [
				"name": name,
				"namespace": namespace,
				"labels": labels
			]
		]
		r
  }
}