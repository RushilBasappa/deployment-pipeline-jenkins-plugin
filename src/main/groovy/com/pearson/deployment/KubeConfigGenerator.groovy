package com.pearson.deployment

import org.yaml.snakeyaml.Yaml

class KubeConfigGenerator {
  String filename
  LinkedHashMap config
  String project
  String docker_registry

  KubeConfigGenerator(String project, LinkedHashMap cfg) {
    this.config   = cfg
    this.project = project
    this.docker_registry = System.getenv().DOCKER_REGISTRY ?: "bitesize-registry.default.svc.cluster.local:5000"
    // this.namespace = namespace
  }

  def setup() {
    config.services.each { service ->
      service.project = project
      service.docker_registry = docker_registry

      def rc = new KubeController(config.namespace, service)
      rc.createOrUpdate()

      def svc = new KubeService(config.namespace, service)
      svc.createOrUpdate()

      // createOrUpdateRC(service)
      // createOrUpdateSVC(service)

      def ingress = new KubeIngress(config.namespace, service)
      ingress.createOrUpdate()

      // createOrUpdateIngress(service)
    }

  }

  def createOrUpdateRC(s) {
    def w = new KubeWrapper('rc', config.namespace)
    try {
      def z = w.get(s.name)
      def yaml = new Yaml()
      def existingRC =  yaml.load(z)

      if (mustUpdateRC(existingRC, s)) {
        def rcFile = generateRcFile(s)
        println "${AnsiColors.green}Updating rc ${s.name}${AnsiColors.reset}"
        w.apply rcFile
      }

    } catch(all) {
      println all
      println "ReplicationController ${s.name} must be created"
      def rcFile = generateRcFile(s)

      w.create rcFile
    }
  }

  private def mustUpdateRC(def oldRC, def newRC) {
    // this is the only thing that we control in rc through
    // environments.bitesize
    def numreplicas = newRC.replicas ?: 1
    if ( oldRC.spec.replicas.toInteger() != numreplicas.toInteger()) {
      return true
    }

    return false
  }

  private def generateRcFile(def s) {
    def numreplicas = s.replicas ?: 1

    def contents
    contents = """\
                  apiVersion: v1
                  kind: ReplicationController
                  metadata:
                    name: ${s.name}
                    labels:
                      creator: pipeline
                      name: ${s.name}
                  spec:
                    replicas: ${numreplicas}
                    template:
                      metadata:
                        name: ${s.name}
                        labels:
                          creator: pipeline
                          name: ${s.name}
                      spec:
                        containers:
                          - name: ${s.name}
                            image: ${docker_registry}/${project}/${s.application ?: s.name}
                            ports:
                              - containerPort: ${s.port}
                        nodeSelector:
                          role: minion

    """.stripIndent()
    def wr = new File("/tmp/${config.name}-${s.name}-rc.yaml")
    wr << contents

    return "/tmp/${config.name}-${s.name}-rc.yaml"
  }

  def createOrUpdateSVC(s) {
    def w = new KubeWrapper('svc', config.namespace)
    try {
      def z = w.get(s.name)
      def yaml = new Yaml()
      def existingSvc =  yaml.load(z)

      if (mustUpdateSvc(existingSvc,s)) {
        println "${AnsiColors.green}Updating rc ${s.name}${AnsiColors.reset}"
        def svcFile = generateSvcFile(s)
        w.apply svcFile
      }
    } catch(all) {
      println "Service ${s.name} must be created"

      def svcFile = generateSvcFile(s)
      w.create svcFile
    }
  }

  private def generateSvcFile(s) {
    def contents
    contents = """\
                  apiVersion: v1
                  kind: Service
                  metadata:
                    name: ${s.name}
                    labels:
                      creator: pipeline
                      name: ${s.name}
                  spec:
                    ports:
                      - port: 80
                        protocol: "TCP"
                        targetPort: ${s.port}
                    selector:
                      name: ${s.name}
    """.stripIndent()
    def wr = new File("/tmp/${config.name}-${s.name}-svc.yaml")
    wr << contents
    return "/tmp/${config.name}-${s.name}-svc.yaml"
  }

  private def mustUpdateSvc(def oldSvc, def newSvc) {
    if ( ! oldSvc.spec.ports.equals(newSvc.ports)) {
      return true
    }
    return false
  }

}
