package com.pearson.deployment

import org.yaml.snakeyaml.Yaml

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

  def readConfig(def filename) {
    this.filename = filename
    def f = new File(filename).getText("UTF-8")
    def yaml = new Yaml()
    this.attributes = yaml.load(f)
  }
}
