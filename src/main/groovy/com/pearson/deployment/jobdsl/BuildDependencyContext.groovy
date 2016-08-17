package com.pearson.deployment.jobdsl

import javaposse.jobdsl.dsl.Context
import com.pearson.deployment.config.bitesize.BuildDependency

class BuildDependencyContext implements Context {
  String pkg
  String type
  String version
  String location
  String repository_key
  String repository

  public void pkg(String value) {
    pkg = value
  }

  public void type(String value) {
    type = value
  }

  public void version(String value) {
    version = value
  }

  public void location(String value) {
    location = value
  }

  public void repository_key(String value) {
    repository_key = value
  }

  public void repository(String value) {
    repository = value
  }

  public BuildDependency dependency() {
    return new BuildDependency(
      pkg: pkg,
      type: type,
      version: version,
      location: location,
      repository_key: repository_key,
      repository: repository
    )
  }

}