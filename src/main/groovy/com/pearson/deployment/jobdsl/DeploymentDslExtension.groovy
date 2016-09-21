package com.pearson.deployment.jobdsl

import hudson.Extension
import javaposse.jobdsl.dsl.helpers.step.StepContext
import javaposse.jobdsl.plugin.DslExtensionMethod
import javaposse.jobdsl.plugin.ContextExtensionPoint

import java.util.logging.Logger
import com.pearson.deployment.builder.*

@Extension(optional = true)
public class DeploymentDslExtension extends ContextExtensionPoint {

  private static final Logger LOG = Logger.getLogger(DeploymentDslExtension.class.getName());

  @DslExtensionMethod(context = StepContext.class) 
  public Object serviceManager(Runnable closure) {
    ServiceManagerContext context = new ServiceManagerContext()
    executeInContext(closure, context)

    LOG.info(getClass().getSimpleName() + ": reading filename " + context.filename)
    return new ServiceManageBuilder(context.filename)
  }

  @DslExtensionMethod(context = StepContext.class)
  public Object serviceManager(String filename) {
    return new ServiceManageBuilder(filename)
  }

  @DslExtensionMethod(context = StepContext.class)
  public Object bitesize_build_dependency(Runnable closure) {
    BuildDependencyContext context = new BuildDependencyContext()
    executeInContext(closure, context)

    LOG.info("We have ${context.type}: ${context.pkg} to install")

    return new BuildDependencyBuilder(
      context.pkg,
      context.version,
      context.type,
      context.location,
      context.repository,
      context.repository_key
    )
  }
}
