package com.pearson.deployment.config.kubernetes

import com.pearson.deployment.config.bitesize.EnvVar
import groovy.json.*

class KubeContainer {
  String name
  String image
  List<KubePort> ports
  List<KubeVolumeMount>volumeMounts
  List<EnvVar> env
  LinkedHashMap livenessProbe

  KubeContainer(LinkedHashMap o) {
    name  = o.name
    image = o.image
    livenessProbe = o.livenessProbe
    ports = o.ports.collect{ p ->
      new KubePort(containerPort: p.containerPort)
    }


    volumeMounts = o.volumeMounts.collect{ mount ->
      new KubeVolumeMount(name: mount.name, mountPath: mount.mountPath)
    }

    env = o.env.collect { var ->
      new EnvVar(name: var.name, secret: var.secret, value: var.value)
    }
  }

  @Override
  boolean equals(Object o) {
    if (o == null) {
      return false
    }

    if (!KubeContainer.class.isAssignableFrom(o.getClass())) {
      return false
    }

    def obj = (KubeContainer)o

    (this.name == obj.name) &&
    (this.image == obj.image) &&
    (this.ports == obj.ports) &&
    (this.volumeMounts == obj.volumeMounts) &&
    (this.livenessProbe == o.livenessProbe) &&
    (this.env == obj.env)
  }

  @Override
  String toString() {
    return "KubeContainer[name: ${name}, image: ${image}, ports: ${ports}, mounts: ${volumeMounts}, env: ${env}]"
  }

  LinkedHashMap asMap() {
    [
      "name": name,
      "image": image,
      "ports": ports.collect { p -> p.asMap() },
      "volumeMounts": volumeMounts.collect { v -> v.asMap() },
      "env": env.collect { e -> e.asMap() },
      "livenessProbe": livenessProbe
    ]
  }
}
