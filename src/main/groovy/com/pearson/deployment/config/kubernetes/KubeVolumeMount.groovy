package com.pearson.deployment.config.kubernetes

class KubeVolumeMount {
  String name
  String mountPath

  LinkedHashMap asMap() {
    [
      name: name,
      mountPath: mountPath
    ]
  }
}