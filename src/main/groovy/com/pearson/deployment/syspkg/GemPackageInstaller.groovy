package com.pearson.deployment.syspkg

// import java.lang.Process

import hudson.Launcher
// import hudson.Proc
import hudson.model.BuildListener
import hudson.model.AbstractBuild

import com.pearson.deployment.config.bitesize.BuildDependency

class GemPackageInstaller extends AbstractPackageInstaller {

  GemPackageInstaller(AbstractBuild build, Launcher launcher, BuildListener listener, BuildDependency dependency) {
    super(build, launcher, listener, dependency)
    pkg = new Package()
  }

  void install() {
    String  query = pkg.version ? "${pkg.name} -v ${pkg.version}" : pkg.name
    exe "sudo gem install ${query}"
  }

  class Package extends AbstractPackageInstaller.Package {
    String name
    String version

    Package() {
      name = dependency.pkg
      version = dependency.version
    }
  }
}