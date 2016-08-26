package com.pearson.deployment.helpers

class Helper implements Serializable {
  public static String dockerRegistry() {
    Map<String, String> env = System.getenv()
    env.get('DOCKER_REGISTRY') ?: "bitesize-registry.default.svc.cluster.local:5000"
  }

  public static String normalizeName(String name) {
    name.replaceAll("-","_")
  }

  public static String addHostPrefix(String host, String prefix) {
    def splitted = host.tokenize(".")
    splitted[0] = splitted[0] + prefix
    splitted.join(".")
  }
}
