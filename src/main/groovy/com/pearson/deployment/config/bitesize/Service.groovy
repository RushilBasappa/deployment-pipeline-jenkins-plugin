package com.pearson.deployment.config.bitesize

class Service extends ManagedResource implements Serializable, Cloneable {
  String application
  String backend
  String type = null
  String version = null
  String external_url
  String port = "80"
  List<EnvVar> env = []
  List<PersistentVolume> volumes

  String namespace = "default"
  String project
  String image
  String selector
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
  HealthCheck health_check

  Tableau tableau_commands

  BVT bvt_commands

  DeploymentMethod deployment

  String deploymentMethod = "rolling-upgrade"

  Map<String, String> options

  public Object clone() throws CloneNotSupportedException {
    return super.clone()
  }


  boolean equals(Object obj) {
    if (obj == null) {
      return false
    }

    if (!Service.class.isAssignableFrom(obj.getClass())) {
      return false
    }

    Service other = (Service)obj

    (name == other.name) &&
    (type == other.type) &&
    (version == other.version) &&
    (external_url == other.external_url) &&
    (port == other.port) &&
    (env == other.env) &&
    (namespace == other.namespace) &&
    (project == other.project) &&
    (image == other.image) &&
    (replicas == other.replicas) &&
    (available_replicas == other.available_replicas) &&
    (template_filename == other.template_filename) &&
    (parameter_filename == other.parameter_filename) &&
    (stack_name == other.stack_name) &&
    (ssl == other.ssl) &&
    (httpsOnly == other.httpsOnly) &&
    (httpsBackend == other.httpsBackend) &&
    (selector == other.selector)
  }

  public boolean isTableauEnabled() {
    if(this.tableau_commands != null && this.tableau_commands.commands.size() > 0) {
      return true
    }
    else false
  }

  public boolean isBVTEnabled() {
    if (this.bvt_commands != null && this.bvt_commands.commands.size() > 0) {
      return true
    }
    else false
  }

  public boolean isThirdParty() {
    this.type != null
  }

  public void setApplication(String value) {
    this.application = value
  }

  public String getApplication() {
    this.application ? this.application : this.name
  }

  public String getBackend() {
    this.backend ? this.backend : this.name
  }

  public void setSslString(String val) {
    ssl = val ? val.toBoolean() : false
  }

  public String getSslString() {
    String.valueOf ssl
  }

  public void setHttpsOnlyString(String val) {
    httpsOnly = val ? val.toBoolean() : false
  }

  public String getHttpsOnlyString() {
    String.valueOf httpsOnly
  }

  public void setHttpsBackendString(String val) {
    httpsBackend = val ? val.toBoolean() : false
  }

  public String getHttpsBackendString() {
    String.valueOf httpsBackend
  }

  def getPorts() {
    def prts = port.split(",")
    prts.collect{ p -> p.toInteger() }
  }

  def setPorts(Integer[] ports) {
    port = ports.join(",")
  }

  // public String getVersion() {
  //   version
  // }

  void setEnvVariables(List<Map> e) {
    def vars = []
    e.each {
      if (it.value) {
        vars << new EnvVar(name: it.name, value: it.value)
      }
    }
    this.env = vars
  }

  void setupDeploymentMethod(def environment) {
    if (deployment == null) {
      println "Deployment method for deployment itself is null: ${environment.name}:${name}"
      this.deployment = new DeploymentMethod()
    }

    this.deployment.method = this.deployment.method ?: environment.deployment?.method
    this.deployment.active = this.deployment.active ?: environment.deployment?.active
    this.deployment.mode   = this.deployment.mode ?: environment.deployment?.mode
  }

}
