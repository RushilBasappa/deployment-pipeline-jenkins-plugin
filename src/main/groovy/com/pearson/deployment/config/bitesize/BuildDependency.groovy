package com.pearson.deployment.config.bitesize

class BuildDependency implements Serializable {
  String pkg
  String type
  String version
  String location
  String repository_key
  String repository

  void setPackage(String p) {
    this.pkg = p
  }
  
  String getPackage() {
    this.pkg
  }
}