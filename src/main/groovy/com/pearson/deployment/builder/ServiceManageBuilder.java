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

        FilePath fp = new FilePath(build.getWorkspace(), this.filename);
        String contents = fp.readToString();

        EnvironmentConfig envConfig = new EnvironmentConfig(contents, true);

        // for (Integer i = 0; i < envConfig.attributes.environments.size(); i++) {
        //   LinkedHashMap e =  envConfig.attributes.environments[i] ;
        //   // println "\${AnsiColors.green}\${e.name} found!\${AnsiColors.reset}"
        //   KubeConfigGenerator k = new KubeConfigGenerator(envConfig.attributes.project, e) ;
        //   // k.setup()
        // }
      } catch (Exception e) {
        return false;
      }

      // env_def.attributes.environments?.each {
      //   println "\${AnsiColors.green}\${it.name} found!\${AnsiColors.reset}"
      //   def k = new KubeConfigGenerator(env_def.attributes.project, it)
      //   k.setup()
      // }
      // println "\${AnsiColors.green}DONE!\${AnsiColors.reset}"
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
