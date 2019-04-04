package com.pearson.deployment.kubernetes

import com.pearson.deployment.config.bitesize.*
import com.pearson.deployment.config.kubernetes.*
import com.pearson.deployment.helpers.*

class KubeDeploymentWrapper extends AbstractKubeWrapper {
  Integer deployTimeout = 300
  static Class resourceClass = KubeDeployment

  KubeDeploymentWrapper(KubeAPI client, Service svc) {
    this.client = client

    def probe = []
    def volumeMounts = []
    def volumeClaims = []
    def ports = []

    if (svc.health_check) {
      probe = [
        exec: [
          command: svc.health_check?.command
        ],
        initialDelaySeconds: svc.health_check.initial_delay,
        timeoutSeconds: svc.health_check.timeout
      ]
    }

    if (svc.volumes) {
      volumeMounts = svc.volumes.collect{ v ->
        [ name: v.name, mountPath: v.path ]
      }

      volumeClaims = svc.volumes.collect{ v ->
        [
          name: v.name,
          persistentVolumeClaim: [
            claimName: v.name
          ]
        ]
      }
    }

    ports = svc.ports.collect { p -> [ containerPort: p ] }

    this.resource = new KubeDeployment(
      metadata: [
        name: svc.name,
        namespace: svc.namespace,
        labels: [
          name: svc.name,
          application: svc.application,
          project: svc.project,
          version: version(svc),
          creator: "pipeline"
        ]
      ],
      spec: [
        replicas: svc.replicas,
        selector: [
          matchLabels: [
            name: svc.name
          ]
        ],
        template: [
          metadata: [
              labels: [
                name: svc.name,
                application: svc.application,
                project: svc.project,
                version: version(svc),
                creator: "pipeline"
              ]
          ],
          spec: [
            nodeSelector: [
              role: "minion"
            ],
            volumes: volumeClaims,
            containers: [
              [
                name: svc.name,
                image: image(svc),
                volumeMounts: volumeMounts,
                ports: ports,
                env: svc.env,
                livenessProbe: probe
              ]
            ]
          ]
        ]
      ],
      status: [
        updatedReplicas: svc.updated_replicas,
        availableReplicas: svc.available_replicas
      ]
    )
  }

  KubeDeploymentWrapper(KubeAPI client, KubeDeployment r) {
    this.client = client
    this.resource = r
  }

  String watch() {
    try {
      def deployment = client.get KubeDeployment, resource.name
      if (deployment.allReplicasOnline()) {
        return 'success'
      }
    } catch (ResourceNotFoundException e) {
    }
    return 'running'
  }

  String status() {
    def deployment = client.get KubeDeployment, resource.name
    "Updated: ${deployment.updatedReplicas}, Available: ${deployment.availableReplicas}"
  }

  @Override
  boolean equals(Object obj) {
    if (obj == null) {
      return false
    }

    if (!KubeDeploymentWrapper.class.isAssignableFrom(obj.class)) {
      return false
    }

    (this.resource == obj.resource)
  }

  String image(Service svc) {
    "${Helper.dockerRegistryBaseImages()}/${svc.project}/${svc.application}:${version(svc)}"
  }

  boolean mustUpdate() {
    getVersion() != getRemoteVersion(resource.name)
  }

  String version(Service svc) {
    if (svc.version) {
      return svc.version
    } else {
      return getRemoteVersion(svc.name)
    }
    // we should not be there
    return null
  }

  String getRemoteVersion(String name) {
    def res = client.get KubeDeployment, name
    return res.labels['version']
  }

  String getVersion() {
    resource.labels['version']
  }

  String getApplication() {
    resource.labels['application']
  }

  String getName() {
    resource.labels['name']
  }

  def setVersion(String value) {
    resource.labels['version'] = value
  }
}
