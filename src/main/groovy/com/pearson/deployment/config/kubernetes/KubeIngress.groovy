package com.pearson.deployment.config.kubernetes

import groovy.json.*

class KubeIngress implements AbstractKubeResource {
  String name
  String namespace
  String ssl
  String httpsOnly
  String httpsBackend
  String externalUrl
  String path = "/"
  String backend
  String port


  public static KubeIngress loadFromString(String contents) {
    JsonSlurper slurper = new JsonSlurper()
    def o = slurper.parseText contents
    def httpRule = o.spec?.rules[0]
    def backend = httpRule?.http?.paths[0]?.backend
    def port = backend?.servicePort ?: 80

    new KubeIngress(
      name:         o.metadata?.name,
      namespace:    o.metadata?.namespace,
      externalUrl:  httpRule?.host,
      httpsOnly:    o.metadata?.labels?.httpsOnly,
      httpsBackend: o.metadata?.labels?.httpsBackend,
      ssl:          o.metadata?.labels?.ssl,
      port:         port
    )
  }

  String toString() {
    def json = new JsonBuilder()

    def root = json {
      apiVersion "extensions/v1beta1"
      metadata {
        name name
        namespace namespace
        labels {
          creator "pipeline"
          name name
          ssl ssl
          if (httpsOnly) {
            httpsOnly httpsOnly
          }

          if (httpsBackend) {
            httpsBackend httpsBackend
          }
        }
      }
      spec {
        rules(
          {
            host externalUrl
            http {
              paths(
                {
                  path path
                  backend {
                    serviceName backend
                    servicePort port
                  }
                }
              )
            }
          }
        )
      }
      name name
      namespace namespace

    }
    json.toString()
  }
}