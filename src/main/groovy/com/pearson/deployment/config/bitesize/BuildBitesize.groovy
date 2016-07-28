package com.pearson.deployment.config.bitesize

import org.yaml.snakeyaml.Yaml
import hudson.FilePath

import com.pearson.deployment.callable.WorkspaceReader

class BuildBitesize implements Serializable {
  String project
  List<BuildComponent> components

  BuildBitesize() {
    components = []
    project      = ''
  }

  public static BuildBitesize readConfigFromPath(FilePath path) {
    Yaml yaml = new Yaml()
    InputStream stream = path.act(new WorkspaceReader())
    return yaml.loadAs(stream, BuildBitesize)
  }

  public static BuildBitesize readConfigFromString(String contents) {
    Yaml yaml = new Yaml()
    InputStream stream = new ByteArrayInputStream(contents.getBytes("UTF-8"))
    return yaml.loadAs(stream, BuildBitesize)
  }

  public BuildComponent getBuildComponent(String name) {
    components?.find{ it.name == name }
  }

} 