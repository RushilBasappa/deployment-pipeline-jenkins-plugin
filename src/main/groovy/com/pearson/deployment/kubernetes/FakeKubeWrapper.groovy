package com.pearson.deployment.kubernetes

import com.github.zafarkhaja.semver.Version

import com.pearson.deployment.config.kubernetes.*

class FakeKubeWrapper implements KubeAPI {
  OutputStream log
  String namespace
  private Version version

  Map<String, Map> store = [
    "service" : new LinkedHashMap(),
    "ingress" : new LinkedHashMap(),
    "thirdpartyresource": new LinkedHashMap(),
    "deployment": new LinkedHashMap()
  ]

  private String namespace

  FakeKubeWrapper() {
    this.namespace = 'default'
    version = Version.valueOf('1.4.4')
  }

  FakeKubeWrapper(String namespace) {
    this.namespace = namespace
    version = Version.valueOf('1.4.4')
  }

  void create(String kind, LinkedHashMap resource) {
    def kindStore = store.get(kind)

    if (kindStore.get(resource.metadata.name) != null) {
      throw new Exception("Cannot create, ${kind} already exist: ${resource.metadata.name}")
    }
    kindStore[resource.metadata.name] = resource
  }

  void create(AbstractKubeResource resource) {
    String kind = resource.class.kind
    def kindStore = store.get(kind)

    if (kindStore.get(resource.name)) {
      throw new Exception("Cannot create, ${kind} already exist}: ${resource.name}")
    } else {
      kindStore[resource.name] = resource.asMap()
    }
  }

  void apply(String kind, LinkedHashMap resource) {
    def kindStore = store.get(kind)
    kindStore[resource.metadata.name] = resource
  }

  void apply(AbstractKubeResource resource) {
    String kind = resource.class.kind
    def kindStore = store.get kind

    kindStore[resource.name] = resource.asMap()
  }

  void apply(AbstractKubeWrapper wrapper) {
    apply wrapper.resource
  }

  void setVersion(String v) {
    version = Version.valueOf(v)
  }

  Version version() {
    version
  }

  void setNamespace(String namespace) {
    this.namespace = namespace
  }

  boolean namespaceExist(String namespace) {
    namespace == this.namespace
  }

  String getVersion() {
    "v1.5.0"
  }

  AbstractKubeResource get(Class klass, String name) {
    def kindStore = store.get(klass.kind)
    def map = kindStore.get(name)
    if (map == null) {
      throw new ResourceNotFoundException("Cannot find ${klass.kind} ${name}")
    }
    klass.newInstance(map)
  }

  AbstractKubeResource get(String kl, String name) {
    def map = fetch(kl, name)
    def klass = AbstractKubeResource.classFromKind(map.kind)
    klass.newInstance(map)
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
