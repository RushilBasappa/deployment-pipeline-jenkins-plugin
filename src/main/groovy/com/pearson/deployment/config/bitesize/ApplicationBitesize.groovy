package com.pearson.deployment.config.bitesize

import org.yaml.snakeyaml.Yaml
import hudson.FilePath

import com.pearson.deployment.callable.WorkspaceReader

class ApplicationBitesize implements Serializable {
  String project
  List<Application> applications

  ApplicationBitesize() {
    applications = []
    project      = ''
  }

  public static ApplicationBitesize readConfigFromPath(FilePath path) {
    Yaml yaml = new Yaml()
    InputStream stream = path.act(new WorkspaceReader())
    return yaml.loadAs(stream, ApplicationBitesize)
  }

  public static ApplicationBitesize readConfigFromString(String contents) {
    Yaml yaml = new Yaml()
    InputStream stream = new ByteArrayInputStream(contents.getBytes("UTF-8"))
    return yaml.loadAs(stream, ApplicationBitesize)
  }

  public Application getApplication(String name) {
    applications?.find{ it.name == name }
  }

} 