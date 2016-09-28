package com.pearson.deployment.config.kubernetes

class KubePort {
  Integer containerPort

  LinkedHashMap asMap() {
      [ "containerPort": containerPort ]
  }

  @Override
  boolean equals(Object o) {
    if (o == null) {
      return false
    }

    if (!KubePort.class.isAssignableFrom(o.class)) {
      return false
    }

    this.containerPort == o.containerPort
  }
}