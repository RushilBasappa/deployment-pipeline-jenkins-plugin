package com.pearson.deployment

class KubeController extends KubeResource {
  KubeController(def config) {
    super('rc', config)
  }
}
