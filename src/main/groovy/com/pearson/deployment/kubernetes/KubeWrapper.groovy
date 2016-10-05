package com.pearson.deployment.kubernetes

import org.yaml.snakeyaml.Yaml
import groovy.json.*

import com.pearson.deployment.config.kubernetes.*
import com.pearson.deployment.helpers.*

class KubeWrapper implements KubeAPI {
  String namespace
  OutputStream log

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
    File f = File.createTempFile(resource.class.kind, 'json', null)
    def compact = Helper.denull(resource.asMap())
    def output = JsonOutput.toJson(compact)
    f.write output
    try {
      exe("kubectl apply -f ${f.path} --validate=false")
    } catch (all) {
      println "Exception occured applying output: ${output}"
    }    
  }

  void apply(AbstractKubeWrapper wrapper) {
    apply wrapper.resource
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
