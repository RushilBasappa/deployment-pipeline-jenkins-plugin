package com.pearson.deployment.builder;

import java.io.IOException;

import hudson.Launcher;
import hudson.Extension;
import hudson.FilePath;
import hudson.model.Build;
import hudson.model.BuildListener;
import hudson.model.AbstractBuild;
import hudson.tasks.Builder;
import hudson.tasks.BuildStepDescriptor;
import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.DataBoundConstructor;

import com.pearson.deployment.*;
import com.pearson.deployment.config.*;
import org.yaml.snakeyaml.Yaml;

//  jobs/ServiceManage.groovy will end up here

public class ServiceManageBuilder extends Builder {
  private final String filename;

  @DataBoundConstructor
  public ServiceManageBuilder(String filename) {
      this.filename = filename;
  }

  @Override
  public boolean perform(AbstractBuild build, Launcher launcher, BuildListener listener) {
      // this is where you 'build' the project
      // since this is a dummy, we just say 'hello world' and call that a build

      // this also shows how you can consult the global configuration of the builder

      try {
              //  
      } catch (Exception e) {
        return false;
      }
      return true;
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
}
