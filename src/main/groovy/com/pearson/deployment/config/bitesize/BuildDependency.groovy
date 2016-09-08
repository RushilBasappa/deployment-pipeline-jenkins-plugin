package com.pearson.deployment.config.bitesize

class BuildDependency extends SystemPackage implements Serializable {
  String location
  String repository_key
  String repository

  void setPackage(String value) {
    name = value
  }

  void setPkg(String value) {
    name = value
  }
  
  String getPackage() {
    name
  }

  String getPkg() {
    name
  }
}