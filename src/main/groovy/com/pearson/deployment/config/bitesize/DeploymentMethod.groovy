package com.pearson.deployment.config.bitesize

class DeploymentMethod implements Serializable {
    String method = "rolling-upgrade"
    String mode
    String active
    int timeout = 300
}