package com.pearson.deployment.kubernetes

import com.pearson.deployment.config.kubernetes.*

abstract class AbstractKubeWrapper {
    KubeAPI client
    AbstractKubeResource resource

    def create() {
      client.create resource
    }

    def update() {
      client.apply resource
    }
  
}