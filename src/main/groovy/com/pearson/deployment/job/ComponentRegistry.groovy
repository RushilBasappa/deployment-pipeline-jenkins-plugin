package com.pearson.deployment.job

import hudson.model.*
import jenkins.model.*
import java.util.Properties
import java.io.FileInputStream

import hudson.plugins.copyartifact.*
// import hudson.model.AbstractBuild
import hudson.Launcher
// import hudson.model.BuildListener
import hudson.FilePath
import jenkins.model.ArtifactManager

import com.pearson.deployment.syspkg.DebInfo
import com.pearson.deployment.callable.PropertiesWriter

class ComponentRegistry implements Serializable {
  private AbstractBuild build
  private AbstractBuild artifactBuild
  private BuildListener listener
  private Launcher launcher
  private def artifacts
  private ArtifactManager artifactManager
  private ArtifactManager buildAm
  private String buildName
  private String buildId

  private String propertiesFile = "manifest"

  ComponentRegistry(AbstractBuild build, Launcher launcher,  BuildListener listener, String buildName, String buildId) {
    this.build         = build
    this.launcher      = launcher
    this.listener      = listener
    this.artifactBuild = Jenkins.instance.getItem(buildName).getBuild(buildId)
    this.artifacts = this.artifactBuild.getArtifacts()
    this.artifactManager = this.artifactBuild.getArtifactManager()
    this.buildAm         = this.build.getArtifactManager()

    // this.application = application
    this.buildId = buildId
    this.buildName = buildName

    writeManifest()
    this.buildAm.archive(
      build.getWorkspace(),
      launcher,
      listener,
      [ "manifest": "manifest"]
    )
  }

  def writeManifest() {
    FilePath fp
    Properties properties = getProperties()

    String fullPath = "${build.workspace}/${propertiesFile}".toString()

    artifacts.each { artifact ->
      def artifactPath = artifactManager.root().child(artifact.relativePath)
      def deb = new DebInfo(artifactPath)
      def name = deb.name()
      def version = deb.version()
      properties.setProperty("$buildName.$name.$version", buildId)
    }

    if(build.workspace.isRemote()) {
      def channel = build.workspace.channel;
      fp = new FilePath(channel, fullPath)
    } else {
      fp = new FilePath(new File(fullPath))
    }

    if (fp != null ) {
      fp.act(new PropertiesWriter(properties))
    }

  }

  private Properties getProperties() {
    try {
      CopyArtifact copyArtifact = new CopyArtifact(
        build.getProject().getName(),
        "",
        new StatusBuildSelector(true),
        propertiesFile,
        null,
        false,
        true
      )
      def perform = copyArtifact.class.getMethod("perform", AbstractBuild, Launcher, BuildListener)
      perform.invoke(copyArtifact, build, launcher, listener)

      // Redo to FilePath reader
      def props = new Properties()
      String fullPath = "${build.workspace}/${propertiesFile}".toString()

      FileInputStream stream = new FileInputStream(fullPath)
      props.load(stream)
      return props

    } catch (Exception e) {
      e.printStackTrace()
      return new Properties()
    }

  }



}
