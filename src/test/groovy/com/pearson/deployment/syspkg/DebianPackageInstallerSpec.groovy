package com.pearson.deployment.syspkg

import spock.lang.*
import groovy.mock.interceptor.MockFor
import org.junit.Rule

import hudson.Launcher
import hudson.model.BuildListener
import hudson.model.AbstractBuild

import com.pearson.deployment.config.bitesize.*

class DebianPackageInstallerSpec extends Specification {
    def setup() {
        AbstractBuild build = Mockito.mock(AbstractBuild.class)
        Launcher launcher = Mockito.mock(Launcher.class)
        BuildListener listener = Mockito.mock(BuildListener.class)
        Mockito.when(listener.getLogger()).thenReturn(System.out)

        BuildDependency dependency = new BuildDependency(
            type: "debian-package",
            name: "curl"
        )

        DebianPackageInstaller installer = new DebianPackageInstaller(build, launcher, listener, dependency)
    }
}