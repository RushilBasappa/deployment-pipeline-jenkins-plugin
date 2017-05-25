package com.pearson.deployment.kubernetes

import groovy.transform.CompileStatic

@CompileStatic
public class ResourceNotFoundException extends Exception {
  public ResourceNotFoundException() {
  }

  public ResourceNotFoundException(String message) {
    super (message)
  }

  public ResourceNotFoundException(Throwable cause) {
    super (cause)
  }

  public ResourceNotFoundException(String message, Throwable cause) {
    super (message, cause)
  }
}
