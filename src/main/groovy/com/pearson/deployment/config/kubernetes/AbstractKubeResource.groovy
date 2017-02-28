package com.pearson.deployment.config.kubernetes


// import com.pearson.deployment.kubernetes

abstract class AbstractKubeResource {
  String kind
    // abstract static AbstractKubeResource loadFromString(String contents)

  def getKind() {
    if (this.kind) {
      return this.kind
    }
    return this.class.kind
  }

  static def build(LinkedHashMap o) {
    // switch(o.kind) {
    // case ["mysql", "Mysql"]:
    // case ["mongo", "Mongo"]:
    // }

    return new KubeThirdPartyGenericResource(o)
  }
}
