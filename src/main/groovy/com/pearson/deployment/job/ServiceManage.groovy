package com.pearson.deployment.job

import com.pearson.deployment.*
import com.pearson.deployment.config.*
import com.pearson.deployment.config.bitesize.*
import com.pearson.deployment.builder.*
import com.pearson.deployment.kubernetes.*
import com.pearson.deployment.helpers.*

import org.yaml.snakeyaml.Yaml
import hudson.FilePath
import hudson.FilePath.FileCallable
import hudson.model.BuildListener
import hudson.model.AbstractBuild
import hudson.remoting.VirtualChannel

import java.security.SecureRandom
import java.math.BigInteger
import java.io.*

class ServiceManage implements Serializable {
    // EnvironmentConfig envConfig
    EnvironmentsBitesize bsize
    boolean changed
    
    private AbstractBuild build
    private BuildListener listener
    private String filename
    private SecureRandom random
    private OutputStream log
    private InputStream stream

    ServiceManage(AbstractBuild build, BuildListener listener, String filename) {
        this.build = build
        this.listener = listener
        this.changed = false
        this.log = listener.getLogger()
        this.random = new SecureRandom()
        this.filename = filename
        try {
            this.bsize = getBsize()
        } catch (all) {
            this.bsize = null
        } 
    }

    def run() {
        this.bsize?.config.environments?.each {
            log.println "Configuring environment ${it.name}"
            manage(it)
        }
    }

    EnvironmentsBitesize getBsize() {
        if (this.bsize != null) {
            return this.bsize
        }
        FilePath fp = new FilePath(workspace(), this.filename)
        stream = fp.act(new FileCallable<InputStream>() {
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

    void setBsize(InputStream stream) {
        this.bsize = new EnvironmentsBitesize(stream, this.log)
    }

    String project() {
        this.bsize.config.project
    }

    private def manage(Environment environment) {
        environment.services?.each { service ->
            service.project = project()
            service.namespace = environment.namespace

            def kube = new KubeHandler(service, log)
            def ch = kube.manage()
            this.changed = ch ?: this.changed
            // manageService(service, environment.namespace)
        }
        log.println "Changed: ${this.changed}"
    }

    private FilePath workspace() {
       this.build.getWorkspace()
    }

    // private def manageService(Service service, String namespace) {
    //     // Check if it's a thirdparty service definition
    //     if (service.type != null) {
    //         setupThirdpartyResource(service, namespace)
    //     } else {
    //         setupDeploymentResource(service, namespace)
    //         setupServiceResource(service, namespace)
    //         setupIngressResource(service, namespace)
    //     }
    // }

    // private def setupThirdpartyResource(Service service, String namespace) {
    //     def ch
    //     def randomstring = new BigInteger(64, random).toString(32)
    //     def rscConf = [
    //         "template_filename": "${service.type}.template".toString(),
    //         "parameter_filename": "${service.type}.parameter".toString(),
    //         "version": "${service.version}".toString(),
    //         "namespace": namespace,
    //         "name": "${service.type}-${service.name}.${namespace}.prsn.io".toString(),
    //         "type": "${service.type}".toString(),
    //         "stack_name": "${service.namespace}-${service.type}-${randomstring}".toString()
    //     ]
    //     def rsc = new KubeThirdPartyResource(namespace, rscConf, logger)
    //     ch = rsc.createOrUpdate()
    //     this.changed = ch ? true : changed
    //     // if changed, set changed = true
    // }

    // private def setupDeploymentResource(Service service, String namespace) {
    //     def ch
    //     def deployment = new KubeDeployment(namespace, service, logger)
    //     if (deployment.exist(service.name) ) {
    //         if (deployment.config.version == "latest" || deployment.config.version == null) {
    //             def old = deployment.get(service.name)
    //             deployment.config.version = old.config.version
    //         }
    //         ch = deployment.createOrUpdate()
    //         this.changed = ch ? true : changed
    //         // if changed, set changed = true
    //     }
    // }

    // private def setupServiceResource(Service service, String namespace) {
    //     def svc = new KubeService(namespace, service, logger)
    //     def ch = svc.createOrUpdate()
    //     this.changed = ch ? true : changed
    // }

    // private def setupIngressResource(Service service, String namespace) {
    //     if (service.external_url != null) {
    //         def ingress = new KubeIngress(namespace, service, logger)
    //         def ch = ingress.createOrUpdate()
    //         this.changed = ch ? true : changed
    //     }
    //     false
    // }

    def deleteBuild() {        
        def proj = build.getProject()
        def buildId = build.number

        build.delete()
        proj.nextBuildNumber = buildId
        // proj.save()        
    }
}