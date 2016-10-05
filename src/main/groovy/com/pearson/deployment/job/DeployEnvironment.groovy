package com.pearson.deployment.job

import hudson.Launcher
import hudson.FilePath

import hudson.model.BuildListener
import hudson.model.AbstractBuild

import com.pearson.deployment.*
import com.pearson.deployment.kubernetes.*
import com.pearson.deployment.config.*
import com.pearson.deployment.config.bitesize.*
import com.pearson.deployment.helpers.*


class DeployEnvironment implements Serializable {
  private static Integer defaultTimeout = 300

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

  DeployEnvironment(
    AbstractBuild build,
    Launcher launcher,
    BuildListener listener,
    String filename,
    String envname) {

    this.build = build
    this.listener = listener
    this.launcher = launcher
    this.log = listener.getLogger()
    this.filename = filename

    FilePath fp = new FilePath(build.workspace, filename)
    this.config = EnvironmentsBitesize.readConfigFromPath(fp)

    this.environment = config?.getEnvironment(envname)
  }

  boolean deploy() {
    boolean changed

    environment?.services.each {      
      if (environment.deployment?.isBlueGreen()) {
        String active = environment.deployment.active
        String other = (active == "blue") ? "green" : "blue"       
      }
      if (! it.isThirdParty() ) {
        changed = deployService(it) ? true : changed
      }      
    }

    return true
  }

  private boolean deployService(Service svc, String deployTo=null) {
    String version = getServiceVersion(svc.name)

    if (version == null) {
      return
    }
    if (deployTo) {
      svc.name = "${svc.name}-${deployTo}"
    }
    runDeploy(svc, version)
  }

  private boolean runDeploy(Service svc, String version) {
    svc.project   = config.project
    svc.namespace = environment.namespace
    svc.version   = version

    KubeAPI client = getKubeAPI(svc.namespace)
    KubeDeploymentWrapper deployment = new KubeDeploymentWrapper(client, svc)

    try {
      if (deployment.mustUpdate()) {
        deployment.update()
        deployment.watch()        
        return true
      }

    } catch (ResourceNotFoundException e) {
      deployment.create()
      deployment.watch()
      return true
    }
    return false
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

  private void createDeployment(KubeDeploymentWrapper deployment) {
    log.println "MUST CREATE DEPLOYMENT FOR ${svc.name}:${version}"
    deployment.create()
    watchDeploy(deployment)
  }

  private void updateDeployment(KubeDeploymentWrapper deployment) {
    log.println "MUST UPDATE DEPLOYMENT FOR ${deployment.svc.name}:${deployment.svc.version}"
    deployment.update()
    watchDeploy(deployment)
  }

  private def watchDeploy(KubeDeploymentWrapper deploy) {
    def timer = 0

    while (true) {
      if (deploy.watch() == 'success') {
        printOut("Deployment for ${deploy.name} finished")
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

  private void checkTimeout(KubeDeploymentWrapper deploy, int timer) {
    if (timer >= deploy.deployTimeout) {
      throw new hudson.AbortException("Timeout reached, deployment failed")
    }
  }

  private void printOut(String str) {
    log.print(str)
    log.flush()
  }
}