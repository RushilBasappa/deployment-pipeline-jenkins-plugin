package com.pearson.deployment.config.bitesize

class DeploymentMethod implements Serializable {
  String method = "rolling-upgrade"
  String mode
  String active
  int timeout = 300

  boolean isBlueGreen() {
    this.method == "bluegreen"
  }

  boolean isValid() {
    if (isBlueGreen() && this.active == null) {
      return false
    }
    true
  }

}