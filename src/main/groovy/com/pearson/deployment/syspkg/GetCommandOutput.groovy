package com.pearson.deployment.syspkg

import hudson.remoting.Callable

class GetCommandOutput implements Callable<String,IOException> {
  String cmd

  public GetCommandOutput(def cmd) {
    this.cmd = cmd
  }

  public String call() {
    println "Executing something ${cmd}"
    exe(cmd)
  }

  @Override
  public void checkRoles(org.jenkinsci.remoting.RoleChecker checker)
  throws SecurityException {
  }

  private String exe(cmd) {
    Process command
    command = cmd.execute()
    command.waitFor()
    // def errOutput = command.err?.text

    if (command.exitValue() != 0) {
      throw new IOException("Error executing ${cmd}: ${command.err?.text}")
    }

    return "${command.err?.text}${command.text}"
  }
}
