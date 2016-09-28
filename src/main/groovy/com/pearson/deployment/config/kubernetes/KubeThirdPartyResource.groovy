package com.pearson.deployment.config.kubernetes

import groovy.json.*

class KubeThirdPartyResource extends AbstractKubeResource {

	String name
	String namespace
  String description
  Map<String,String> labels

	public static final String kind = "thirdpartyresource"

  KubeThirdPartyResource(LinkedHashMap o) {
    name = o.metadata.name
    namespace = o.metadata.namespace
		description = o.description
    labels = o.metadata.labels
	
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
		[
			"apiVersion":  "extensions/v1beta1",
			"kind":        "ThirdPartyResource",
			"description": description,
			"metadata": [
				"name": name,
				"namespace": namespace,
				"labels": labels
			]
		]
  }
}