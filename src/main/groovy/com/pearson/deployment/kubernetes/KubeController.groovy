package com.pearson.deployment.kubernetes


class KubeController extends KubeResource {
  KubeController(def namespace, def config) {
    super('rc', namespace, config)
  }

  def compareTo(def other) {
    def this_app = this.config.application ?: this.config.name
    def other_app = other.config.application ?: other.config.name
    // not sure if this.config == other.config good enough
    (this.config.name == other.config.name ) &&
    (this_app == other_app) &&
    (this.config.port == other.config.port) &&
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
    def env = svc.env ?: []

    [
      "apiVersion": "v1",
      "kind": "ReplicationController",
      "metadata" : [
        "name": svc.name,
        "labels": [
          "creator": "pipeline",
          "name": svc.name,
          "application": image_name
        ]
      ],
      "spec": [
        "replicas": numreplicas,
        "template": [
          "metadata": [
            "name": svc.name,
            "labels": [
              "creator": "pipeline",
              "name": svc.name
            ]
          ],
          "spec": [
            "containers": [
              [
                "name": svc.name,
                "image": image,
                "env": env,
                "ports": [
                  [ "containerPort": svc.port ]
                ]
              ]
            ],
            "nodeSelector": [
              "role": "minion"
            ]
          ]
        ]

      ]
    ]
  }

  def specToConfig(def spec) {
    def env = spec.spec?.template?.spec?.containers?.getAt(0)?.env ?: []
    def port  = spec.spec?.template?.spec?.containers?.getAt(0)?.ports?.getAt(0)?.containerPort ?: 80

    [
      "name": spec.metadata.name,
      "application": spec.metadata.labels.application,
      "port": port,
      "env": env
    ]
  }
}
