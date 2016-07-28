package com.pearson.deployment.callable

// import java.io.InputStream
// import java.io.File

// import hudson.FilePath
import hudson.FilePath.FileCallable
import hudson.remoting.VirtualChannel

class WorkspaceReader implements FileCallable<InputStream> {
  private static final long serialVersionUID = 1L

  @Override
  public void checkRoles(org.jenkinsci.remoting.RoleChecker checker)
  throws SecurityException {
  }

  @Override
  public InputStream invoke(File f, VirtualChannel channel) {
    return new FileInputStream(f)
  }
}