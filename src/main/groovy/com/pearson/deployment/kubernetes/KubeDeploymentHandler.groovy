package com.pearson.deployment.kubernetes

import com.pearson.deployment.config.bitesize.Service
import com.pearson.deployment.helpers.Helper

import org.yaml.snakeyaml.Yaml

class KubeDeploymentHandler extends KubeResourceHandler {

  KubeDeploymentHandler(Service svc, OutputStream log) {
    super(svc, log)
    this.handlerType = 'deployment'
    this.client = new KubeWrapper(handlerType, svc.namespace)
  }

  // Create deployment from kubectl yaml
  KubeDeploymentHandler(LinkedHashMap resource, OutputStream log=System.out) {
    this.log = log
    this.handlerType = 'deployment'

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

    this.client = new KubeWrapper(handlerType, svc.namespace)
  }

  private KubeDeploymentHandler getHandler(String name) {
    try {
      LinkedHashMap deployment = client.fetch(name)
      return new KubeDeploymentHandler(deployment, log)
    } catch (all) {
      throw new ResourceNotFoundException("Deployment ${name} not found")
    }
  }

  int compareTo(KubeDeploymentHandler other) {
    if ((svc.name == other.svc.name) &&
    (svc.application == other.svc.application) &&
    (svc.port == other.svc.port) &&
    (svc.replicas == other.svc.replicas) &&
    (svc.env == other.svc.env)) {
      return 0
    } else {
      return 1
    }
  }

  boolean differs(KubeDeploymentHandler other) {
    return ! compareTo(other)
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

  private LinkedHashMap kubeSpec() {
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