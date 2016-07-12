package com.pearson.deployment.syspkg

import spock.lang.*
import groovy.mock.interceptor.MockFor
import org.mockito.*
import org.junit.Rule

import hudson.FilePath
import hudson.Launcher
import hudson.Launcher.LocalLauncher
import hudson.model.BuildListener
import hudson.model.StreamBuildListener
import hudson.model.AbstractBuild
import java.io.OutputStream

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

  // def "it must do something" () {
  //   when: "we install missing package"
  //   installer.install()
  //   then: "apt-get install must be called"
  //   Mockito.verify(launcher, times(1)).launch("apt-get install curl")
  // }
}