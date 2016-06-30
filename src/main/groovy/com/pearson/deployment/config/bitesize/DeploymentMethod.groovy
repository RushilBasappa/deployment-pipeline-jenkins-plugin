package com.pearson.deployment.config.bitesize

class DeploymentMethod {
    String method = "rolling-upgrade"
    String mode
    int timeout = 300
}