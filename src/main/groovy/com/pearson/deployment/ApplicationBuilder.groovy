package com.pearson.deployment

import hudson.model.BuildListener


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
    "${dockerRegistry()}/${project}/${application.name}:${application.version}"
  }

  def dockerRegistry() {
    Env.get('DOCKER_REGISTRY') ?: "bitesize-registry.default.svc.cluster.local:5000"
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
