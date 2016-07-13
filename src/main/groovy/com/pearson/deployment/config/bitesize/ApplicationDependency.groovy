package com.pearson.deployment.config.bitesize

class ApplicationDependency implements Serializable {
  String name
  String type
  String version
  BuildOrigin origin
}