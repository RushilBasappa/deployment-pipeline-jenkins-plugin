package com.pearson.deployment

import hudson.FilePath
import hudson.Launcher

import hudson.model.BuildListener
import hudson.model.AbstractBuild

import hudson.plugins.copyartifact.CopyArtifact
import hudson.plugins.copyartifact.SpecificBuildSelector

import com.pearson.deployment.config.ApplicationConfig
import com.pearson.deployment.helpers.Helper

class ApplicationBuilder implements Serializable {
  def appDefinition
  def project
  def applications
  String configFile

  ApplicationBuilder(String configPath) {
    this.appDefinition = new ApplicationConfig(configPath)
    this.project = appDefinition.attributes.project
    this.applications = appDefinition.attributes.applications
  }

  // ApplicationBuilder(String contents) {
  ApplicationBuilder(AbstractBuild build, Launcher launcher,  BuildListener listener, String buildName, String buildId, String filename) {
    this.configFile    = filename
    FilePath fp        = new FilePath(build.getWorkspace(), filename)
    String contents    = fp.readToString()

    this.appDefinition = new ApplicationConfig(contents, true)
    this.project       = appDefinition.attributes.project
    this.applications  = appDefinition.attributes.applications
  }

  def getApplication(String name) {
    applications?.find { it.name == name }
  }

  def getDockerImage(String app) {
    def application = getApplication(app)
    if (application == null) {
      throw Exception("Application ${app} not defined")
    }

    def version = getAppVersion(app)
    "${Helper.dockerRegistry()}/${project}/${application.name}:${version}"
  }

  def getAppVersion(String app) {
    def application = getApplication(app)
    if (application.version) { return application.version }

    def dep = application.dependencies?.find { it.origin?.build != null }
    dep?.version
  }

  // def copyDependencyArtifacts(def deps, def versions) {
  //   for (def i = 0; i < deps.size(); i++ ) {
  //     dep = deps[i]
  //     if (dep.origin?.build) {
  //      jobId = versions["\${dep.origin.build}.\${dep.name}.\${dep.version}"]
  //      if (jobId) {
  //        log "COPY", "Copying \${dep.name}:\${dep.version} from job \${jobId}"
  //        step([\$class: 'CopyArtifact', target:"deb/", projectName:dep.origin.build, selector: [\$class: 'SpecificBuildSelector', buildNumber: jobId]])
  //      }
  //     }
  //   }
  // }

  def copyDependencyArtifacts(String app) {
    def application = getApplication(app)
    for (def i = 0; i < application.dependencies?.size(); i++ ) {
      def dep = deps[i]

      if (dep.origin?.build) {
        def jobId = versions["${dep.origin.build}${dep.name}.${dep.version}"]
        if (jobId) {
          // log "COPY", "Copying \${dep.name}:\${dep.version} from job \${jobId}"
          def a = new CopyArtifact(dep.name, selector)
          CopyArtifact copyArtifact = new CopyArtifact(
            dep.origin.build,
            "",
            new SpecificBuildSelector(jobId),
            "",
            "deb/",
            false,
            true
          )

          def perform = copyArtifact.class.getMethod("perform", AbstractBuild, Launcher, BuildListener)
          perform.invoke(copyArtifact, build, launcher, listener)
        }
      }
    }
  }
}
