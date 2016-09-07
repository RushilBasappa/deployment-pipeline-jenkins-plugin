package com.pearson.deployment

import com.pearson.deployment.helpers.Helper
import com.pearson.deployment.config.bitesize.*
import com.pearson.deployment.syspkg.*

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
    String debianInstallString = debianDependencies(application.dependencies)
    String gemInstallString = gemDependencies(application.dependencies)

    def tmpl = '''\
                  FROM ${registry}/baseimages/${runtime}
                  MAINTAINER Bitesize Project <bitesize-techops@pearson.com>
                  <% if (deb_packages != "") out.print apt_get_install %>
                  <% if (gem_packages != "") out.print gem_install %>
                
                  ENTRYPOINT [${entrypoint}]
    '''.stripIndent()

    def installDeb = """\
                        RUN echo 'deb http://apt/ bitesize main' > /etc/apt/sources.list.d/bitesize.list
                        RUN ${debianInstallString}
    """.stripIndent()

    def installGem = """\
                        RUN ${gemInstallString}
    """

    def template = new GStringTemplateEngine().createTemplate(tmpl)

    def binding = [
      registry:        this.dockerRegistry,
      runtime:         application.runtime,
      deb_packages:    debianInstallString,
      apt_get_install: installDeb,
      gem_packages:    gemInstallString,
      gem_install:     installGem,
      entrypoint:      commandToEntrypoint(application.command)
    ]

    template.make(binding).toString()
  }

  String currentTimeTag() {
    new SimpleDateFormat("yyyyMMddHHmmss").format(new Date())
  }

  private String debianDependencies(def deps) {
    def ret = ["apt-get -q update"]
    deps?.each { pkg ->
      if (pkg.type == "debian-package") {
        ret.add DebianPackageInstaller.installCmd(pkg)
      }
    }
    ret.add "rm -rf /var/cache/apt/*"     
    ret.join(" && \\\n  ")
  }

  private String gemDependencies(def deps) {
    def ret = []
    deps?.each { pkg ->
      if (pkg.type == "gem-package") {    
        ret.add GemPackageInstaller.installCmd(pkg)
      }
    }
    ret.join(" && \\\n  ")
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
