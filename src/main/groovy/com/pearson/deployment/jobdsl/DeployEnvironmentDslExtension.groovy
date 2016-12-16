package com.pearson.deployment.jobdsl

import hudson.Extension
import javaposse.jobdsl.dsl.helpers.step.StepContext
import javaposse.jobdsl.plugin.DslExtensionMethod
import javaposse.jobdsl.plugin.ContextExtensionPoint

import java.util.logging.Logger
import com.pearson.deployment.builder.*


@Extension(optional = true)
public class DeployEnvironmentDslExtension extends ContextExtensionPoint {

  @DslExtensionMethod(context = StepContext.class)
  public Object deploy_environment(Runnable closure) {
    DeployEnvironmentContext context = new DeployEnvironmentContext()
    executeInContext(closure, context)

    LOG.info(getClass().getSimpleName() + ": reading filename " + context.filename)
    return new DeployEnvironmentBuilder(context.filename, context.environment)
  }

  @DslExtensionMethod(context = StepContext.class)
  public Object deploy_environment(String filename, String environment) {
    return new DeployEnvironmentBuilder(filename, environment)
  }

}
