package com.pearson.deployment.config.bitesize

import com.pearson.deployment.helpers.Helper
import org.yaml.snakeyaml.Yaml
import hudson.FilePath

import javax.validation.Valid

import com.pearson.deployment.callable.WorkspaceReader

class ApplicationBitesize implements Serializable {
  String project
  @Valid List<Application> applications

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
    yaml.loadAs(stream, ApplicationBitesize)    
  }

  public Application getApplication(String name) {
    applications?.find{ it.name == name }
  }

  void setProject(String p) {
    project = p
    applications?.each {
      it.project = p
    }

  }

  void setApplications(List<Application> apps) {
    apps.each {
      it.project = project
    }
    applications = apps
  }

  public static String getEOToken(String projectName) {
    "${Helper.eoToken(projectName)}"
  }

} 