package com.pearson.deployment.kubernetes

import com.pearson.deployment.config.bitesize.Service
import com.pearson.deployment.config.kubernetes.KubeIngress

class KubeIngressWrapper extends AbstractKubeWrapper {
  static Class resourceClass = KubeIngress

    KubeIngressWrapper(KubeAPI client, Service svc) {
        this.client = client
        this.resource = new KubeIngress(            
          metadata: [
            name: svc.name,
            namespace: svc.namespace,
            labels: [
              ssl: svc.ssl.toString(),
              httpsBackend: svc.httpsBackend.toString(),
              httpsOnly: svc.httpsOnly.toString(),
              creator: "pipeline",
              name: svc.name
            ]
          ],
          spec: [
            rules: [
              [
                host: svc.external_url,
                http: [
                  paths: [
                    [
                      path: "/",
                      backend: [
                        serviceName: svc.backend,
                        servicePort: svc.port
                      ]
                    ]
                  ]
                ]
              ]
            ]
          ]
        )
    }

    KubeIngressWrapper(KubeAPI client, KubeIngress ingress) {
      this.client   = client
      this.resource = ingress
    }

    @Override
    boolean equals(Object o) {
      if (o == null) {
        return false
      }

      if (!KubeIngressWrapper.class.isAssignableFrom(o.class)) {
        return false
      }

      def obj = (KubeIngressWrapper)o

      (this.resource == obj.resource)
    }

    void setName(String value) {
      resource.name = value
    }

    String getName() {
      resource.name
    }

    void setSsl(boolean value) {
      resource.labels['ssl'] = value.toString()
    }

    boolean getSsl() {
      resource.labels['ssl'] ? resource.labels['ssl'].toBoolean() : false
    }

    void setHttpsBackend(String value) {
      resource.labels['httpsBackend'] = value
    }

    String getHttpsBackend() {
      resource.labels['httpsBackend']
    }

    void setHttpsOnly(boolean value) {
      resource.labels['httpsOnly'] = value.toString()
    }

    boolean getHttpsOnly() {
      resource.labels['httpsOnly'] ? resource.labels['httpsOnly'].toBoolean() : false
    }

    void setExternalUrl(String value) {
      resource.rules[0].host = value
    }

    String getExternalUrl() {
      resource.rules[0].host
    }

    void setBackend(String value) {
      resource.rules[0].backendName = value
    }

    String getBackend() {
      resource.rules[0].backendName
    }

    void setPort(Integer value) {
      resource.rules[0].paths[0].backendPort = value
    }

    Integer getPort() {
      resource.rules[0].paths[0].backendPort
    }
}