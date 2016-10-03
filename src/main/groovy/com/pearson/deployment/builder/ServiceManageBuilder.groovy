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


import java.security.SecureRandom
// import java.math.BigInteger
// import java.io.*

//  jobs/ServiceManage.groovy will end up here

public class ServiceManageBuilder extends Builder {
  private String filename = null;

  EnvironmentsBitesize config
  boolean changed
  
  private AbstractBuild build
  private BuildListener listener
  private SecureRandom random
  private OutputStream log
  private Map<String, KubeEnvironmentManager> environmentManagers

  // need to move this to factory 
  private def cloudClientClass = KubeWrapper.class

  @DataBoundConstructor
  public ServiceManageBuilder(String filename) {
      this.filename = filename;
  }

  @Override
  public boolean perform(AbstractBuild build, Launcher launcher, BuildListener listener) {
      // this is where you 'build' the project
      // since this is a dummy, we just say 'hello world' and call that a build

      // this also shows how you can consult the global configuration of the builder
    build = build
    listener = listener
    changed = false
    log = listener.getLogger()
    random = new SecureRandom()
    environmentManagers = new LinkedHashMap()

    FilePath fp = new FilePath(build.workspace, filename)

    try {
      config = EnvironmentsBitesize.readConfigFromPath(fp)

      config.environments?.each {
        log.println "Configuring environment ${it.name}"
        KubeAPI client = getKubeAPI(it.namespace)
        KubeEnvironmentManager envManager = new KubeEnvironmentManager(client, config.project, it, log)
        environmentManagers[it.namespace] = envManager

        envManager.manage()
      }
      
    } catch (Exception e) {
      e.printStackTrace()
      return false
    }
    true
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

  @Extension
  public static final class DescriptorImpl extends BuildStepDescriptor<Builder> {

    public DescriptorImpl() {
      load();
    }

    @Override
    public String getDisplayName() {
      return "PaaS Service Manager";
    }

    @Override
    public boolean isApplicable(Class type) {
      return true;
    }
  }


  String getFilename() {
    return filename
  }

  @DataBoundSetter
  void setFilename(String value) {
    this.filename = value
  }
}
