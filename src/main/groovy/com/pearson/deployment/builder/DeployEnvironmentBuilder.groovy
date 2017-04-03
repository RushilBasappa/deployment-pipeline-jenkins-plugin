package com.pearson.deployment.builder


import hudson.Launcher
import hudson.Extension
import hudson.FilePath
import hudson.model.BuildListener
import hudson.model.AbstractBuild
import hudson.tasks.Builder
import hudson.tasks.BuildStepDescriptor
import org.kohsuke.stapler.DataBoundConstructor
import org.kohsuke.stapler.DataBoundSetter

import com.pearson.deployment.*
import com.pearson.deployment.config.*

import com.pearson.deployment.config.bitesize.*
import com.pearson.deployment.kubernetes.*
import com.pearson.deployment.helpers.*


class DeployEnvironmentBuilder extends Builder {

  Environment environment
  private String environmentName
  private String filename
  private EnvironmentsBitesize config

  private AbstractBuild build
  private BuildListener listener
  private Launcher launcher

  private OutputStream log

  private def cloudClientClass = KubeWrapper.class

  @DataBoundConstructor
  DeployEnvironmentBuilder(String filename, String environmentName) {
    this.filename = filename
    this.environmentName = environmentName
  }

  @Override
  public boolean perform(AbstractBuild build, Launcher launcher, BuildListener listener) {
    FilePath fp = new FilePath(build.workspace, this.filename)
    this.config = EnvironmentsBitesize.readConfigFromPath(fp)
    this.log = listener.getLogger()
    this.build = build
    this.listener = listener
    this.launcher = launcher

    this.environment = this.config?.getEnvironment(this.environmentName)
    deploy()
  }

  @DataBoundSetter
  void setFilename(String value) {
    this.filename = filename
  }

  String getFilename() {
    this.filename
  }

  @DataBoundSetter
  void setEnvironmentName(String value) {
    this.environmentName  = value
  }

  String getEnvironmentName() {
    this.environmentName
  }

  boolean deploy() {
    boolean success = true

    environment?.services.each {
      String deployTo

      it.setupDeploymentMethod(environment)

      if (it.deployment?.isBlueGreen()) {
        String active = environment.deployment.active
        deployTo = (active == "blue") ? "green" : "blue"
      }
      if (! it.isThirdParty() ) {
        if (! deployService(it, deployTo)) {
          success = false
        }
      }
    }
    return success
  }

  private boolean deployService(Service svc, String deployTo=null) {
    String version = getServiceVersion(svc.name)
    log.println "Got service version ${version}\n"

    if (version == null) {
      return true
    }
    if (deployTo) {
      svc.application = svc.application ?: svc.name
      svc.name = "${svc.name}-${deployTo}"
    }
    runDeploy(svc, version)
  }

  private boolean runDeploy(Service svc, String version) {
    svc.project   = this.config.project
    svc.namespace = environment.namespace
    svc.version   = version

    KubeAPI client = getKubeAPI(svc.namespace)
    KubeDeploymentWrapper deployment = new KubeDeploymentWrapper(client, svc)

    try {
      if (deployment.mustUpdate()) {
        log.println "MUST UPDATE DEPLOYMENT FOR ${deployment.name}:${deployment.version}"
        deployment.update()
        watchDeploy(deployment)
        return true
      } else {
        log.println "NO CHANGES FROM ${deployment.name}:${deployment.version}"
        return true
      }

    } catch (ResourceNotFoundException e) {
      log.println "MUST CREATE DEPLOYMENT FOR ${deployment.name}:${deployment.version}"
      deployment.create()
      watchDeploy(deployment)
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
    String version = build.getEnvironment(this.listener).get("${appname}_VERSION".toString())

    if ( version == "" || version == "latest") {
      version = null
    }
    log.println "${serviceName} deploy: got version ${version}"
    return version
  }

  private def watchDeploy(KubeDeploymentWrapper deploy) {
    def timer = 0

    while (true) {
      if (deploy.watch() == 'success') {
        printOut("\nDeployment for ${deploy.name} finished\n\n")
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

  @Extension
  public static final class DescriptorImpl extends BuildStepDescriptor<Builder> {

    public DescriptorImpl() {
      load();
    }

    @Override
    public String getDisplayName() {
      return "Deploy environment";
    }

    @Override
    public boolean isApplicable(Class type) {
      return true;
    }
  }


}
