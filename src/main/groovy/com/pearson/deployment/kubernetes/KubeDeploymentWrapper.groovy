package com.pearson.deployment.kubernetes

import com.pearson.deployment.config.bitesize.*
import com.pearson.deployment.config.kubernetes.*
import com.pearson.deployment.helpers.*

class KubeDeploymentWrapper extends AbstractKubeWrapper {
  Integer deployTimeout = 300
  static Class resourceClass = KubeDeployment 

  KubeDeploymentWrapper(KubeAPI client, Service svc) {
    this.client = client

    this.resource = new KubeDeployment(
      metadata: [
        name: svc.name,
        namespace: svc.namespace,
        labels: [
          application: svc.application,
          project: svc.project,
          version: version(svc),
          creator: "pipeline"
        ]
      ],
      spec: [
        replicas: svc.replicas,
        template: [
          metadata: [
              labels: [
                application: svc.application,
                project: svc.project,
                version: version(svc),
                creator: "pipeline"
              ]
          ],
          spec: [
            containers: [
              [
                name: svc.name,
                image: image(svc),
                volumes: svc.volumes,
                ports: [
                  [ containerPort: svc.port ]
                ],
                env: svc.env
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
    def deployment = client.get KubeDeployment, resource.name
    if (deployment.allReplicasOnline()) {
      return 'success'
    }
    return 'running'
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
    def name = svc.application ?: svc.name
    "${Helper.dockerRegistry()}/${svc.project}/${name}:version(svc)"
  }

  boolean mustUpdate() {
    getVersion() != getRemoteVersion()
  }

  String version(Service svc) {
    if (svc.version) {
      return svc.version
    } else {
      getRemoteVersion()
    }
    // we should not be there
    return null
  }

  String getRemoteVersion() {
    try {
      def res = client.get KubeDeployment, name
      return res.labels['version']
    } catch (e) {
      return null
    }

  }

  String getVersion() {
    resource.labels['version']
  }

  def setVersion(String value) {
    resource.labels['version'] = value
  }
}