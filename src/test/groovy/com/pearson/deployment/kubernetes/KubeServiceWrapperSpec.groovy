package com.pearson.deployment.kubernetes

import com.pearson.deployment.config.kubernetes.*

import spock.lang.*
import org.yaml.snakeyaml.Yaml

class KubeServiceWrapperSpec extends Specification {
  KubeServiceWrapper handler
  KubeAPI client

  def setup() {
    client = new FakeKubeWrapper('sample-app-dev')

    Yaml yaml = new Yaml()
    String config = new File("src/test/resources/kubernetes/test-service.yaml").text
    LinkedHashMap map = yaml.load(config)
    KubeService service = new KubeService(map)
    handler = new KubeServiceWrapper(client, service)
  }

  def "Create new service" () {
    given:
      handler.create()

    when:
      def service = client.get KubeService, 'test'

    then:
      service.ports[0].port == 80
      service.namespace == 'sample-app-dev'
  }

  def "Update service attribute" () {
    given:
      handler.create()

    when:
      handler.ports = [ 90 ]
      handler.update()
      println handler.ports[0].port
      def service = client.get KubeService, 'test'
      def updated = new KubeServiceWrapper(client, service)

    then:
      updated.ports[0].port == 90
  }

  def "Update without changes does nothing" () {
    given:
      handler.create()

    when:
      handler.update()
      def service = client.get KubeService, 'test'
      def updated = new KubeServiceWrapper(client, service)

    then:
      updated == handler

  }

  def "Fetching non-existent service throws ResourceNotFoundException" () {
    when:
      client.get KubeService, 'nonexistent'

    then:
      ResourceNotFoundException ex = thrown()
      ex.message == 'Cannot find service nonexistent'
  }
}
