package com.pearson.deployment.config.bitesize

class BuildComponent implements Serializable {
  String name
  String version
  String os
  List<BuildDependency> dependencies
  BuildRepository repository
  List<String,String> build
  List<String,String> artifacts
} 