package com.pearson.deployment.helpers

class Helper implements Serializable {
  public static String dockerRegistry() {
    Map<String, String> env = System.getenv()
    env.get('DOCKER_REGISTRY') ?: "custom-registry"
  }

  public static String aptlyRepo() {
    Map<String, String> env = System.getenv()
    env.get('APTLY_REPO') ?: "http://apt/"
  }

  public static String normalizeName(String name) {
    name.replaceAll("-","_")
  }

  public static String addHostPrefix(String host, String prefix) {
    def splitted = host.tokenize(".")
    splitted[0] = splitted[0] + prefix
    splitted.join(".")
  }

  public static boolean equalMaps(HashMap a, HashMap b) {
    def a1 = a.findAll{ it.value && it.value != "" }
    def b1 = b.findAll{ it.value && it.value != ""}
    a1 == b1
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
