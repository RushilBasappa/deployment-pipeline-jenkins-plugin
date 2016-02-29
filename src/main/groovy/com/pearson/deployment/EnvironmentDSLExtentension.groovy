package com.pearson.deployment

import hudson.Extension
import javaposse.jobdsl.dsl.helpers.step.StepContext
import javaposse.jobdsl.plugin.DslExtensionMethod
import javaposse.jobdsl.plugin.ContextExtensionPoint
// import javaposse.jobdsl.dsl.*
import javaposse.jobdsl.dsl.Context

@Extension(optional = true)
public class EnvironmentDSLExtension extends ContextExtensionPoint {
    @DslExtensionMethod(context = StepContext.class)
    public Object environment_definition(String filename) {
      return new EnvDefinition(filename)
    }
}
