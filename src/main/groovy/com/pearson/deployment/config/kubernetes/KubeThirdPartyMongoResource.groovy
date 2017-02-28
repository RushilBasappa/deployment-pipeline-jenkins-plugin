package com.pearson.deployment.config.kubernetes

class KubeThirdPartyMongoResource extends KubeThirdPartyGenericResource {
    public static final String kind = "Mongo"

    KubeThirdPartyMongoResource(LinkedHashMap o) {
      super(o)
    }
}
