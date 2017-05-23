package com.pearson.deployment.kubernetes

import com.pearson.deployment.config.bitesize.Service
import com.pearson.deployment.config.kubernetes.KubeService
import com.pearson.deployment.config.kubernetes.KubeServicePort

class KubeServiceWrapper extends AbstractKubeWrapper {
  static Class resourceClass = KubeService

  KubeServiceWrapper(KubeAPI client, Service svc) {
    this.client = client

    def selector = svc.selector ?: svc.name
    def app = svc.application ?: svc.name

    def ports = svc.ports.collect{ v ->
      [
        name: "tcp-port-${v}",
        protocol: "TCP",
        port: v,
        targetPort: v
      ]
    }
    this.resource = new KubeService(
      metadata: [
        name: svc.name,
        namespace: svc.namespace,
        labels: [
          name: svc.name,
          project: svc.project,
          application: app,
          creator: "pipeline"
        ]
      ],
      spec: [
        selector: [
          name: selector,
          project: svc.project,
          application: app
        ],
        ports: ports
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

    (o.ports == obj.ports) &&
    (resource == obj.resource)
  }

  void setPorts(ArrayList<Integer> values) {
    def v = values.collect {p ->
      new KubeServicePort(
        port: p,
        protocol: "TCP",
        name: "tcp-port-${p}",
        targetPort: p
      )
    }
    resource.ports = v
  }

  ArrayList<KubeServicePort> getPorts() {
    resource.ports
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
