package com.pearson.deployment.kubernetes

class FakeKubeWrapper implements KubeAPI {
  OutputStream log
  String namespace

  Map<String, Map> store = [
    "service" : new LinkedHashMap(),
    "ingress" : new LinkedHashMap(),
    "thirdpartyresource": new LinkedHashMap(),
    "deployment": new LinkedHashMap()
  ]

  private String namespace

  FakeKubeWrapper() {
    this.namespace = 'default'
  }

  FakeKubeWrapper(String namespace) {
    this.namespace = namespace
  }

  void create(String kind, LinkedHashMap resource) {
    def kindStore = store.get(kind)

    if (kindStore.get(resource.metadata.name) != null) {
      throw new Exception("Cannot create, ${kind} already exist: ${resource.metadata.name}")
    }
    kindStore[resource.metadata.name] = resource
  }

  void apply(String kind, LinkedHashMap resource) {
    def kindStore = store.get(kind)
    kindStore[resource.metadata.name] = resource
  }

  void setNamespace(String namespace) {
    this.namespace = namespace
  }

  boolean namespaceExist(String namespace) {
    namespace == this.namespace
  }

  LinkedHashMap fetch(String kind, String name) {
    def kindStore = store.get(kind)
    def resource = kindStore.get(name)
    if ( resource == null) {
      throw new ResourceNotFoundException("Cannot find ${kind} ${name}")
    }
    return resource
  }

}