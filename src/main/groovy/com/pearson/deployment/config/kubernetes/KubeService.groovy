package com.pearson.deployment.config.kubernetes

import com.pearson.deployment.config.bitesize.Service

import groovy.json.*

class KubeService extends AbstractKubeResource {
  public static final String kind = "service"
  String name
  String namespace
  Map<String, String> selector
  Map<String, String> labels
  List<KubeServicePort> ports = []

  KubeService(LinkedHashMap map) { 
    name      = map.metadata.name
    namespace =  map.metadata.namespace
    ports     =  map.spec.ports.collect{ p -> new KubeServicePort(p)}
  }

  boolean equals(Object o) {
    if (o == null) {
      return false
    }

    if (!KubeService.class.isAssignableFrom(o.class)) {
      return false
    }

    def obj = (KubeService)o
    (name == obj.name ) &&
    (namespace == obj.namespace) &&
    (selector == obj.selector) &&
    (labels == obj.labels) &&
    (ports == obj.ports)
  }

  LinkedHashMap asMap() {    
    [
      "apiVersion": "v1",
      "kind":       "Service",
      "metadata": [
        "name":      name,
        "namespace": namespace,
        "labels":    labels,
      ],
      "spec": [
        "ports": ports.collect { p -> p.asMap() }
      ]
    ]
  }
}