package com.pearson.deployment

import com.pearson.deployment.helpers.Helper
import com.pearson.deployment.config.bitesize.*

import java.util.regex.*

import java.util.Date
import java.text.DateFormat
import java.text.SimpleDateFormat
import groovy.text.GStringTemplateEngine

class Dockerfile implements Serializable {
  String filename
  Application application
  String dockerRegistry

  private writer

  Dockerfile(Application app) {
    this.application = app
    this.dockerRegistry = Helper.dockerRegistry()
  }

  def contents() {
    String debianDependencyString = debianDependencies(application.dependencies)

    def tmpl = '''\
                  FROM ${registry}/baseimages/${runtime}
                  MAINTAINER Bitesize Project <bitesize-techops@pearson.com>
                  <% if (deb_packages != "") out.print apt_get_install %>
                
                  ENTRYPOINT [${entrypoint}]
    '''.stripIndent()

    def installDeb = """\
                        RUN echo 'deb http://apt/ bitesize main' > /etc/apt/sources.list.d/bitesize.list
                        RUN apt-get -q update && apt-get install -y --force-yes ${debianDependencyString} && rm -rf /var/cache/apt
    """.stripIndent()

    def template = new GStringTemplateEngine().createTemplate(tmpl)

    def binding = [
      registry:        this.dockerRegistry,
      runtime:         application.runtime,
      deb_packages:    debianDependencyString,
      apt_get_install: installDeb,
      entrypoint:      commandToEntrypoint(application.command)
    ]

    template.make(binding).toString()
  }

  String currentTimeTag() {
    new SimpleDateFormat("yyyyMMddHHmmss").format(new Date())
  }

  private String debianDependencies(def deps) {
    String ret = ""
    deps?.each {
      if (it.type == "debian-package") {
        ret +=  it.version ? " ${it.name}=${it.version}-* " : "${it.name} "
      }
    } 
    ret
  }

  private String commandToEntrypoint(String cmd) {
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
