package com.pearson.deployment

class Dockerfile implements Serializable {
  String filename
  LinkedHashMap app
  String docker_registry

  private writer

  Dockerfile(LinkedHashMap app) {
    // this.filename = filename
    this.app = app
    this.docker_registry = System.getenv().DOCKER_REGISTRY ?: "bitesize-registry.default.svc.cluster.local:5000"
    // writer = new File(this.filename)

    // writer << this.contents()
  }

  def contents() {
    def entrypoint = app.command ?: 'bash'
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
       ENTRYPOINT ${entrypoint}
    """.trim().stripIndent()
  }
}
