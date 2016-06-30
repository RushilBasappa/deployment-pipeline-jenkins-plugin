package com.pearson.deployment.kubernetes

import com.pearson.deployment.config.bitesize.Service

import org.yaml.snakeyaml.Yaml

class KubeResourceHandler implements Comparable {
    Service svc
    KubeWrapper client
    protected String handlerType = "pod"
    protected OutputStream log

    KubeResourceHandler(Service svc, OutputStream log=System.out) {
        this.svc = svc
        this.log = log
        this.client = new KubeWrapper(handlerType, svc.namespace)
    }

    KubeResourceHandler() {
    }

    def createOrUpdate() {
        def existing = getHandler(svc.name)

        if (!existing && handlerType != 'deployment') {
            log.println("... > creating ${svc.namespace}/${handlerType}/${svc.name} ")
            create()
        }

        if (existing && existing.compareTo(this)) {
            log.println "... > updating ${svc.namespace}/${handlerType}/${svc.name}"
            update()
        }
    }

    int compareTo(def other) {
        // implemented in subclasses
        return 0
    }

    private def update() {
        LinkedHashMap m = kubeSpec()        
        def f = writeSpecFile m        
        client.apply f
        
    }

    private def create() {
        def m = kubeSpec()
        def f = writeSpecFile m
        def r = client.create f
        r
    }

    private String writeSpecFile(LinkedHashMap contents) {
        Yaml yaml = new Yaml()
        String output = yaml.dumpAsMap(contents)
        String filename = resourceFilename()
        def writer = new File(filename)
        writer.write output

        return filename

    }

    private String resourceFilename() {
        def tmpDir = System.getProperty('java.io.tmpdir')
        "${tmpDir}/${svc.namespace}-${handlerType}-${svc.name}.yaml".toString()
    }
}