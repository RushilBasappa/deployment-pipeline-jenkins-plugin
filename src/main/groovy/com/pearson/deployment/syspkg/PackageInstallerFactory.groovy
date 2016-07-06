package com.pearson.deployment.syspkg

import hudson.Launcher
import hudson.model.BuildListener
import hudson.model.AbstractBuild

import com.pearson.deployment.config.bitesize.BuildDependency


class PackageInstallerFactory {
  public static AbstractPackageInstaller getInstaller(
    AbstractBuild build,
    Launcher launcher,
    BuildListener listener,
    BuildDependency dependency) {
            
    switch(dependency.type) {
      case 'gem-package':
        return new GemPackageInstaller(build, launcher, listener, dependency)
      case 'debian-package':
        return new DebianPackageInstaller(build, launcher, listener, dependency)
      default:
        throw new Exception("Unknown build dependency")
        break

    }
  }
}