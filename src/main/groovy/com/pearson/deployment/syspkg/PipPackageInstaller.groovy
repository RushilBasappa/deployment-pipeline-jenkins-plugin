package com.pearson.deployment.syspkg

// import java.lang.Process

import hudson.Launcher
// import hudson.Proc
import hudson.model.BuildListener
import hudson.model.AbstractBuild

import com.pearson.deployment.config.bitesize.BuildDependency

class PipPackageInstaller extends AbstractPackageInstaller {

  PipPackageInstaller(AbstractBuild build, Launcher launcher, BuildListener listener, BuildDependency dependency) {
    super(build, launcher, listener, dependency)
    pkg = new Package()
  }

  void install() {
    exe "sudo pip install ${pkg.name}"
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