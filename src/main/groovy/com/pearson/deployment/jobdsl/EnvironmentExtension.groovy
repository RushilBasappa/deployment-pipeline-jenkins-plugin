// package com.pearson.deployment.jobdsl;
//
// import hudson.Extension;
// import javaposse.jobdsl.dsl.helpers.step.StepContext;
// import javaposse.jobdsl.plugin.DslExtensionMethod;
// import javaposse.jobdsl.plugin.ContextExtensionPoint;
// import javaposse.jobdsl.dsl.* ;
// // import javaposse.jobdsl.dsl.Context
//
// import com.pearson.deployment.builder.*;
//
// @Extension(optional = true)
// public class EnvironmentDSLExtension extends ContextExtensionPoint {
//     @DslExtensionMethod(context = StepContext.class)
//     public Object service_manager(String filename) {
//       return new ServiceManageBuilder(filename);
//     }
// }
