package com.pearson.deployment.job

import hudson.FilePath
import hudson.model.BuildListener
import hudson.model.AbstractBuild

import com.pearson.deployment.*
import com.pearson.deployment.kubernetes.*
import com.pearson.deployment.config.*
import com.pearson.deployment.helpers.*


class DeployEnvironment implements Serializable {
  private static def DEFAULT_TIMEOUT = 300

  private AbstractBuild build
  private EnvironmentConfig envConfig
  private LinkedHashMap definition
  private BuildListener listener
  private def out

  DeployEnvironment(AbstractBuild build, BuildListener listener, String filename, String envname) {
    this.build = build
    this.listener = listener
    FilePath fp = new FilePath(build.getWorkspace(), filename)
    String contents = fp.readToString()

    this.out = listener.getLogger()

    envConfig = new EnvironmentConfig(contents, true)
    definition = envConfig.getEnvironment(envname)
  }

  def deploy() {
    for (def i = 0; i < definition.services?.size(); i++ ) {
      deployService(definition.services[i])
    }
  }

  private def deployService(def svc) {
    def appname_normalized = svc.name.replaceAll("-","_")
    svc.version = build.getEnvironment(listener).get("${appname_normalized}_VERSION".toString())
    svc.project = envConfig.attributes.project
    svc.docker_registry = Helper.dockerRegistry()

    def deployment = new KubeDeployment(definition.namespace, svc)

    if (deployment.exist(svc.name)) {
      def oldDeployment = deployment.get(svc.name)
      if (oldDeployment.config.version == svc.version) {
        out.println  "SAME VERSION ${svc.version} FOUND FOR ${svc.name}"
        out.flush()
      } else {
        out.println "MUST UPDATE ${svc.name} FROM ${oldDeployment.config.version} TO ${svc.version}"
        out.flush()
        updateService(deployment, svc)
      }
    } else {
      out.println "MUST CREATE ${svc.name}"
      out.flush()
      createService(deployment, svc)
    }
  }

  private def updateService(KubeDeployment deployment, LinkedHashMap service) {
    deployment.update()
    watchDeploy(deployment, service)
  }

  private def createService(KubeDeployment deployment, LinkedHashMap service) {
    deployment.create()
    watchDeploy(deployment, service)
  }

  private def watchDeploy(KubeDeployment deployment, LinkedHashMap service) {
    def timer = 0
    def step = 5 // seconds
    def timeout = service.deployment?.timeout ?: DEFAULT_TIMEOUT

    while (true) {
      def result = deployment.watch()
      def status = deployment.done(result?.config)
      if (status == 'success') {
        out.println "Deployment for ${service.name} finished"
        out.flush()
        break
      }

      timer = timer + step
      sleep(step*1000)

      if ( timer >= timeout) {
        throw new hudson.AbortException("Timeout reached, deployment failed")
      } else {
        out.println "."
        out.flush()
      }
    }
  }
}
