package com.pearson.deployment.config

import org.yaml.snakeyaml.Yaml
import hudson.FilePath
// import hudson.model.AbstractBuild

class ConfigReader {
  String filename
  def attributes

  def validate(@DelegatesTo(ConfigValidator) Closure closure){
    ConfigValidator config = new ConfigValidator()
    // config.props = props

    closure.delegate = config
    closure.setResolveStrategy(Closure.DELEGATE_FIRST)
    closure.call()

    return config.validate()
  }

  def readConfig(String filename) {
    this.filename = filename
    String contents = new File(filename).getText("UTF-8")
    this.attributes = loadYaml(contents)
  }

  // def readConfig(AbstractBuild build, String filename) {
  //   this.filename = filename
  //   FilePath fp = new FilePath(build.getWorkspace(), filename)
  //   String contents = fp.read()
  //   this.attributes = loadYaml(contents)
  // }

  def loadYaml(String contents) {
    Yaml yaml = new Yaml()
    yaml.load(contents)
  }
}
