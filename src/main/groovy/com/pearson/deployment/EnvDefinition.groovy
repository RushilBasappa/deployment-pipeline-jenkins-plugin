package com.pearson.deployment

import hudson.tasks.Builder
import org.kohsuke.stapler.DataBoundConstructor
// import hudson.model.AbstractBuild
// import hudson.model.Launcher
// import hudson.model.TaskListener

class EnvDefinition extends Builder {
  private final String filename

  @DataBoundConstructor
  EnvDefinition(String filename) {
    this.filename = filename
  //  this.config = new EnvironmentConfig(filename)
  }

  // @Override
  // def perform(AbstractBuild build, Launcher launcher, BuildListener listener) {
  //   perform(build, launcher, (TaskListener)listener)
  // }
  //

  // def perform(AbstractBuild build, Launcher launcher, TaskListener listener) {
  //   FilePath ws = build.getWorkspace()
  //
  //   if (ws == null) {
  //     Node node = build.getBuiltOn()
  //     if (node == null) {
  //       throw new NullPointerException("no such build node: ${build.getBuiltOnStr()}")
  //     }
  //     throw new NullPointerException("no workspace from node ${node} which is computer ${node.toComputer()}")
  //   }
  //
  //   def config = new EnvironmentConfig("${ws}/${filename}")
  //   config.attributes.environments?.each {
  //     println "\${AnsiColors.green}\${it.name} found!\${AnsiColors.reset}"
  //     def k = new KubeConfigGenerator(it)
  //     k.setup()
  //   }
  // }

 // @Extension
 // public static class DescriptorImpl extends BuildStepDescriptor<Builder> {
 //   private String filename
 //
 //   DescriptorImpl() {
 //     load()
 //   }
 //
 //   String getFilename() {
 //     return filename
 //   }
 //
 //   @Override
 //   public boolean isApplicable(Class<? extends AbstractProject> jobType) {
 //     return true
 //   }
 //
 //   @Override
 //   public String getDisplayName() {
 //    return "Populate environment build jobs"
 //   }
 //
 //   @Override
 //   public boolean configure(StaplerRequest req, JSONObject data) throws FormException {
 //     req.bindJSON(this, data);
 //     return super.configure(req, data);
 //   }
 // }
}