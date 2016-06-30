package com.pearson.deployment.kubernetes

import org.yaml.snakeyaml.Yaml
import org.codehaus.groovy.runtime.StackTraceUtils

import java.util.logging.Level
import java.util.logging.Logger

class KubeResource {
  LinkedHashMap config
  private KubeWrapper kube
  LinkedHashMap attr
  String klass
  String namespace
  private def yaml
  def log

  private static final Logger LOG = Logger.getLogger(KubeResource.class.getName());

  KubeResource(String klass, String namespace, LinkedHashMap config, def logger=System.out) {
    this.config    = config
    this.namespace = namespace
    this.klass     = klass
    this.kube      = new KubeWrapper(klass, namespace)
    this.yaml      = new Yaml()
    this.log       = logger
  }

  def get(def name) {
    try {
      def data             = kube.get(name)
      def existingResource =  yaml.load(data)
      def existingConfig   = specToConfig(existingResource)
      return this.class.newInstance(namespace, existingConfig)
    } catch(all) {
      LOG.severe StackTraceUtils.deepSanitize(all).toString()
      return null
    }
  }

  def createOrUpdate() {
    def existing = get(config.name)

    if (existing) {
      if (!this.compareTo(existing)) {
        update()
        return true
      }
    } else {
      create()
      return true
    }
    return false
  }

  def create() {
    log.println "Must create new ${namespace}/${klass} resource"
    def contents = yaml.dumpAsMap(configToSpec())

    def writer   = new File(resourceFilename())
    writer.write contents
    kube.create(resourceFilename())
  }

  def update() {
    log.println "Must update ${namespace}/${klass}/${config.name} resource"
    def contents = yaml.dumpAsMap(configToSpec())

    def writer = new File(resourceFilename())
    writer.write contents
    kube.apply(resourceFilename())
  }

  def exist(def name) {
    try {
      def rs = get(name)
      return rs != null
    } catch(all) {
      return false
    }

  }

  // def configToSpec() {
  //   throw new RuntimeException("Method not implemented")
  // }

  private String resourceFilename() {
    def tmpDir = System.getProperty('java.io.tmpdir')
    "${tmpDir}/${namespace}-${klass}-${config.name}.yaml".toString()
  }
}
