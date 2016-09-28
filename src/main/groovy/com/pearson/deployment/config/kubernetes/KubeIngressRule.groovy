package com.pearson.deployment.config.kubernetes

import groovy.json.*

class KubeIngressRule {
  String host
  List<KubeHttpPath> paths

  KubeIngressRule(LinkedHashMap o) {
    this.host = o.host
    this.paths = o.http.paths.collect { p -> new KubeHttpPath(p) }
  }

  @Override
  boolean equals(Object o) {
    if (o == null) {
      return false
    }

    if (!KubeIngressRule.class.isAssignableFrom(o.class)) {
      return false
    }

    def obj = (KubeIngressRule)o
    
    (host == obj.host) &&
    (paths == obj.paths)
  }


  LinkedHashMap asMap() {
    [
      "host": host,
      "http": [
        "paths": paths.collect { p -> p.asMap() }
      ]
    ]      
  }
}