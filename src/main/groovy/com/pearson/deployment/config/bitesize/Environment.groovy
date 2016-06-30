package com.pearson.deployment.config.bitesize

class Environment {
    String name
    String namespace
    String next_environment // not used
    DeploymentMethod deployment
    List<Service> services
    List<EnvironmentTest> tests
}