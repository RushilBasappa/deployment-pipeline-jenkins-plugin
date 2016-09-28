package com.pearson.deployment.kubernetes

import com.pearson.deployment.config.bitesize.*
import com.pearson.deployment.config.kubernetes.*
import com.pearson.deployment.helpers.*

class KubeDeploymentWrapper extends AbstractKubeWrapper {

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
        replicas: replicas,
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

  def watch() {

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

  String version(Service svc) {
    if (svc.version) {
      return svc.version
    } else {
      try {
        // we need to retrieve client from somewhere
        def res = client.fetch 'deployment', svc.name
        svc.version = result.metadata.labels.version
        return svc.version
      } catch (e) {

      }      
    }
    // we should not be there
    return null
  }

  def getVersion() {
    resource.labels['version']
  }

  def setVersion(String value) {
    resource.labels['version'] = value
  }
}