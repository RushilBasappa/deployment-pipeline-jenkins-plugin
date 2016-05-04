package com.pearson.deployment

class DeploymentApplication implements Serializable {
  String name
  ApplicationBuilder builder
  def attributes

  DeploymentApplication(String name) {
    this.name = name
    def jenkins_home = System.getenv().JENKINS_HOME ?: '/var/jenkins_home'
    // TODO: change to read from workspace via FilePath
    this.builder = new ApplicationBuilder("${jenkins_home}/workspace/seed-job/application.bitesize")
    this.attributes = builder.getApplication(name)
  }

  // def copyDependencyArtifacts() {
  //   for (def i = 0; i < attributes.dependencies?.size(); i++ ) {
  //     dep = deps[i]
  //     if (dep.origin?.build) {
  //      def jobId = versions["\${dep.name}.\${dep.version}"]
  //      if (jobId) {
  //        log "COPY", "Copying \${dep.name}:\${dep.version} from job \${jobId}"
  //        selector = new SpecificBuildSelector(jobId)
  //        step([\$class: 'CopyArtifact', target:"deb/", projectName:dep.name, selector: [\$class: 'SpecificBuildSelector', buildNumber: jobId]])
  //      }
  //     }
  //   }
  //
  //   def getVersions() {
  //     def selector = new StatusBuildSelector(stable: false)
  //     copy = new CopyArtifact(filter:'manifest', projectName:'components-registry', selector: selector)
  //     step([\$class: 'CopyArtifact', filter: 'manifest', projectName:'components-registry', selector: [\$class: 'StatusBuildSelector', stable: false]])
  //     versions = readPropertiesFromFile ("manifest")
  //   }
  // }

}
