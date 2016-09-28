package com.pearson.deployment.kubernetes

import com.pearson.deployment.config.bitesize.PersistentVolume
import com.pearson.deployment.config.kubernetes.KubePersistentVolumeClaim

class KubePersistentVolumeClaimWrapper extends AbstractKubeWrapper {

  KubePersistentVolumeClaimWrapper(KubeAPI client, PersistentVolume volume) {
    this.client = client
    this.resource = new KubePersistentVolumeClaim(
      metadata: [
        name: volume.name
      ],
      spec: [
        accessModes: volume.modes,
        resources: [
          requests: [
            storage: volume.size
          ]
        ],
        selector: [
          matchLabels: [
            name: volume.name
          ]
        ]
      ]
    )
  }

  KubePersistentVolumeClaimWrapper(KubeAPI client, KubePersistentVolumeClaim resource) {
    this.client = client
    this.resource = resource      
  }

  @Override
  boolean equals(Object o) {
    if ( o == null) {
      return false
    }

    if (!KubePersistentVolumeClaimWrapper.class.isAssignableFrom(o.class)) {
      return false
    }

    def obj = (KubePersistentVolumeClaimWrapper)o
    
    resource == obj.resource
  }

  void setName(String value) {
    resource.name = value
  }

  String getName() {
    resource.name
  }

  void setAccessModes(List<String> values) {
    resource.accessModes = values
  }

  List<String> getAccessModes() {
    resource.accessModes
  }

  void setSize(String value) {
    resource.size = value
  }

  String getSize() {
    resource.size
  }

} 