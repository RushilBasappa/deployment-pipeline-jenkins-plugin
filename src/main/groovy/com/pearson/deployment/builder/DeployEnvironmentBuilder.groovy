package com.pearson.deployment.builder

import java.util.logging.Logger

import hudson.FilePath
import hudson.tasks.Builder
import hudson.tasks.BuildStepDescriptor

import org.kohsuke.stapler.DataBoundConstructor

class DeployEnvironmentBuilder extends Builder {
  private static final Logger LOG = Logger.getLogger(DeployEnvironmentBuilder.class.getName());

  @DataBoundConstructor
  DeployEnvironmentBuilder(String environmentFile, String environmentName) {
  
    FilePath fp = new FilePath(build.workspace, filename)
    this.config = EnvironmentsBitesize.readConfigFromPath(fp)

    this.environment = config?.getEnvironment(envname)
  }

}