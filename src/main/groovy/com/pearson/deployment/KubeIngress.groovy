package com.pearson.deployment

class KubeIngress extends KubeResource {
  KubeIngress(def c) {
    super('ing', c)
  }

  def compareTo(KubeIngress i, o) {

  }


  def configToSpec(def s) {
    def svc = s
    if (s == null ) {
      svc = config
    }
    [
      "apiVersion": "extensions/v1beta1",
      "kind": "Ingress",
      "metadata" : [
        "name": svc.name,
        "labels": [
          "creator": "pipeline",
          "name": svc.name
        ]
      ],
      "spec": [
        "rules": [
          [
            "host": svc.external_url,
            "http": [
              "paths": [
                [
                  "path": "/",
                  "backend": [
                    "serviceName": svc.name,
                    "servicePort": svc.port
                  ]
                ]
              ]
            ]
          ]
        ]
      ]
    ]

    def specToConfig(def spec) {
      [
        "name": spec.metadata.name,
        "external_url": spec.spec.rules[0]?.host,
        "port": spec.spec.rules[0]?.port
      ]
    }
    // """\
    //   apiVersion: extensions/v1beta1
    //   kind: Ingress
    //   metadata:
    //     name: ${svc.name}
    //     labels:
    //       creator: pipeline
    //       name: ${svc.name}
    //   spec:
    //     rules:
    //       - host: ${svc.external_url}
    //         http:
    //           paths:
    //           - path: /
    //             backend:
    //               serviceName: ${svc.name}
    //               servicePort: ${svc.port}
    // """.trim().stripIndent()
  }
}
