package com.pearson.deployment.kubernetes

import com.github.zafarkhaja.semver.Version

interface KubeAPI {
    void create(String kind, LinkedHashMap resource)
    void apply(String kind, LinkedHashMap resource)
    LinkedHashMap fetch(String kind, String name) throws ResourceNotFoundException

    void setNamespace(String namespace)

    boolean namespaceExist(String namespace)

    Version version()
}
