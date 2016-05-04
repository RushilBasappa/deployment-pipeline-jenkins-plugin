package com.pearson.deployment.kubernetes

import java.security.SecureRandom
import java.math.BigInteger

class KubeThirdPartyResource extends KubeResource {

  private SecureRandom random

  KubeThirdPartyResource( def namespace, def config) {
    random = new SecureRandom()
    super('thirdpartyresource', namespace, config)
  }

  def compareTo(def other) {
    // not sure if this.config == other.config good enough
    (this.config.name      == other.config.name ) &&
    (this.config.namespace == other.config.namespace)
  }


  def configToSpec(def s) {
    def svc = s

    if (s == null ) {
      svc = config
    }

    if (svc.name == null) {
      svc.name = new BigInteger(64, random).toString(32);
    }
    
    [
      "apiVersion": "extensions/v1beta1",
      "kind": "ThirdPartyResource",
      "metadata" : [
        "name": svc.name,
        "namespace": svc.namespace,
        "labels": [
          "creator": "pipeline",
          "name": svc.name
        ]
      ]
    ]
  }

  def specToConfig(def spec) {
    [
      "name": spec.metadata.name,
      "port": spec?.spec?.ports?.getAt(0)?.port ?: 80
    ]
  }
}
