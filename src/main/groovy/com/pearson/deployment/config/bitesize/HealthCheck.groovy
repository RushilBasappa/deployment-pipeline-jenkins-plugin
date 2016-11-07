package com.pearson.deployment.config.bitesize

class HealthCheck implements Serializable {
    List<String> command
    // LinkedHashMap http
    int initial_delay  = 10
    int timeout   = 5
}

/*
health_check:
  http:
    path: /health_check
    port: TCP-PORT
*/

/*
health_check:
  command:
    - cat
    - /tmp/health.txt
    
*/