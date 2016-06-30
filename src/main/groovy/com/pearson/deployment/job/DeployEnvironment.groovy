package com.pearson.deployment.job

import hudson.FilePath
import hudson.model.BuildListener
import hudson.model.AbstractBuild
import hudson.remoting.VirtualChannel

import hudson.FilePath
import hudson.FilePath.FileCallable

import com.pearson.deployment.*
import com.pearson.deployment.kubernetes.*
import com.pearson.deployment.config.*
import com.pearson.deployment.config.bitesize.*
import com.pearson.deployment.helpers.*


class DeployEnvironment implements Serializable {
  private static def DEFAULT_TIMEOUT = 300

  private AbstractBuild build
  private EnvironmentConfig envConfig
  private LinkedHashMap definition
  private BuildListener listener
  private OutputStream log
  private EnvironmentsBitesize bsize
  Environment environment

  // DeployEnvironment(AbstractBuild build, BuildListener listener, String filename, String envname) {
  //   this.build = build
  //   this.listener = listener
  //   FilePath fp = new FilePath(build.getWorkspace(), filename)
  //   String contents = fp.readToString()

  //   this.log = listener.getLogger()

  //   envConfig = new EnvironmentConfig(contents, true)
  //   definition = envConfig.getEnvironment(envname)
  // }

  DeployEnvironment(AbstractBuild build, BuildListener listener, String filename, String envname) {
    this.build = build
    this.listener = listener
    this.log = listener.getLogger()
    this.bsize = getBsize()

    this.environment = bsize?.config?.environments?.find {
      it.name == envname
    }
  }

  EnvironmentsBitesize getBsize() {
    if (this.bsize != null) {
      return this.bsize
    }
    FilePath fp = new FilePath(workspace(), this.filename)
    InputStream stream = fp.act(new FileCallable<InputStream>() {
      private static final long serialVersionUID = 1L
            
      @Override
      public InputStream invoke(File file, VirtualChannel ch) throws IOException, InterruptedException {
        return new FileInputStream(file)
      }

      @Override
      public void checkRoles(org.jenkinsci.remoting.RoleChecker checker) throws SecurityException {
      }
    })

    new EnvironmentsBitesize(stream, this.log)  
  }

  def deploy() {
    environment.services.each { 
      deployService(it)
    }
    // for (def i = 0; i < definition.services?.size(); i++ ) {
    //   def svc = definition.services[i]
    //   if (svc.type == null) {
    //     deployService(svc)
    //   }
    // }
  }

  private def deployService(Service svc) {
    String appname_normalized = Helper.normalizeName(svc.name)
    String version = build.getEnvironment(listener).get("${appname_normalized}_VERSION".toString())
    if (version == null || version == "" || version == "latest") {
      return
    }
    svc.project = project()
    svc.namespace = environment.namespace

    def deployment = new KubeDeploymentHandler(svc, log)

    def existing = deployment.getHandler(svc.name)

    if (!existing) {
      log.println "MUST CREATE DEPLOYMENT FOR ${svc.name}:${svc.version}"
      deployment.create()
      watchDeploy(deployment)
    } else if (!existing.compareTo(deployment)) {
      log.println "MUST UPDATE DEPLOYMENT FOR ${svc.name}:${svc.version}"
      deployment.update()
      watchDeploy(deployment)
    }
  }

  void setBsize(InputStream stream) {
    this.bsize = new EnvironmentsBitesize(stream, this.log)
  }

  String project() {
    this.bsize.config.project
  }

  private FilePath workspace() {
    this.build.getWorkspace()
  }

  private def watchDeploy(KubeDeploymentHandler deploy) {
    def timer = 0
    def step = 5 // seconds
    def timeout = deploy.svc.deployment?.timeout ?: DEFAULT_TIMEOUT

    while (true) {
      def result = deployment.watch()
      //def status = deployment.done(result?.config)
      if (result == 'success') {
        log.print "Deployment for ${deploy.svc.name} finished"
        log.flush()
        break
      }

      timer = timer + step
      sleep(step*1000)

      if ( timer >= timeout) {
        throw new hudson.AbortException("Timeout reached, deployment failed")
      } else {
        log.print "."
        log.flush()
      }
    }
  }
  // private def deployService(def svc) {
    // def appname_normalized = Helper.normalizeName(svc.name)
    // svc.version = build.getEnvironment(listener).get("${appname_normalized}_VERSION".toString())
    // svc.project = envConfig.attributes.project
    // svc.docker_registry = Helper.dockerRegistry()


    // Service service = new Service(
    //   application: svc.application, 
    //   name: svc.name,
    //   project: svc.project,
    //   version: svc.version,
    //   external_url: svc.external_url,
    //   port: svc.port,
    //   env: svc.env,
    //   namespace: svc.namespace,
    //   image: svc.image
    //   replicas: svc.replicas,
    // )

  //   def deployment = new KubeDeployment(definition.namespace, svc)

  //   if (deployment.exist(svc.name)) {
  //     def oldDeployment = deployment.get(svc.name)
  //     log.println oldDeployment
      
  //     if (oldDeployment.config.version == svc.version) {
  //       log.println  "SAME VERSION ${svc.version} FOUND FOR ${svc.name}"
  //       log.flush()
  //     } else if ( svc.version != "" && svc.version != null ){
  //       log.println "MUST UPDATE ${svc.name} FROM ${oldDeployment.config.version} TO ${svc.version}"
  //       log.flush()
  //       updateService(deployment, svc)
  //     }
  //   } else {
  //     log.println "MUST CREATE ${svc.name}"
  //     log.flush()
  //     createService(deployment, svc)
  //   }
  // }

  private def updateService(KubeDeployment deployment, LinkedHashMap service) {
    deployment.update()
    watchDeploy(deployment, service)
  }

  private def createService(KubeDeployment deployment, LinkedHashMap service) {
    deployment.create()
    watchDeploy(deployment, service)
  }

  private def watchDeploy(KubeDeployment deployment, LinkedHashMap service) {
    def timer = 0
    def step = 5 // seconds
    def timeout = service.deployment?.timeout ?: DEFAULT_TIMEOUT

    while (true) {
      def result = deployment.watch()
      def status = deployment.done(result?.config)
      if (status == 'success') {
        log.println "Deployment for ${service.name} finished"
        log.flush()
        break
      }

      timer = timer + step
      sleep(step*1000)

      if ( timer >= timeout) {
        throw new hudson.AbortException("Timeout reached, deployment failed")
      } else {
        log.println "."
        log.flush()
      }
    }
  }
}
