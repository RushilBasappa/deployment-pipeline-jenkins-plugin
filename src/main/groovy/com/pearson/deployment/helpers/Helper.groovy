package com.pearson.deployment.helpers

class Helper implements Serializable {
  public static dockerRegistry() {
    Map<String, String> env = System.getenv()
    env.get('DOCKER_REGISTRY') ?: "bitesize-registry.default.svc.cluster.local:5000"
  }
}
