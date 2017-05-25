package com.pearson.deployment.config.kubernetes

import groovy.json.*

class KubeServicePort {
  Integer port
  Integer targetPort
  String  protocol
  String  name

  KubeServicePort(LinkedHashMap o) {
    name = o.name
    port = o.port
    targetPort = o.targetPort
    protocol = o.protocol
  }

  @Override
  boolean equals(Object o) {
    if (o == null) {
      return false
    }

    if (!KubeServicePort.class.isAssignableFrom(o.class)) {
      return false
    }
    def obj = (KubeServicePort)o

    (name == obj.name) &&
    (port == obj.port) &&
    (protocol == obj.protocol) &&
    (targetPort == obj.targetPort)
  }

  String toString() {
    return "[name: ${name}, port: ${port}, targetPort: ${targetPort}, protocol: ${protocol}]"
  }

  LinkedHashMap asMap() {
    [
      "port": port,
      "name": name,
      "protocol": protocol,
      "targetPort": targetPort
    ]
  }
}
