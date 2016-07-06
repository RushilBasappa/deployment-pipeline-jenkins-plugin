package com.pearson.deployment.config.bitesize

class Service {
  String name
  String application
  String type = null
  String version = null
  String external_url
  int port = 80
  List<EnvVar> env = []
  String namespace = "default"
  String project
  String image
  int replicas = 1
  int available_replicas
  int updated_replicas

  String template_filename = ""
  String parameter_filename = ""
  String stack_name = ""
  boolean ssl = false
  String sslString
  boolean httpsOnly = false
  String httpsOnlyString
  boolean httpsBackend = false
  String httpsBackendString

  Service() {
    this.application = this.application ?: this.name
  }

  public void setApplication(String app) {
    this.application = app ? app : this.name
  }
  
  public String getApplication() {
    this.application ? this.application : this.name
  }

  public void setSslString(String val) {
    this.ssl = (val == "true")
  }

  public String getSslString() {
    this.ssl ? "true" : "false"
  }

  public void setHttpsOnlyString(String val) {
    this.httpsOnly = (val == "true")
  }

  public String getHttpsOnlyString() {
    this.httpsOnly ? "true" : "false"
  }

  public void setHttpsBackendString(String val) {
    this.httpsBackend = (val == "true")
  }

  public String getHttpsBackendString() {
    this.httpsBackend ? "true" : "false"
  }

  void setEnvVariables(List<Map> e) {
    def vars = []
    e.each {
      if (it.value) {
        vars << new EnvVar(name: it.name, value: it.value)
      }
    }
    this.env = vars
  }

}