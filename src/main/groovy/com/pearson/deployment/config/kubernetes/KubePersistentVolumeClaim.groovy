package com.pearson.deployment.config.kubernetes

import groovy.json.*

class KubePersistentVolumeClaim extends AbstractKubeResource {
  public static final String kind = "pvc"

  String name
  String namespace
  String size
  List<String> accessModes
  Map<String,String> labels
  Map<String,String> matchLabels

  KubePersistentVolumeClaim(LinkedHashMap o) {
    name = o.metadata.name
    namespace = o.metadata.namespace
    modes = o.spec.accessModes
    labels = o.metadata.labels
    matchLabels = o.spec.selector?.matchLabels      
  }

  LinkedHashMap asMap() {
    def json = new JsonBuilder()

    json {
      apiVersion "v1"
      kind       "PersistentVolumeClaim"
      metadata {
        name     name
        namespace namespace
        labels   labels
      }
      spec {
        accessModes accessModes
        resources {
          requests {
            storage size
          }
        }
        selector {
          matchLabels matchLabels
        }
      }
    }
  }
}