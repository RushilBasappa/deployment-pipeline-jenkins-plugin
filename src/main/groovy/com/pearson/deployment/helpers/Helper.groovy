package com.pearson.deployment.helpers

import org.yaml.snakeyaml.Yaml
import org.json.JSONException;
import org.json.JSONObject;

class Helper implements Serializable {
  public static dockerRegistry() {
    Map<String, String> env = System.getenv()
    env.get('DOCKER_REGISTRY') ?: "bitesize-registry.default.svc.cluster.local:5000"
  }

  public static String yamlToJson(String y) {
    Yaml yaml = new Yaml();
    Object obj = yaml.load(y);

    try {
      return JSONValue.toJSONString(obj);
    } catch(all) {
      LOG.severe StackTraceUtils.deepSanitize(all).toString()
      return null
    }
  }
}
