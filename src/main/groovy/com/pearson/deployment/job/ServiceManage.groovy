package com.pearson.deployment.job

import com.pearson.deployment.*
import com.pearson.deployment.config.*
import com.pearson.deployment.config.bitesize.*
import com.pearson.deployment.builder.*
import com.pearson.deployment.kubernetes.*
import com.pearson.deployment.helpers.*
import com.pearson.deployment.callable.WorkspaceReader

import org.yaml.snakeyaml.Yaml
import hudson.FilePath
import hudson.FilePath.FileCallable
import hudson.model.BuildListener
import hudson.model.AbstractBuild
import hudson.remoting.VirtualChannel

import java.security.SecureRandom
import java.math.BigInteger
import java.io.*

class ServiceManage implements Serializable {
  EnvironmentsBitesize config
  boolean changed
  
  private AbstractBuild build
  private BuildListener listener
  private String filename
  private SecureRandom random
  private OutputStream log

  ServiceManage(AbstractBuild build, BuildListener listener, String filename) {
    this.build = build
    this.listener = listener
    this.changed = false
    this.log = listener.getLogger()
    this.random = new SecureRandom()
    this.filename = filename

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
      manage(it)
    }
  }

  private def manage(Environment environment) {
    environment.services?.each { service ->
      service.project = project()
      service.namespace = environment.namespace

      def kube = new KubeManager(service, log)
      def ch = kube.manage()
      changed = ch ?: changed
    }
    log.println "Changed: ${changed}"
  }

  String project() {
    config?.project
  }
}