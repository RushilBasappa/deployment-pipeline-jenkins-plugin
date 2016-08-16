package com.pearson.deployment.kubernetes

import spock.lang.*
import org.yaml.snakeyaml.Yaml

import com.pearson.deployment.config.bitesize.*

class KubeDeploymentHandlerSpec extends Specification {

  KubeDeploymentHandler handler
  KubeAPI client
  LinkedHashMap resource
  Yaml yaml

  def setup() {
    yaml = new Yaml()
    String config = new File("src/test/resources/kubernetes/test-deployment.yaml").text
      
    resource = yaml.load(config)
    client = new FakeKubeWrapper('sample-app-dev')
    handler = new KubeDeploymentHandler(client, resource, System.out)
  }

  def "create new deployment" () {
    given:
      handler.create()
    when:
      def spec = client.fetch('deployment', 'test')
      def created = new KubeDeploymentHandler(client, spec, System.out) 
    then:
      created.svc.version == '1.1.2'
  }

  def "upgrade deployment version" () {
    given: "We have a new service version"
      handler.create()
      handler.svc.version = '1.1.3'

    when: "Deployment is updated"
      handler.update()

    then: "Updated deployment's version is the one specified in config"      
      def updated = handler.getHandler('test')
      updated.svc.version == '1.1.3'
  }

  def "update without changes does nothing" () {
    given: "Deployment is created"
      handler.create()

    when: "We run update"
      handler.update()

    then: "Object does not change"
      def newHandler = handler.getHandler('test')
      newHandler == handler
  } 

  def "change environment variable" () {
    given:
      handler.create()

    when: "Environment variable is changed"
      def updatedConfig = new File("src/test/resources/kubernetes/test-deployment-updated.yaml").text
      resource = yaml.load(updatedConfig) 
      def updated = new KubeDeploymentHandler(client, resource, System.out)
      updated.update()

    then: "Deployment is updated"
      def newHandler = handler.getHandler('test')
      newHandler != handler
  }

  def "fetching non-existing deployment throws ResourceNotFoundException" () {
    when: "We fetch non-existing deployment"
      def n = handler.getHandler('nonexisting')

    then: "ResourceNotFoundException is thrown"
      ResourceNotFoundException ex = thrown()
      ex.message == "Cannot find deployment nonexisting"
  }

  // Uncomment this if failing
  // @Unroll
  def "KubeDeploymentHandler comparison" () {
    given:
      handler.create()

    when:
      def newHandler = handler.getHandler('test')
      newHandler.svc."${attribute}" = attr_value

    then:
      handler.equals(newHandler) == expected

    where:
      attribute     | attr_value                                          | expected
      "name"        | "other"                                             | false
      "name"        | "test"                                              | true
      "application" | "test"                                              | false 
      "application" | "sample-app"                                        | true
      "port"        | 88                                                  | false
      "port"        | 80                                                  | true
      "replicas"    | 1                                                   | true
      "replicas"    | 2                                                   | false
      "env"         | [new EnvVar(name: "NODE_ENV", value: "production")] | true
      "env"         | [new EnvVar(name: "NODE_ENV", value: "dev")]        | false
      "env"         | [new EnvVar(name: "DIFFERENT", value: "dev")]       | false
   
  }
    
}