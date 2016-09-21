package com.pearson.deployment.config.kubernetes

abstract class AbstractKubeResource {
    abstract static AbstractKubeResource loadFromString(String contents)
}