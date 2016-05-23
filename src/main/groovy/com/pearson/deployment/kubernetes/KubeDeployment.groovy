package com.pearson.deployment.kubernetes

class KubeDeployment extends KubeResource {
  KubeDeployment(def namespace, def config) {
    super('deployment', namespace, config)
  }

  def watch() {
    def result = get(this.config.name)
    return result
  }

  def done(def d) {
    if (d.replicas == d.updated_replicas) {
      if (d.available_replicas == d.updated_replicas) {
        return 'success'
      }
    }
    return 'running'
  }

  def compareTo(KubeDeployment other) {
    def this_app = this.config.application ?: this.config.name
    def other_app = other.config.application ?: other.config.name

    def this_replicas = this.config.replicas ?: 1
    def other_replicas = other.config.replicas ?: 1

    // not sure if this.config == other.config good enough
    (this.config.name == other.config.name ) &&
    (this_app == other_app) &&
    (this.config.port == other.config.port) &&
    (this_replicas == other_replicas)
    (this.config.env == other.config.env)
  }

  def configToSpec(def s) {
    def svc = s
    if (s == null ) {
      svc = config
    }

    def numreplicas = svc.replicas ?: 1
    String docker_registry = svc.docker_registry
    String project = svc.project
    String image_name = svc.application ?: svc.name
    String image = "${docker_registry}/${project}/${image_name}"

    if (svc.version) {
      image = "${image}:${svc.version}".toString()
    } else {
      image = "${image}:latest".toString()
    }

    def env = svc.env ?: []

    def version = svc.version ?: 'latest'

    [
      "apiVersion": "extensions/v1beta1",
      "kind": "Deployment",
      "metadata": [
        "name": svc.name,
        "labels": [
          "creator": "pipeline",
          "name": svc.name,
          "version": version,
          "application": image_name
        ]
      ],
      "spec": [
        "replicas": numreplicas,
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
                "env": env
              ]
            ]
          ]
        ]
      ]
    ]
  }

  def specToConfig(def spec) {
    def env = spec.spec?.template?.spec?.containers?.getAt(0)?.env ?: []
    def port  = spec.spec?.template?.spec?.containers?.getAt(0)?.ports?.getAt(0)?.containerPort ?: 80
    def image = spec.spec?.template?.spec?.containers?.getAt(0)?.image
    def replicas = spec.spec?.replicas ?: 1

    [
      "name": spec.metadata.name,
      "application": spec.metadata.labels.application,
      "port": port,
      "image": image,
      "version": spec.metadata.labels.version,
      "replicas": replicas,
      "current_replicas": spec.status?.replicas,
      "updated_replicas": spec.status?.updatedReplicas,
      "unavailable_replicas": spec.status?.unavailableReplicas,
      "available_replicas": spec.status?.availableReplicas,
      "env": env
    ]

  }
}
