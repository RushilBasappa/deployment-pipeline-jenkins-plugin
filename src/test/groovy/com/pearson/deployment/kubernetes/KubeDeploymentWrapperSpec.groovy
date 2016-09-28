package com.pearson.deployment.kubernetes

import com.pearson.deployment.config.kubernetes.*
import spock.lang.*
import org.yaml.snakeyaml.Yaml

class KubeDeploymentWrapperSpec extends Specification {
  KubeDeploymentWrapper handler
  KubeAPI client


  def setup() {
    Yaml yaml = new Yaml()
    String config = new File("src/test/resources/kubernetes/test-deployment.yaml").text
      
    LinkedHashMap resource = yaml.load(config)
    client = new FakeKubeWrapper('sample-app-dev')
    KubeDeployment deployment  = new KubeDeployment(resource)
    handler = new KubeDeploymentWrapper(client, deployment)
  }

  def "create new deployment" () {
    given:
      handler.create()
    when:
      KubeDeployment deployment = client.get(KubeDeployment, 'test')
      def created = new KubeDeploymentWrapper(client, deployment) 
    then:
      created.version == '1.1.2'
  }

  def "upgrade deployment version" () {
    given: "We have a new service version"
      handler.create()
      handler.version = '1.1.3'

    when: "Deployment is updated"
      handler.update()

    then: "Updated deployment's version is the one specified in config" 
      KubeDeployment deployment = client.get KubeDeployment, 'test'     
      def updated = new KubeDeploymentWrapper(client, deployment)
      updated.version == '1.1.3'
  }

  def "update without changes does nothing" () {
    given: "Deployment is created"
      handler.create()

    when: "We run update"
      handler.update()

    then: "Object does not change"
      def deployment = client.get KubeDeployment, 'test'
      def updated = new KubeDeploymentWrapper(client, deployment)     

      updated == handler
  }
}