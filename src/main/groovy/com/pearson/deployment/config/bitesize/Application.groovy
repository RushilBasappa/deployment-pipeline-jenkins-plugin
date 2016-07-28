package com.pearson.deployment.config.bitesize

// import org.yaml.snakeyaml.Yaml

// import java.io.InputStream
// import java.io.OutputStream
// import java.io.ByteArrayInputStream

// import hudson.FilePath

// import com.pearson.deployment.callable.WorkspaceReader


class Application implements Serializable {
  String name
  String runtime
  String command
  List<ApplicationDependency> dependencies
} 