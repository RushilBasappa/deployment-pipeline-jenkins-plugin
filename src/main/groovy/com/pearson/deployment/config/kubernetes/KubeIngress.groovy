package com.pearson.deployment.config.kubernetes

import groovy.json.*

class KubeIngress extends AbstractKubeResource {
  public static final String kind = "ingress"

  Map<String, String> labels
  List<KubeIngressRule> rules = []

  String name
  String namespace
 
  KubeIngress(LinkedHashMap o) {
    name = o.metadata?.name
    namespace = o.metadata?.namespace
    labels    = o.metadata?.labels 
    rules     = o.spec.rules?.collect{ r -> new KubeIngressRule(r)}
  }


  boolean equals(Object o) {
    if (o == null) {
      return false
    }

    if (!KubeIngress.class.isAssignableFrom(o.class)) {
      return false
    }
    
    def obj = (KubeIngress)o
    (name == obj.name) &&
    (namespace == obj.namespace) &&
    (labels == obj.labels) &&
    (rules == obj.rules)
    
  }

  LinkedHashMap asMap() {
    [
      "apiVersion": "extensions/v1beta1",
      "metadata": [
        "name": name,
        "namespace": namespace,
        "labels": labels                
      ],
      "spec": [
        "rules":  rules.collect{ r -> r.asMap() }
      ]
    ]
  }

}