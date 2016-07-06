package com.pearson.deployment.syspkg

import hudson.Launcher
import hudson.model.BuildListener
import hudson.model.AbstractBuild

import com.pearson.deployment.config.bitesize.BuildDependency


public abstract class AbstractPackageInstaller {
  AbstractBuild build
  Launcher launcher
  BuildListener listener
  OutputStream log
  BuildDependency dependency
  Package pkg

  AbstractPackageInstaller(AbstractBuild build, Launcher launcher, BuildListener listener, BuildDependency dependency) {
    this.build = build
    this.launcher = launcher
    this.listener = listener
    this.dependency = dependency
    this.log = listener.getLogger()
  }

  abstract void install()

  def exe(String cmd, OutputStream logger=this.log) {
    log.println("Executing ${cmd}")
    launcher.launch(cmd, build.getEnvVars(), logger, build.workspace)
  }

  abstract class Package {
  }
}