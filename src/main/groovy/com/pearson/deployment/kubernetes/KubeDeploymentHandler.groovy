package com.pearson.deployment.kubernetes

import com.pearson.deployment.config.bitesize.Service
import com.pearson.deployment.helpers.Helper

import org.yaml.snakeyaml.Yaml

class KubeDeploymentHandler extends KubeResourceHandler {

  KubeDeploymentHandler(KubeAPI client, Service svc, OutputStream log) {
    super(client, svc, log)
    this.kind = 'deployment'
    this.client = client
  }

  // Create deployment from kubectl yaml
  KubeDeploymentHandler(KubeAPI client, LinkedHashMap resource, OutputStream log=System.out) {
    super(client, new Service(), log)
    this.kind = 'deployment'

    svc = new Service()
    svc.name = resource.metadata.name
    svc.namespace= resource.metadata.namespace
    svc.replicas = resource.spec.replicas
    
    svc.updated_replicas = resource.status?.updatedReplicas ?: 0
    svc.available_replicas = resource.status?.availableReplicas ?: 0   

    LinkedHashMap container = resource.spec?.template?.spec?.containers[0]

    svc.port = container.ports[0]?.containerPort
    svc.image = container.image

    svc.version = resource.metadata?.labels?.version ?: container.image.split(':').last()

    svc.setEnvVariables(container.env)

  }

  private KubeDeploymentHandler getHandler(String name) {
    LinkedHashMap deployment = client.fetch(kind, name)
    new KubeDeploymentHandler(client, deployment, log)
  }

  @Override
  boolean equals(Object obj) {
    if (obj == null) {
      return false
    }

    if (!KubeDeploymentHandler.class.isAssignableFrom(obj.getClass())) {
      return false
    }

    KubeDeploymentHandler other = (KubeDeploymentHandler)obj

    if ((svc.name == other.svc.name) &&
    (svc.application == other.svc.application) &&
    (svc.port == other.svc.port) &&
    (svc.replicas == other.svc.replicas) &&
    (svc.env == other.svc.env)) {
      return true
    }
    return false
  }

  String watch() {
    def result = getHandler(svc.name)
    if ((result.svc.replicas == result.svc.updated_replicas) &&
        (result.svc.available_replicas == result.svc.updated_replicas)) {
        return 'success'
    }
    return 'running'
  }

  private def dockerImageName() {
      def name = svc.application ?: svc.name
      "${Helper.dockerRegistry()}/${svc.project}/${name}"
  }

  private def env() {
      // There be filtering of vault_key etc.
      svc.env
  }

  private def version() {
    if (svc.version) {
      return svc.version
    } else {
      try {
        KubeDeploymentHandler result = getHandler(svc.name)
        return result.svc.version
      } catch(ResourceNotFoundException e) {
      }
    }
    return "latest"
  }

  private LinkedHashMap resource() {
    String image = "${dockerImageName()}:${version()}".toString()

    [
      "apiVersion": "extensions/v1beta1",
      "kind": "Deployment",
      "metadata": [
        "name": svc.name,
        "labels": [
          "creator": "pipeline",
          "name": svc.name,
          "version": version(),
          "application": svc.application
        ]
      ],
      "spec": [
        "replicas": svc.replicas,
        "selector": [
          "matchLabels": [
            "name": svc.name
          ]
        ],
        "template": [
          "metadata": [
            "labels": [
              "creator": "pipeline",
              "name": svc.name
            ]
          ],
          "spec": [
            "containers":  [
              [
              "name": svc.name,
              "image": image,
              "ports": [ [ "containerPort": svc.port ] ],
              "env": env()
              ]
            ],
            "nodeSelector": [
              "label": "minion"
            ]
          ]
        ]
      ]
    ] 
  }
}