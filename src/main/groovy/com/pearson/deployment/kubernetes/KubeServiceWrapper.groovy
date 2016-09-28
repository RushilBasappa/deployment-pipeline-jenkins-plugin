package com.pearson.deployment.kubernetes

import com.pearson.deployment.config.bitesize.Service
import com.pearson.deployment.config.kubernetes.KubeService

class KubeServiceWrapper extends AbstractKubeWrapper {

  KubeServiceWrapper(KubeAPI client, Service svc) {
    this.client = client
    this.resource = new KubeService(
      metadata: [
        name: svc.name,
        namespace: svc.namespace,
        labels: [
          name: svc.name,
          project: svc.project,
          application: svc.application
        ]
      ],
      spec: [
        selector: [
          name: svc.name,
          project: svc.project
        ],
        ports: [
          [
            port: svc.port,
            protocol: "TCP",
            name: "tcp-port",
            targetPort: svc.port
          ]
        ]
      ]
    )
  }

  KubeServiceWrapper(KubeAPI client, KubeService resource) {
    this.client = client
    this.resource = resource
  }

  @Override
  boolean equals(Object o) {
    if ( o == null) {
      return false
    }

    if (!KubeServiceWrapper.class.isAssignableFrom(o.class)) {
      return false
    }

    def obj = (KubeServiceWrapper)o

    (resource == obj.resource)
  }

  void setPort(Integer value) {
    resource.ports[0].port = value
  }

  Integer getPort() {
    resource.ports[0].port
  }

  void setName(String value) {
    resource.name = value
  }

  String getName() {
    resource.name
  }

  void setProject(String value) {
    resource.labels['project'] = value
  }

  String getProject() {
    resource.labels['project']
  }

  void setApplication(String value) {
    resource.labels['application'] = value
  }

  String getApplication() {
    resource.labels['application']
  }

  void setNamespace(String value) {
    resource.namespace = value
  }

  String getNamespace() {
    resource.namespace
  }
}