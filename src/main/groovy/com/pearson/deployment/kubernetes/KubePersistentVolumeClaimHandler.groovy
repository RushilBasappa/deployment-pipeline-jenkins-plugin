// package com.pearson.deployment.kubernetes


// class KubePersistentVolumeClaimHandler extends KubeResourceHandler {
//   KubeServiceHandler(KubeAPI client, Service svc, OutputStream log=System.out) {
//     super(client, svc, log)
//     this.kind = 'service'
//   }

//   KubeServiceHandler(KubeAPI client, LinkedHashMap resource, OutputStream log=System.out) {
//     super(client, new Service(), log)
//     this.kind = 'service'

//     svc.volumes.each {

//     }
  
//     // svc.name = resource.metadata.name
//     // svc.namespace = resource.metadata.namespace
//     // svc.port = resource.spec.ports[0].port
//   }

// }