package com.pearson.deployment

class KubeService extends KubeResource {
  KubeService( def namespace, def config) {
    super('service', namespace, config)
  }
}
