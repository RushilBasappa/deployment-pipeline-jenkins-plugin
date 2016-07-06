package com.pearson.deployment.syspkg

import java.lang.Process

import hudson.Launcher
import hudson.Proc
import hudson.model.BuildListener
import hudson.model.AbstractBuild

import com.pearson.deployment.config.bitesize.BuildDependency

class GemPackageInstaller extends AbstractPackageInstaller {

  GemPackageInstaller(AbstractBuild build, Launcher launcher, BuildListener listener, BuildDependency dependency) {
    super(build, launcher, listener, dependency)
    this.pkg = new Package()
  }

  void install() {
    pkg.install()
  }

  class Package extends AbstractPackageInstaller.Package {
    String name
    String version

    Package() {
      this.name = dependency.package
      this.version = dependency.version
    }

    void install() {
      String  query = version ? "${name} -v ${version}" : name

      Proc proc = exe "sudo gem install ${query}"
      int exitCode = proc.join()

      if (exitCode != 0) {
        throw new Exception("Gem install failed")
      }
    }
  }
}