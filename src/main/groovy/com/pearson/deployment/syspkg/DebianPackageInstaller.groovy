package com.pearson.deployment.syspkg

// import java.io.OutputStream
// import java.io.ByteArrayOutputStream

import hudson.Launcher
// import hudson.Proc
import hudson.model.BuildListener
import hudson.model.AbstractBuild

import com.pearson.deployment.config.bitesize.BuildDependency
import com.pearson.deployment.config.bitesize.SystemPackage

class DebianPackageInstaller extends AbstractPackageInstaller {

  DebianPackageInstaller(AbstractBuild build, Launcher launcher, BuildListener listener, BuildDependency dependency) {
    super(build, launcher, listener, dependency)
    pkg = new Package()
  }

  public static String installCmd(SystemPackage pkg) {
    String query = pkg.version ? "${pkg.name}=${pkg.version}*" : pkg.name
    "apt-get install -q -y --force-yes ${query}"
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
      name = dependency.pkg
      version = dependency.version
    }

    void install() {
      if (isInstalled()) {
        return
      } 

      String cmd = DebianPackageInstaller.installCmd(dependency)
      exe("sudo apt-get update -q")
      exe("sudo ${cmd}")
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
      String query = (version || version == 'null' || version == '') ? "--show -f='${version}'" : "--show"
      try {
        exe("dpkg-query ${query} ${name}", stream)
      } catch(e) {
        return false
      }
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