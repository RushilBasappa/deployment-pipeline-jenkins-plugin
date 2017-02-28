package com.pearson.deployment.kubernetes

import com.pearson.deployment.config.bitesize.*
import com.pearson.deployment.config.kubernetes.*
// This class is used as a generic class for TPRs in kubernetes
// >=1.3.0. It tries to build out generic TPR object
class KubeThirdPartyInstanceWrapper extends AbstractKubeWrapper {
  static Class resourceClass = KubeThirdPartyGenericResource

  KubeThirdPartyInstanceWrapper(KubeAPI client, Service svc) {
    this.client = client

    this.resource = new KubeThirdPartyGenericResource(
      apiVersion: "prsn.io/v1",
      kind: svc.type.capitalize(),
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



  KubeThirdPartyInstanceWrapper(KubeAPI client, KubeThirdPartyGenericResource resource) {
    this.client = client
    this.resource = resource
  }

  @Override
  boolean equals(Object o) {
    if (o == null) {
      return false
    }

    if (!KubeThirdPartyInstanceWrapper.class.isAssignableFrom(o.class)) {
      return false
    }

    def obj = (KubeThirdPartyInstanceWrapper)o

    this.resource == obj.resource
  }

}
