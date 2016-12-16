package com.pearson.deployment.jobdsl;

import javaposse.jobdsl.dsl.Context;

public class ServiceManagerContext implements Context {
  String filename;

  public void filename(String value) {
    filename = value;
  }

}
