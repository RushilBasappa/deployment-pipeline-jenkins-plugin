package com.pearson.deployment

class KubeService extends KubeResource {
  KubeService( def config) {
    super('service', config)
  }
}
