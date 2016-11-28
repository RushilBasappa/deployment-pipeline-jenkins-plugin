package com.pearson.deployment.config.bitesize

class EnvVar implements Serializable {
  String name
  String value
  String secret

  boolean equals(Object obj) {

    if (obj == null) {
      return false
    }

    if (!EnvVar.class.isAssignableFrom(obj.getClass())) {
      return false
    }

    EnvVar other = (EnvVar)obj
    if (name == other.name && value == other.value && secret == other.secret) {
      return true
    }
    return false
  }

  LinkedHashMap asMap() {
    if ( secret != null ) {
      [ name: secret, valueFrom: [secretKeyRef: [name: value, key: value]]]
    } else {
      [ name: name, value: value ]
    }
  }
}
