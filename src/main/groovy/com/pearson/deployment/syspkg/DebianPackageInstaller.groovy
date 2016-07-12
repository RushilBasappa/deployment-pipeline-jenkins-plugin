package com.pearson.deployment.syspkg

import java.io.OutputStream
import java.io.ByteArrayOutputStream

import hudson.Launcher
import hudson.Proc
import hudson.model.BuildListener
import hudson.model.AbstractBuild

import com.pearson.deployment.config.bitesize.BuildDependency

class DebianPackageInstaller extends AbstractPackageInstaller {

  DebianPackageInstaller(AbstractBuild build, Launcher launcher, BuildListener listener, BuildDependency dependency) {
    super(build, launcher, listener, dependency)
    this.pkg = new Package()
  }

  void install() {
    if (dependency.location != null) {
      pkg.installFromLocation(dependency.location)
    } else {
      Repository repository = new Repository()
      repository.addKey()
      repository.add()
      pkg.install()
    }
  }

  class Package extends AbstractPackageInstaller.Package {

    String name
    String version

    Package() {
      this.name = dependency.package
      this.version = dependency.version
    }

    void install() {
      if (isInstalled()) {
        return
      } 

      String query = version ? "${name}=${version}" : name  
      exe("sudo apt-get install -q -y ${query}")
    }

    void installFromLocation(String location) {
      if (isInstalled()) {
        return
      }

      String tmpdir  = System.getProperty("java.io.tmpdir")
      String debfile = (location =~ /.*\/(.*\.deb)/)[0][1]
      String path = "${tmpdir}/${debfile}"
      File   debpath = new File(path)

      if ( !debpath.exists()) {
        exe "curl -k -o ${path} -s -L ${location}"
        exe "sudo dpkg -i ${path}"
      }
    }

    boolean isInstalled() {
      OutputStream stream = new ByteArrayOutputStream()
      exe("dpkg-query --show -f='${version}' ${name}", stream)
      String installedVersion = stream.toString("UTF-8")
      
      if (!version || version == installedVersion) {
        return true
      }
      return false
    }
  }

  class Repository {
    String repo
    String repoKey

    Repository() {
      this.repo = dependency.repository
      this.repoKey = dependency.repository_key
    }

    void addKey() {
      if (repoKey) {
        exe "sudo apt-key adv --keyserver keyserver.ubuntu.com --recv-keys ${repoKey}"
      }
    }

    void add() {
      if (!repo) {
        return
      }

      if (directRepoString()) {
        exe "sudo add-apt-repository ${repo}"
      } else {
        exe "sudo add-apt-repository deb ${repo} ${ubtuntuRelease()} main"
      }
      exe "sudo apt-get update"
    }

    private boolean directRepoString() {
      return ( repo =~ /ppa:.*/ || repo =~ /deb .*/)
    }

    private String ubuntuRelease() {
      OutputStream stream = new ByteArrayOutputStream()
      exe "lsb_release -c", stream
      String codename = stream.toString()
      (codename =~ /Codename:\s+(.*)/)[0][1]
    }
  }
}