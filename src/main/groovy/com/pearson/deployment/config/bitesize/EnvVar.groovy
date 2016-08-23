package com.pearson.deployment.config.bitesize

class EnvVar implements Serializable {
  String name
  String value

  boolean equals(Object obj) {

    if (obj == null) {
      return false
    }

    if (!EnvVar.class.isAssignableFrom(obj.getClass())) {
      return false
    }

    EnvVar other = (EnvVar)obj
    if (name == other.name && value == other.value) {
      return true
    }
    return false
  }
}