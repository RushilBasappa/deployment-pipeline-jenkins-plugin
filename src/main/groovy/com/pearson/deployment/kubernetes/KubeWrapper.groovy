package com.pearson.deployment.kubernetes

import com.github.zafarkhaja.semver.Version
import org.yaml.snakeyaml.Yaml
import groovy.json.*

import com.pearson.deployment.config.kubernetes.*
import com.pearson.deployment.helpers.*

import groovyx.net.http.HTTPBuilder

class KubeWrapper implements KubeAPI {
  String namespace
  OutputStream log

  private Version kubeVersion

  KubeWrapper() {
    this.namespace = 'default'
  }

  KubeWrapper(String namespace, OutputStream log=System.out) {
    this.namespace = namespace
    this.log = log
  }

  LinkedHashMap fetch(String kind, String name) {
    try {
      String result = exe("kubectl get ${kind} ${name} --namespace=${namespace} -o yaml")
      Yaml yaml = new Yaml()
      yaml.load(result)
    } catch (all) {
      throw new ResourceNotFoundException("Cannot find ${kind} ${name}")
    }
  }

  AbstractKubeResource get(Class klass, String name) {
    try {
      String result = exe("kubectl get ${klass.kind} ${name} --namespace=${namespace} -o yaml")
      Yaml yaml = new Yaml()
      return klass.newInstance(yaml.load(result))
    } catch (all) {
      throw new ResourceNotFoundException("Cannot find ${klass.kind} ${name}")
    }
  }

  AbstractKubeResource get(String kl, String name) {
    def map = fetch(kl, name)
    return AbstractKubeResource.build(map)

  }

  void create(String kind, LinkedHashMap resource) {
    String filename = writeSpecFile(resource)
    exe("kubectl create -f ${filename} --namespace=${namespace} --validate=false")
  }

  void create(AbstractKubeResource resource) {
    apply resource
  }

  void apply(String kind, LinkedHashMap resource) {
    String filename = writeSpecFile(resource)
    exe("kubectl apply -f ${filename} --namespace=${namespace} --validate=false")
  }

  void apply(AbstractKubeResource resource) {
    File f = File.createTempFile(resource.kind, '.json', null)
    def compact = Helper.denull(resource.asMap())
    def output = JsonOutput.toJson(compact)
    def ns = resource.namespace

    def applystr = "-f ${f.path} --validate=false"
    if (ns) {
      applystr = "${applystr} --namespace ${ns}"
    }

    // println "Kind: ${resource.kind}"

    def cmd = "kubectl apply ${applystr}"
    f.write output
    if (resource.kind == "thirdpartyresource") {
      def token = new File("/var/run/secrets/kubernetes.io/serviceaccount/token").text
      def masterHost = System.env.KUBERNETES_SERVICE_HOST

      def url = "https://${masterHost}/apis/extensions/v1beta1/namespaces/${ns}/thirdpartyresources"
      def headers = """-H "Authorization: Bearer ${token}" -H "Accept: application/json" -H "Content-Type: application/json" """
      cmd = "curl -k ${headers} -d @${f.absolutePath} ${url}"

      // def http = new HTTPBuilder("https://${masterHost}")
      // http.ignoreSSLIssues()
      //
      // http.setHeaders([
      //   "Authorization": "Bearer ${token}",
      //   "Accept": "application/json",
      //   "Content-Type": "application/json"
      // ])
      //
      // http.post(
      //   path: "/apis/extensions/v1beta1/namespaces/${ns}/thirdpartyresources",
      //   body: output
      // ) { resp ->
      //   println "POST Success: ${resp.statusLine}"
    }

    // } else
    try {
      exe(cmd)
      f.delete()
    } catch (all) {
      println "Exception occured applying '${cmd}', output: ${output}"

    // }
    }
  }

  void apply(AbstractKubeWrapper wrapper) {
    apply wrapper.resource
  }

  Version version() {
    if (this.kubeVersion) {
      return this.kubeVersion
    }

    def output = exe("kubectl version")
    def serverLine =  (output =~ /.*Server.*/)[0]
    def matcher =  ( serverLine =~ /.*GitVersion:"v(.*?)",.*/)
    if (matcher.matches()) {
      this.kubeVersion = Version.valueOf(matcher[0][1])
      return this.kubeVersion
    } else {
      return "0.0.0"
    }
  }

  void setNamespace(String namespace) {
    this.namespace = namespace
  }

  boolean namespaceExist(String namespace) {
    try {
      exe("kubectl get ns ${namespace}")
    } catch(Exception e) {
      return false
    }
    true
  }

  protected def exe(cmd) {
    Process command
    def c = appendShellPrefix(cmd)
    command = c.execute()
    command.waitFor()
    def errOutput = command.err.text
    if (errOutput) {
      throw new Exception("Error executing '${cmd}': ${errOutput}")
    }
    return command.text
  }

  private def appendShellPrefix(String cmd) {
    String[] commandArray = new String[3]
    commandArray[0] = "sh"
    commandArray[1] = "-c"
    commandArray[2] = cmd
    return commandArray
  }

  private String writeSpecFile(LinkedHashMap contents) {
    Yaml yaml = new Yaml()
    String output = yaml.dumpAsMap(contents)
    String filename = resourceFilename(contents)
    def writer = new File(filename)
    writer.write output

    return filename
  }

  private String resourceFilename(LinkedHashMap contents) {

    def tmpDir = System.getProperty('java.io.tmpdir')
    "${tmpDir}/${namespace}-${contents.Kind}-${contents.metadata.name}.yaml".toString()
  }
}
