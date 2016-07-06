package com.pearson.deployment

import com.pearson.deployment.helpers.Helper

import java.util.regex.*

class Dockerfile implements Serializable {
  String filename
  LinkedHashMap app
  String docker_registry

  private writer

  Dockerfile(LinkedHashMap app) {
    // this.filename = filename
    this.app = app
    this.docker_registry = Helper.dockerRegistry()
    // writer = new File(this.filename)

    // writer << this.contents()
  }

  def contents() {
    def entrypoint = commandToEntrypoint(app.command)

    def dependencies = ""
    app.dependencies.each {
      if (it.origin) {
        dependencies = "${dependencies} /packages/${it.name}_${it.version}_amd64.deb"
      }
    }

    """\
       FROM ${this.docker_registry}/baseimages/${app.runtime}
       MAINTAINER Simas Cepaitis <simas.cepaitis@pearson.com>
       ADD ./deb /packages
       RUN dpkg -i ${dependencies}
       ENTRYPOINT [${entrypoint}]
    """.stripIndent()
  }

  private def commandToEntrypoint(String cmd) {
    String regex = "\"([^\"]*)\"|(\\S+)"
    Matcher m = Pattern.compile(regex).matcher(cmd);
    def commands = []
    while (m.find()) {
      if (m.group(1) != null ) {
        commands.push "\"${m.group(1)}\"".toString()
      } else {
        commands.push "\"${m.group(2)}\"".toString()
      }
    }
    commands.join(",") ?: 'bash'
  }
}
