package com.pearson.deployment.jobdsl

import hudson.Extension
import javaposse.jobdsl.dsl.helpers.step.StepContext
import javaposse.jobdsl.dsl.helpers.publisher.PublisherContext
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
      pkg: context.pkg,
      type: context.type,
      version: context.version,
      location: context.location,
      repository_key: context.repository_key,
      repository: context.repository
    )
  }
}
