package com.pearson.deployment.syspkg

// import java.lang.Process

import hudson.Launcher
// import hudson.Proc
import hudson.model.BuildListener
import hudson.model.AbstractBuild

import com.pearson.deployment.config.bitesize.BuildDependency
import com.pearson.deployment.config.bitesize.SystemPackage

class GemPackageInstaller extends AbstractPackageInstaller {

  GemPackageInstaller(AbstractBuild build, Launcher launcher, BuildListener listener, BuildDependency dependency) {
    super(build, launcher, listener, dependency)
    pkg = new Package()
  }

  void install() {    
    String cmd = GemPackageInstaller.installCmd(dependency)
    exe "sudo {cmd}"
  }

  static String installCmd(SystemPackage pkg) {  
    String  query = pkg.version ? "${pkg.name} -v ${pkg.version}" : pkg.name
    "gem install --no-ri --no-rdoc ${query}"  
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