package com.pearson.deployment.job

// import hudson.FilePath
import hudson.model.BuildListener
import hudson.model.AbstractBuild
// import hudson.remoting.VirtualChannel
import hudson.Launcher

import hudson.FilePath
// import hudson.FilePath.FileCallable

// import hudson.plugins.copyartifact.CopyArtifact
// import hudson.plugins.copyartifact.WorkspaceSelector

import com.pearson.deployment.*
import com.pearson.deployment.kubernetes.*
import com.pearson.deployment.config.*
import com.pearson.deployment.config.bitesize.*
import com.pearson.deployment.helpers.*
// import com.pearson.deployment.callable.WorkspaceReader


class DeployEnvironment implements Serializable {
  private static def DEFAULT_TIMEOUT = 300

  EnvironmentsBitesize config
  Environment environment

  private AbstractBuild build
  private LinkedHashMap definition
  private BuildListener listener
  private Launcher launcher
  private OutputStream log
  private String filename

  // need to move this to factory 
  private def cloudClientClass = KubeWrapper.class

  DeployEnvironment(AbstractBuild build, Launcher launcher, BuildListener listener, String filename, String envname) {
    this.build = build
    this.listener = listener
    this.launcher = launcher
    this.log = listener.getLogger()
    this.filename = filename

    FilePath fp = new FilePath(build.workspace, filename)
    this.config = EnvironmentsBitesize.readConfigFromPath(fp)

    this.environment = config?.getEnvironment(envname)
  }

  def deploy() {
    environment?.services.each { 
      deployService(it)
    }
  }

  private def deployService(Service svc) {

    String version = getServiceVersion(svc.name)
    if (version == null) {
      return
    }

    svc.project   = config.project
    svc.namespace = environment.namespace
    svc.version   = version

    KubeAPI client = getKubeAPI(svc.namespace)
    
    try {
      def deployment = new KubeDeploymentHandler(client, svc, log)
      def existing   = deployment.getHandler(svc.name)

      if (existing.svc.version != deployment.svc.version) {
        updateDeployment(deployment)
      }
    } catch (ResourceNotFoundException e) {
      createDeployment(deployment)
    }
  }

  private KubeAPI getKubeAPI(String namespace) {
    KubeAPI api = this.cloudClientClass.newInstance()
    api.setNamespace(namespace)
    api.log = log
    return api
  }

  private String getServiceVersion(String serviceName) {
    String appname = Helper.normalizeName(serviceName)
    String version = build.getEnvironment(listener).get("${appname}_VERSION".toString())

    if ( version == "" || version == "latest") {
      version = null
    }
    log.println "${serviceName} deploy: got version ${version}"
    return version
  }

  private void createDeployment(KubeDeploymentHandler deployment) {
    log.println "MUST CREATE DEPLOYMENT FOR ${svc.name}:${version}"
    deployment.create()
    watchDeploy(deployment)
  }

  private void updateDeployment(KubeDeploymentHandler deployment) {
    log.println "MUST UPDATE DEPLOYMENT FOR ${deployment.svc.name}:${deployment.svc.version}"
    deployment.update()
    watchDeploy(deployment)
  }

  private def watchDeploy(KubeDeploymentHandler deploy) {
    def timer = 0

    while (true) {
      if (deploy.watch() == 'success') {
        printOut("Deployment for ${deploy.svc.name} finished")
        break
      }
      timer = tick(timer)
      checkTimeout(deploy, timer)
      printOut(".")
    }
  }

  private int tick(int timer) {
    int step = 5 // seconds
    timer = timer + step
    sleep(step*1000)
    return timer
  }

  private void checkTimeout(KubeDeploymentHandler deploy, int timer) {
    // def timeout = deploy.svc.deployment?.timeout ?: DEFAULT_TIMEOUT
    def timeout = DEFAULT_TIMEOUT
    if (timer >= timeout) {
      throw new hudson.AbortException("Timeout reached, deployment failed")
    }
  }

  private void printOut(String str) {
    log.print(str)
    log.flush()
  }
}