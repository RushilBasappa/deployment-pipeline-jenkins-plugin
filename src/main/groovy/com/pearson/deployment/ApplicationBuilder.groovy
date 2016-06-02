package com.pearson.deployment

import hudson.model.BuildListener
import com.pearson.deployment.config.ApplicationConfig
import com.pearson.deployment.helpers.Helper


class ApplicationBuilder implements Serializable {
  def appDefinition
  def project
  def applications

  ApplicationBuilder(String configPath) {
    this.appDefinition = new ApplicationConfig(configPath)
    this.project = appDefinition.attributes.project
    this.applications = appDefinition.attributes.applications
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

  // def copyDependencyArtifacts(String app) {
  //   def application = getApplication(app)
  //   for (def i = 0; i < application.dependencies?.size(); i++ ) {
  //     dep = deps[i]
  //
  //     if (dep.origin?.build) {
  //       jobId = versions["\${dep.name}.\${dep.version}"]
  //       if (jobId) {
  //         // log "COPY", "Copying \${dep.name}:\${dep.version} from job \${jobId}"
  //         a = new CopyArtifact(dep.name, selector)
  //         step([\$class: 'CopyArtifact', target:"deb/", projectName:dep.name, selector: [\$class: 'SpecificBuildSelector', buildNumber: jobId]])
  //       }
  //     }
  //   }
  // }
}
