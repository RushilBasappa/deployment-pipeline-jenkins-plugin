package com.pearson.deployment.builder;

import hudson.Launcher
import hudson.Extension
import hudson.model.BuildListener
import hudson.model.AbstractBuild
import hudson.tasks.Builder
import hudson.tasks.BuildStepDescriptor
import org.kohsuke.stapler.DataBoundConstructor
import org.kohsuke.stapler.DataBoundSetter

import java.util.logging.Logger

import hudson.util.ListBoxModel

import com.pearson.deployment.config.bitesize.*
import com.pearson.deployment.syspkg.*

public class BuildDependencyBuilder extends Builder {
  private static final Logger LOG = Logger.getLogger(BuildDependencyBuilder.class.getName());

  private BuildDependency dependency

  private String pkg
  private String version
  private String type
  private String location
  private String repository
  private String repositoryKey


  @DataBoundConstructor
  public BuildDependencyBuilder(String pkg, String version, String type, String location, String repository, String repository_key) {
    // this.pkg = dep.pkg
    // this.version = dep.version
    // this.type = dep.type
    // this.location = dep.location
    // this.repository = dep.repository
    // this.repositoryKey = dep.repository_key
    this.pkg = pkg
    this.version = version
    this.type = type
    this.location = location
    this.repositoryKey = repository_key
    this.repository = repository
    
    dependency = new BuildDependency(      
      pkg: pkg,
      version: version,
      type: type,
      location: location,
      repository: repository,
      repository_key: repositoryKey
    )
  }

  @Override
  public boolean perform(AbstractBuild build, Launcher launcher, BuildListener listener) {
    def log = listener.getLogger()

    try {
      def installer = PackageInstallerFactory.getInstaller(build, launcher, listener, dependency)
      installer.install()
    } catch (Exception e) {
      e.printStackTrace()
      return false;
    }
    return true;
  }

  @Extension
  public static final class DescriptorImpl extends BuildStepDescriptor<Builder> {

    public DescriptorImpl() {
      load();
    }

    public ListBoxModel doFillTypeItems() {      
      ListBoxModel m = new ListBoxModel()
      m.add("Debian package", "debian-package")
      m.add("Gem package", "gem-package")
      m.add("Pip package", "pip-package")       
      return m
    }

    @Override
    public String getDisplayName() {
      return "Build Dependency";
    }

    @Override
    public boolean isApplicable(Class type) {
      return true;
    }
  }

  @DataBoundSetter
  void setPkg(String value) {
    this.pkg = value
  }

  String getPkg() {
    this.pkg
  }

  @DataBoundSetter
  void setVersion(String value) {
    this.version = value
  }

  String getVersion() {
    this.version
  }
  
  @DataBoundSetter
  void setType(String value) {
    this.type = value
  }

  String getType(){
    this.type
  }

  @DataBoundSetter
  void setLocation(String value) {
    this.location = value
  }

  String getLocation() {
    this.location
  }

  @DataBoundSetter
  void setRepository(String value) {
    this.repository = value
  }

  String getRepository() {
    this.repository
  }

  @DataBoundSetter
  void setRepositoryKey(String value) {
    this.repositoryKey = value
  }

  String getRepositoryKey() {
    this.repositoryKey
  }
}
