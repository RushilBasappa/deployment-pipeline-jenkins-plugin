package com.pearson.deployment.config.bitesize

class BuildComponent implements Serializable {
  String name
  String version
  String os
  String type = "debian-package"
  String runtime
  List<BuildDependency> dependencies
  BuildRepository repository
  Map<String,String> notifications
  List<EnvVar> env = []
  List<? extends Map<String,String>> build
  List<? extends Map<String,String>> artifacts
}
