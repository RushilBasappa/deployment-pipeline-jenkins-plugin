package com.pearson.deployment.config.bitesize

public class EnvironmentNotFoundException extends Exception {
  public EnvironmentNotFoundException() {
  }

  public EnvironmentNotFoundException(String message) {
    super (message)
  }

  public EnvironmentNotFoundException(Throwable cause) {
    super (cause)
  }

  public EnvironmentNotFoundException(String message, Throwable cause) {
    super (message, cause)
  }
}