package com.pearson.deployment.callable

// import java.util.Properties

import hudson.FilePath.FileCallable
import hudson.remoting.VirtualChannel

class PropertiesWriter implements FileCallable<Void> {

  private Properties props

  public PropertiesWriter(Properties props) {
    this.props = props
  }

  @Override
  public void checkRoles(org.jenkinsci.remoting.RoleChecker checker)
  throws SecurityException {
  }

  @Override
  public Void invoke(File f, VirtualChannel channel) {
    OutputStream out = new FileOutputStream(f)
    props.store(out, '')
  }
}
