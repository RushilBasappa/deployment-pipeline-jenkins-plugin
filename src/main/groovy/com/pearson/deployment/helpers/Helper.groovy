package com.pearson.deployment.helpers

class Helper implements Serializable {
  public static String dockerRegistry() {
    Map<String, String> env = System.getenv()
    env.get('DOCKER_REGISTRY') ?: "custom-registry"
  }

  public static String normalizeName(String name) {
    name.replaceAll("-","_")
  }

  public static String addHostPrefix(String host, String prefix) {
    def splitted = host.tokenize(".")
    splitted[0] = splitted[0] + prefix
    splitted.join(".")
  }

  public static def denull(obj) {
    if(obj instanceof Map) {
      obj.collectEntries{ k, v ->
        if(v) [(k): denull(v)] else [:]
      }
    } else if(obj instanceof List) {
      obj.collect { denull(it) }.findAll { it != null }
    } else {
      obj
    }
  }
}
