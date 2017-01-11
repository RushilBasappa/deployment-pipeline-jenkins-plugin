package com.pearson.deployment.kubernetes.thirdpartyresource

import com.pearson.deployment.kubernetes.AbstractKubeManager
import com.pearson.deployment.kubernetes.KubeAPI
import com.pearson.deployment.config.bitesize.*
import com.pearson.deployment.config.kubernetes.*

class MongoWrapper extends AbstractKubeManager {
  static Class resourceClass = KubeThirdPartyMongoResource

  MongoWrapper(KubeAPI client, Service svc) {
    this.client = client

    this.resource = new KubeThirdPartyMongoResource(
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

  MongoWrapper(KubeAPI client, KubeThirdPartyMongoResource resource) {
    this.client = client
    this.resource = resource
  }

  boolean manage() {
    true
  }

  @Override
  boolean equals(Object o) {
    if (o == null) {
      return false
    }

    if (!MongoWrapper.class.isAssignableFrom(o.class)) {
      return false
    }

    def obj = (MongoWrapper)o

    this.resource == obj.resource
  }
}
