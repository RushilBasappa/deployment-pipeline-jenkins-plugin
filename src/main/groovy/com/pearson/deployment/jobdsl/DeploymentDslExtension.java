package com.pearson.deployment.jobdsl;

import hudson.Extension;
import javaposse.jobdsl.dsl.Context;
import javaposse.jobdsl.dsl.helpers.step.StepContext;
import javaposse.jobdsl.plugin.DslExtensionMethod;
import javaposse.jobdsl.plugin.ContextExtensionPoint;
import javaposse.jobdsl.dsl.* ;
// import javaposse.jobdsl.dsl.Context

// DOES NOT WORK

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.pearson.deployment.builder.*;

@Extension(optional = true)
public class DeploymentDslExtension extends ContextExtensionPoint {

  private static final Logger LOG = Logger.getLogger(DeploymentDslExtension.class.getName());

  @DslExtensionMethod(context = StepContext.class)
  public Object serviceManager(String filename) {
    LOG.info(getClass().getSimpleName() + ": reading filename " + filename);
    return new ServiceManageBuilder(filename);
  }
}
