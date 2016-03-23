package com.pearson.deployment
import org.yaml.snakeyaml.Yaml
import org.codehaus.groovy.runtime.StackTraceUtils

class KubeResource {
  LinkedHashMap config
  private KubeWrapper kube
  LinkedHashMap attr
  String klass
  String namespace
  private def yaml

  KubeResource(String klass, String namespace, LinkedHashMap config) {
    this.config = config
    this.namespace = namespace
    this.klass = klass
    this.kube = new KubeWrapper(klass, namespace)
    this.yaml = new Yaml()
  }

  def get(def name) {
    try {
      def data = kube.get(name)
      def existingResource =  yaml.load(data)
      def existingConfig = specToConfig(existingResource)
      return this.class.newInstance(namespace, existingConfig)
    } catch(all) {
      println StackTraceUtils.deepSanitize(all)
      return null
    }
  }

  def createOrUpdate() {
    def existing = get(config.name)

    if (existing) {
      if (!this.compareTo(existing)) {
        update()
      }
    } else {
      create()
    }
  }

  def create() {
    println "Must create new ${namespace}/${klass} resource"
    def contents = yaml.dumpAsMap(configToSpec())

    def writer = new File(resourceFilename())
    writer.write contents
    kube.create(resourceFilename())
  }

  def update() {
    println "Must update new ${namespace}/${klass} resource"
    def contents = yaml.dumpAsMap(configToSpec())

    def writer = new File(resourceFilename())
    writer.write contents
    kube.apply(resourceFilename())
  }

  // def configToSpec() {
  //   throw new RuntimeException("Method not implemented")
  // }

  private def resourceFilename() {
    "/tmp/${namespace}-${klass}-${config.name}.yaml"
  }
}
