package com.pearson.deployment.config.kubernetes

import groovy.json.*

import com.pearson.deployment.helpers.*
import com.pearson.deployment.config.bitesize.*

class KubeDeployment extends AbstractKubeResource {
  public static final String kind = "deployment"

  String  name
  String  namespace
  Integer replicas
  Integer updatedReplicas
  Integer availableReplicas
  Map<String, String> labels
  List<KubePodVolume> volumes
  List<KubeContainer> containers
  Map<String, String> labelSelector
  Map<String, String> nodeSelector


  KubeDeployment(LinkedHashMap obj) {
    name = obj.metadata.name
    labels = obj.metadata.labels
    namespace = obj.metadata.namespace
    replicas = obj.spec.replicas
    updatedReplicas = obj.status?.updatedReplicas ?: 0
    availableReplicas = obj.status?.availableReplicas ?: 0

    containers = obj.spec.template?.spec.containers.collect { container ->
      new KubeContainer(container)
    }

    volumes = obj.spec.template.spec.volumes.collect { volume ->
      new KubePodVolume(volume)
    }

    labelSelector = obj.spec.selector?.matchLabels
    nodeSelector = obj.spec.template.spec.nodeSelector
  }

  @Override
  boolean equals(Object o) {
    if (o == null) {
      return false
    }

    if (!KubeDeployment.class.isAssignableFrom(o.getClass())) {
      return false
    }
    def obj = (KubeDeployment)o

    (name == obj.name ) &&
    (namespace == obj.namespace)
    (replicas == obj.replicas) &&
    (containers == obj.containers) &&
    (volumes == obj.volumes) &&
    (nodeSelector ==  obj.nodeSelector) &&
    (labelSelector == obj.labelSelector)
  }

  boolean allReplicasOnline() {
    (replicas == updatedReplicas) && (replicas == availableReplicas)
  }

  LinkedHashMap asMap() {
    [
      "apiVersion": "extensions/v1beta1",
      "kind":       "Deployment",
       "metadata":[
        "name": name,
        "namespace": namespace,
        "labels": labels
      ],
      "spec": [
        "replicas" : replicas,
        "selector": [
          "matchLabels" : labelSelector
        ],
        "template": [
          "metadata": [
            "labels": labels
          ],
          "spec": [
            "containers": containers.collect { c -> c.asMap() },
            "volumes": volumes.collect { v -> v.asMap() },
            "nodeSelector": nodeSelector
          ]
        ]
      ]
    ]
  }
}
