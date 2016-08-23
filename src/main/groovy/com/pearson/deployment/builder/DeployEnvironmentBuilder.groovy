package com.pearson.deployment.builder


class DeployEnvironmentBuilder extends Builder {
  private static final Logger LOG = Logger.getLogger(DeployEnvironmentBuilder.class.getName());

  @DataBoundContructor
  DeployEnvironmentBuilder(String environmentFile, String environmentName) {
  
    FilePath fp = new FilePath(build.workspace, filename)
    this.config = EnvironmentsBitesize.readConfigFromPath(fp)

    this.environment = config?.getEnvironment(envname)
  }

}