package com.pearson.deployment.syspkg

import java.util.concurrent.ExecutionException

import hudson.Launcher
import hudson.Launcher.ProcStarter
import hudson.model.BuildListener
import hudson.model.AbstractBuild
import hudson.Proc

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

  String exe(String cmd, OutputStream logger=this.log) {
    log.println("Executing ${cmd}")
    ProcStarter procStarter = launcher.launch().cmdAsSingleString(cmd)

    Proc proc = procStarter.start()

    // Proc proc = launcher.launch(cmd, build.getEnvVars(), logger, build.workspace)

    int exitCode = proc.join()

    if (exitCode != 0) {
      throw new ExecutionException("Executing ${cmd} failed")
    }

    return logger.toString()
  }

  abstract class Package {
  }
}