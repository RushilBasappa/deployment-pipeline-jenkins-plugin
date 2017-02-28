package com.pearson.deployment.config.kubernetes

class KubeThirdPartyMysqlResource extends KubeThirdPartyGenericResource {
    public static final String kind = "Mysql"

    KubeThirdPartyMysqlResource(LinkedHashMap o) {
      super(o)
    }
}
