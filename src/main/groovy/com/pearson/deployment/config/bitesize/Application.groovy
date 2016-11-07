package com.pearson.deployment.config.bitesize

import com.pearson.deployment.helpers.*
// import org.yaml.snakeyaml.Yaml

// import java.io.InputStream
// import java.io.OutputStream
// import java.io.ByteArrayInputStream

// import hudson.FilePath

// import com.pearson.deployment.callable.WorkspaceReader

import com.pearson.deployment.validation.*

class Application extends Validator implements Serializable {
  @ValidString(regexp='[a-z:\\.\\d\\-]*', message='field "runtime" has invalid value')
  String name

  @ValidString(regexp='[a-z:\\.\\d\\-]*', message='field "runtime" has invalid value')
  String runtime

  String command
  String version = null
  @ValidString(regexp='[a-z:\\.\\d\\-]*', message='field "runtime" has invalid value')
  String project
  List<ApplicationDependency> dependencies

  def getDockerImage() {
    "${Helper.dockerRegistry()}/${project}/${name}"
  }

  def getVersion() {
    if (this.version) {
      return this.version
    }

    def dep = dependencies?.find { it.origin?.build != null }
    dep?.version
  }

  String normalizedName() {
    if (name == null) {
      return ""
    }
    Helper.normalizeName(name)
  }

  void setRuntime(String value) {
    validateField('runtime', value)
    this.runtime = value
  }
} 