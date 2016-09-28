package com.pearson.deployment.config.kubernetes

import groovy.json.*

class KubePodVolume {
  String name
  Map<String,String> persistentVolumeClaim


  LinkedHashMap asMap() {
    [
      "name": name,
      "persistentVolumeClaim": persistentVolumeClaim
    ]
  }

  @Override
  boolean equals(Object o) {
    true
  }
}