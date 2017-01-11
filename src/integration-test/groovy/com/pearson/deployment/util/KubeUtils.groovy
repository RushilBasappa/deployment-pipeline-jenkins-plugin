package com.pearson.deployment.util


import java.nio.file.Files
import io.fabric8.kubernetes.api.model.*
import io.fabric8.kubernetes.client.*
import io.fabric8.kubernetes.api.model.extensions.*
import org.yaml.snakeyaml.Yaml
import groovy.json.*
import org.ajoberstar.grgit.Grgit
import org.apache.commons.io.FileUtils

class KubeUtils {

  def client
  def jenkinsChartFolder
  Yaml yaml
  static private chartsGitRepo = 'git@github.com:pearsontechnology/kubernetes-charts.git'

  KubeUtils() {
    client = new DefaultKubernetesClient()
    jenkinsChartFolder = Files.createTempDirectory("charts")
    yaml = new Yaml()
  }

  def cleanup() {
    FileUtils.deleteDirectory(jenkinsChartFolder.toFile())
  }

  def deployJenkins(def nsName) {

    def grgit = Grgit.clone(
      dir: jenkinsChartFolder,
      uri: chartsGitRepo
    )

    def deployment = loadJenkinsDeployment(nsName)
    def serviceJenkins = loadJenkinsService(nsName)
    def serviceAptly = loadAptlyService(nsName)
    def jenkinsIngress = loadJenkinsIngress(nsName)

    // client.extensions().deployments().inNamespace(nsName).patch(deployment)
    // client.services().inNamespace(nsName).patch(serviceJenkins)
    // client.services().inNamespace(nsName).patch(serviceAptly)
    // client.extensions().ingress().inNamespace(nsName).patch(jenkinsIngress)
    client.resource(deployment).apply()
    client.resource(serviceJenkins).apply()
    client.resource(serviceAptly).apply()
    client.resource(jenkinsIngress).apply()
  }

  def deployGogs(def namespace) {
    def yaml = new Yaml()

    def secret = yaml.load(new File("src/integration-test/resources/gogs-ssh-secret.yaml").text)
    secret.metadata.namespace = namespace
    secret.data.publickey = System.getenv().GIT_PUBLIC_KEY.bytes.encodeBase64().toString()
    def cc = resourceFromMap(secret, Secret)
    client.resource(cc).apply()

    def deployment = yaml.load(new File("src/integration-test/resources/gogs-deployment.yaml").text)
    deployment.metadata.namespace = namespace
    def dc = resourceFromMap(deployment, Deployment)
    client.resource(dc).apply()

    def service = yaml.load(new File("src/integration-test/resources/gogs-service.yaml").text)
    service.metadata.namespace = namespace
    def sc = resourceFromMap(service, Service)
    client.resource(sc).apply()
  }

  private def loadJenkinsDeployment(String namespace) {
    def deployment = loadTemplate('jenkins-deployment.yaml.tmpl')
    def e = System.getenv()

    deployment.metadata.namespace = namespace
    deployment.spec.template.spec.containers[0].env = [
      [
        name: "JENKINS_ADMIN_USER",
        value: e.JENKINS_ADMIN_USER
      ],
      [
        name: "JENKINS_ADMIN_PASSWORD",
        value: e.JENKINS_ADMIN_PASSWORD
      ],
      [
        name: "DOCKER_REGISTRY",
        value: "bitesize-registry.default.svc.cluster.local:5000"
      ],
      [
        name: "SEED_JOBS_REPO",
        value: e.SEED_JOBS_REPO
      ],
      [
        name: "GIT_PRIVATE_KEY",
        value: e.GIT_PRIVATE_KEY
      ]
    ]
    deployment.spec.template.spec.containers[0].image = e.JENKINS_IMAGE

    resourceFromMap(deployment, Deployment)
  }

  private def loadAptlyService(String namespace) {
    def service = loadTemplate('jenkins-svc-aptly.yaml.tmpl')
    service.metadata.namespace = namespace
    resourceFromMap(service, Service)

  }

  private def loadJenkinsService(String namespace) {
    def service = loadTemplate('jenkins-svc-jenkins.yaml.tmpl')
    service.metadata.namespace = namespace
    resourceFromMap(service, Service)
  }

  private def loadJenkinsIngress(String namespace) {
    def ingress = loadTemplate('jenkins-ingress.yaml.tmpl')

    ingress.metadata.namespace = namespace
    ingress.spec.rules[0].host = "jenkins.sample-app.io"

    resourceFromMap(ingress, Ingress)
  }

  def createNamespaces(def namespaces) {
    namespaces.each { nsName ->
      def ns = client.namespaces().withName(nsName).get()
      if (ns == null) {
        def nsObj = new NamespaceBuilder()
                      .withNewMetadata()
                      .withName(nsName)
                      .endMetadata()
                      .build()

        client.namespaces().create(nsObj)
      } else {
        println "${ns.getMetadata().getName()} already exist"
      }
    }
  }

  private LinkedHashMap loadTemplate(String name) {
    def svdoc = new File("${jenkinsChartFolder}/jenkins/templates/${name}").text.replaceAll("%%",'')
    yaml.load(svdoc)
  }

  private resourceFromMap(LinkedHashMap mp, Class klass) {
    def js = JsonOutput.toJson(mp)
    def cj = getList(klass)
    cj = client.load(new ByteArrayInputStream(js.bytes)).get()
    cj.first()
  }

  public <T> List<T> getList(Class<T> requiredType) {
    return new ArrayList<T>();
}
}
