package com.pearson.deployment.config

class ConfigValidator implements Serializable {
  def required = []
  def attributes = [:]

  def validate(Closure config) {
    def missingValues = []
    required?.each { propertyName ->
      if (propertyValue(attributes, propertyName) == [:]) {
        missingValues << propertyName
      }
    }
    if (missingValues) {
      throw new Exception("Values must be provided for required properties: ${missingValues}.")
    }
  }

  private  propertyValue(def conf, String propertyName) {
    def dotIdx = propertyName.indexOf('.')
    def key
    def rest

    if (dotIdx != -1) {
      key = propertyName.substring(0, dotIdx)
      rest = propertyName.substring(dotIdx + 1)
    } else {
      key = propertyName
      rest = ''
    }

    if (rest instanceof Map) {
      propertyValue(conf[key], rest)
    } else {
      (conf instanceof Map) ? (conf[key] ?: [:]) : [:]
    }
  }
}
