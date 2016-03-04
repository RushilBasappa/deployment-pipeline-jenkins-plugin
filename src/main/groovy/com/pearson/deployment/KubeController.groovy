package com.pearson.deployment

class KubeController extends KubeResource {
  KubeController(def namespace, def config, def image) {
    super('rc', namespace, config)
  }

  def compareTo(KubeController other) {
    // not sure if this.config == other.config good enough
    (this.config.name == other.config.name ) &&
    (this.config.application == other.config.application) &&
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
                "env": svc.env,
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
    [
      "name": spec.metadata.name,
      "application": spec.metadata.labels.application,
      "port": spec.spec.containers[0].ports[0].containerPort,
      "env": spec.spec.containers[0].env
    ]
  }
}
