package com.pearson.deployment.job

import com.pearson.deployment.*
import com.pearson.deployment.config.*
import com.pearson.deployment.config.bitesize.*
import com.pearson.deployment.builder.*
import com.pearson.deployment.kubernetes.*
import com.pearson.deployment.helpers.*
// import com.pearson.deployment.callable.WorkspaceReader

// import org.yaml.snakeyaml.Yaml
import hudson.FilePath
// import hudson.FilePath.FileCallable
import hudson.model.BuildListener
import hudson.model.AbstractBuild
// import hudson.remoting.VirtualChannel

import java.security.SecureRandom
// import java.math.BigInteger
// import java.io.*

class ServiceManage implements Serializable {
  EnvironmentsBitesize config
  boolean changed
  
  private AbstractBuild build
  private BuildListener listener
  private String filename
  private SecureRandom random
  private OutputStream log
  private Map<String, KubeEnvironmentManager> environmentManagers

  // need to move this to factory 
  private def cloudClientClass = KubeWrapper.class

  ServiceManage(AbstractBuild build, BuildListener listener, String filename) {
    this.build = build
    this.listener = listener
    this.changed = false
    this.log = listener.getLogger()
    this.random = new SecureRandom()
    this.filename = filename
    this.environmentManagers = new LinkedHashMap()

    FilePath fp = new FilePath(build.workspace, filename)
    try {
      this.config = EnvironmentsBitesize.readConfigFromPath(fp)
    } catch (FileNotFoundException e) {
      this.config = null
    }
  }

  def run() {
    config?.environments?.each {
      log.println "Configuring environment ${it.name}"
      KubeAPI client = getKubeAPI(it.namespace)
      KubeEnvironmentManager envManager = new KubeEnvironmentManager(client, config.project, it, log)
      environmentManagers[it.namespace] = envManager

      envManager.manage()
    }
  }

  KubeEnvironmentManager getEnvironmentManager(String name) {
    return environmentManagers.get(name)
  }

  private KubeAPI getKubeAPI(String namespace) {
    KubeAPI api = this.cloudClientClass.newInstance()
    api.setNamespace(namespace)
    api.log = log
    return api
  }

  def setCloudClient(def klass) {
    this.cloudClientClass = klass
  }

  String project() {
    config?.project
  }
}