package com.pearson.deployment.config.bitesize

class BuildDependency extends SystemPackage implements Serializable {
  String pkg
  String type
  String version
  String location
  String repository_key
  String repository

  void setPackage(String p) {
    this.pkg = p
    this.name = p
  }

  void setName(String value) {
    this.pkg = value
    this.name = value
  }
  
  String getPackage() {
    this.pkg
  }
}