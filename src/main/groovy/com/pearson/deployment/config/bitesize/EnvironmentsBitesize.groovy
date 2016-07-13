package com.pearson.deployment.config.bitesize

import org.yaml.snakeyaml.Yaml

import java.io.InputStream
import java.io.OutputStream
import java.io.ByteArrayInputStream

import hudson.FilePath

import com.pearson.deployment.callable.WorkspaceReader

class EnvironmentsBitesize implements Serializable {
  String project
  List<Environment> environments

  EnvironmentsBitesize() {
  }

  public static EnvironmentsBitesize readConfigFromPath(FilePath path) {
    Yaml yaml = new Yaml()
    InputStream stream = path.act(new WorkspaceReader())
    return yaml.loadAs(stream, EnvironmentsBitesize)
  }

  public static EnvironmentsBitesize readConfigFromString(String contents) {
    Yaml yaml = new Yaml()
    InputStream stream = new ByteArrayInputStream(contents.getBytes("UTF-8"))
    return yaml.loadAs(stream, EnvironmentsBitesize)
  }

  public Environment getEnvironment(String name) {
    environments?.find{ it.name == name }
  }

} 