package com.pearson.deployment.jobdsl;

import javaposse.jobdsl.dsl.Context;

public class DeployEnvironmentContext implements Context {
  String filename
  String environment

  public void filename(String value) {
    filename = value;
  }

  public void environment(String value) {
    environment = value
  }
}
