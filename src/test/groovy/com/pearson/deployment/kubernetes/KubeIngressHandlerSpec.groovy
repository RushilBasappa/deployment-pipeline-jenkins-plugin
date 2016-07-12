package com.pearson.deployment.kubernetes

import spock.lang.*

import org.yaml.snakeyaml.Yaml

import com.pearson.deployment.config.bitesize.*

class KubeIngressHandlerSpec extends Specification {
  KubeIngressHandler handler
  KubeAPI client
  LinkedHashMap resource
  Yaml yaml


  def setup() {
    yaml = new Yaml()

    def config = new File("src/test/resources/kubernetes/test-ingress.yaml").text
    resource = yaml.load(config)
    client = new FakeKubeWrapper('sample-app-dev')
    handler = new KubeIngressHandler(client, resource, System.out)
  }

  def "create new ingress" () {
    when:
      handler.create()
      def spec = client.fetch('ingress', 'test')
      def created = new KubeIngressHandler(client, spec, System.out)

    then:
      created.svc.port == 81
      created == handler
  }

  def "update ingress attribute" () {
    given: "Ingress exist"
      handler.create()

    when: "Ingress is updated"
      def newHandler = handler.getHandler("test")
      newHandler.svc."${attribute}" = attr_value
      newHandler.update()

    then: "Ingress host is different"
      def updated = handler.getHandler('test')
      updated != handler

    where:
      attribute     | attr_value                  | expected
      "external_url" | "test-updated.pearson.com" | "test-updated.pearson.com"
      "port"         | 88                         | 88
      "ssl"          | true                       | true
      "httpsBackend" | true                       | true
      "httpsOnly"    | true                       | true
      "namespace"    | "newnamespace"             | "newnamespace"
  }


  // TODO: these tests are placeholders for immutable env configuration
  // we should check that new name exist and old name is gone 
  def "update ingress name" () {
    given: "Ingress exist"
      handler.create()

    when: "Ingress name is changed"
      def config = new File("src/test/resources/kubernetes/test-ingress-updated.yaml").text
      resource = yaml.load(config)
      def newHandler = new KubeIngressHandler(client, resource, System.out)
      newHandler.svc.name = "test-name"
      newHandler.update()

    then: "Ingress with different name exist"
      def updated = newHandler.getHandler('test-name')
      updated.svc.name == "test-name"
      updated != handler
  }

  def "update without changes does nothing" () {   
    given: "Ingress exist"
    handler.create()
    when: "Ingress is updated"
    handler.update()
    then:
    def updated = handler.getHandler('test')
    updated == handler
  }

  def "fetching non-existing ingress throws ResourceNotFoundException" () {
    when: "We fetch non-existing ingress"
    def n = handler.getHandler('nonexisting')
    then: "ResourceNotFoundException is thrown"
    ResourceNotFoundException ex = thrown()
    ex.message == "Cannot find ingress nonexisting"
  }
} 