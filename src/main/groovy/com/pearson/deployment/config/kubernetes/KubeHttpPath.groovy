package com.pearson.deployment.config.kubernetes

import groovy.json.*

class KubeHttpPath {
  String  path
  String  backendName
  String backendPort

  KubeHttpPath(LinkedHashMap o) {
    path = o.path
    backendName = o.backend?.serviceName
    backendPort = o.backend?.servicePort
  }

  @Override
  boolean equals(Object o) {
    if (o == null) {
      return false
    }

    if (!KubeHttpPath.class.isAssignableFrom(o.class)) {
      return false
    }

    def obj = (KubeHttpPath)o

    (path == obj.path) &&
    (backendName == obj.backendName) &&
    (backendPort == obj.backendPort)
  }

  LinkedHashMap asMap() {
    [
      "path": path,
      "backend": [
        "serviceName": backendName,
        "servicePort": backendPort
      ]
    ]
  }
}
