package com.pearson.deployment.kubernetes.thirdpartyresource

import com.pearson.deployment.kubernetes.AbstractKubeManager
import com.pearson.deployment.kubernetes.KubeAPI
import com.pearson.deployment.config.kubernetes.*
import com.pearson.deployment.config.bitesize.*

class MysqlWrapper extends AbstractKubeManager {
  static Class resourceClass = KubeThirdPartyMysqlResource

  MysqlWrapper(KubeAPI client, Service svc) {
    this.client = client

    this.resource = new KubeThirdPartyMysqlResource(
      apiVersion: "prsn.io/v1",
      kind: svc.type,
      metadata: [
        name: svc.name,
        namespace: svc.namespace
      ],
      spec: [
        version: svc.version,
        options: svc.options
      ]
    )
  }

  MysqlWrapper(KubeAPI client, KubeThirdPartyMysqlResource resource) {
    this.client = client
    this.resource = resource
  }

  boolean manage() {
    true
  }
  
  String namespace() {
    resource.namespace
  }
  @Override
  boolean equals(Object o) {
    if (o == null) {
      return false
    }

    if (!MysqlWrapper.class.isAssignableFrom(o.class)) {
      return false
    }

    def obj = (MysqlWrapper)o

    this.resource == obj.resource
  }
}
