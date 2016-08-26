package com.pearson.deployment.syspkg

import spock.lang.*
import org.mockito.*

import hudson.Launcher


// import hudson.FilePath
// import hudson.Launcher
// import hudson.Launcher.LocalLauncher
// import hudson.model.BuildListener
// import hudson.model.StreamBuildListener
// import hudson.model.AbstractBuild
// import java.io.OutputStream

import com.pearson.deployment.config.bitesize.*

class DebianPackageInstallerSpec extends Specification {
  DebianPackageInstaller installer
  Launcher launcher

  // def setup() {
  //   AbstractBuild build = Mockito.mock(AbstractBuild.class)

  //   BuildListener listener = new StreamBuildListener(System.out)
  //   launcher = Mockito.mock(Launcher.class)

  //   BuildDependency dependency = new BuildDependency(
  //     type: "debian-package",
  //     package: "curl"
  //   )

  //   DebianPackageInstaller original = new DebianPackageInstaller(build, launcher, listener, dependency)
  //   installer = Mockito.spy(original)

  //   Mockito.doReturn("1.0.0").when(installer).exe() //"dpkg-query --show -f='\${version}' curl")

  // //   Mockito.when(
  // //     installer.exe("dpkg-query --show -f='\${version}' curl")
  // //   ).thenReturn("1.0.0")
  // }

  def "it must install package without version specified" () {

  }

  def "it must install package with version specified" () {

  }

}