package com.pearson.deployment.config.bitesize

class EnvVar implements Comparable {
  String name
  String value

  int compareTo(def other) {
    if (name == other.name && value == other.value) {
      return 0
    }
    return 1
  }
}