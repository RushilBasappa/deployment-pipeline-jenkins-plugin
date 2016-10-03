package com.pearson.deployment.kubernetes

import com.pearson.deployment.config.kubernetes.*
import com.pearson.deployment.config.bitesize.*

class KubeThirdPartyWrapper extends AbstractKubeWrapper {
  static Class resourceClass = KubeThirdPartyResource

  KubeThirdPartyWrapper(KubeAPI client, Service svc) {
    this.client = client
    this.resource = new KubeThirdPartyResource(
      description: "Resource for ${svc.type}",
			metadata: [
				name: svc.name,
				namespace: svc.namespace,
				labels: [
          project: svc.project,
          application: svc.application,
          type: svc.type,
          version: svc.version,
          template_filename: svc.template_filename,
          parameter_filename: svc.parameter_filename,
          stack_name: svc.stack_name
        ]
      ]
    )
  }

  KubeThirdPartyWrapper(KubeAPI client, KubeThirdPartyResource resource) {
    this.client = client
    this.resource = resource
  }

  @Override
  boolean equals(Object o) {
    if (o == null) {
      return false
    }

    if (!KubeThirdPartyWrapper.class.isAssignableFrom(o.class)) {
      return false
    }

    def obj = (KubeThirdPartyWrapper)o

    this.resource == obj.resource
  }

  void setVersion(String value) {
    resource.labels['version'] = value
  }

  String getVersion() {
    resource.labels['version']
  }

  void setType(String value) {
    resource.labels['type'] = value
  }

  String getType() {
    resource.labels['type']
  }

  void setName(String value) {
    resource.name = value
  }

  String getName() {
    resource.name
  }

  void setTemplateFilename(String value) {
    resource.labels['template_filename'] = value
  }

  String getTemplateFilename() {
    resource.labels['template_filename']
  }

  void setParameterFilename(String value) {
    resource.labels['parameter_filename'] = value
  }

  String getParameterFilename() {
    resource.labels['parameter_filename']
  }
    
}