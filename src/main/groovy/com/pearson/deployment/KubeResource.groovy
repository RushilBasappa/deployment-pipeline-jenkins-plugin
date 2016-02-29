package com.pearson.deployment
import org.yaml.snakeyaml.Yaml

class KubeResource {
  LinkedHashMap config
  private KubeWrapper kube
  LinkedHashMap attr

  KubeResource(String klass, LinkedHashMap config) {
    this.config = config
    this.klass = klass
    this.kube = new KubeWrapper(klass, config.namespace)
  }

  def get(def name) {
    try {
      def data = kube.get(s.name)
      def yaml = new Yaml()
      def existingResource =  yaml.load(data)
      return new KubeResource(klass, specToConfig(existingResource))
      // return existingResource
    } catch(all) {
      return null
    }
  }

  def createOrUpdate() {
    def existing = get(config.name)

    if (existing) {
      if (existing == config) {
        update()
      }
    } else {
      create()
    }
  }

  def create() {

  }

  def update() {

  }
}
