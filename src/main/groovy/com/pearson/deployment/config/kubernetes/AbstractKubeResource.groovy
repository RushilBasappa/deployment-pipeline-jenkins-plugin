package com.pearson.deployment.config.kubernetes

abstract class AbstractKubeResource {
  String kind
    // abstract static AbstractKubeResource loadFromString(String contents)

  Class classFromKind(String kind) {
    switch(kind) {
      case "mysql":
        return KubeThirdPartyWrapper;
      case "mongo":
        return KubeThirdPartyWrapper;

    }

  }
}
