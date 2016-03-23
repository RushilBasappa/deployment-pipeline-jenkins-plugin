package com.pearson.deployment

class BuildConfig extends ConfigReader implements Serializable {
  BuildConfig(String filename) {
    readConfig(filename)
    // validation
    attributes.components?.each {
      validate_component(it)
    }

  }

  def components() {
    return attributes.components
  }

  private def validate_component(def component) {
    validate {
      required = [
        "name",
        "repository.git",
        "build"
      ]
      attributes = component
    }
  }
}
