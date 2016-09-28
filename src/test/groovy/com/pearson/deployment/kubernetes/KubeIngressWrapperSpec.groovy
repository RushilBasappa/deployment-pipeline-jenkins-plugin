package com.pearson.deployment.kubernetes

import com.pearson.deployment.config.kubernetes.*

import spock.lang.*
import org.yaml.snakeyaml.Yaml

class KubeIngressWrapperSpec extends Specification {
  KubeIngressWrapper handler
  KubeAPI client

  def setup() {
    Yaml yaml = new Yaml()
    String config = new File("src/test/resources/kubernetes/test-ingress.yaml").text
    LinkedHashMap map = yaml.load(config)
    KubeIngress resource = new KubeIngress(map)

    client = new FakeKubeWrapper('sample-app-dev')
    handler = new KubeIngressWrapper(client, resource)
  }

  def "Create new ingress" () {
    given:
      handler.create()

    when:
      def ingress = client.get KubeIngress, 'test'

    then:
      ingress.name == 'test'
      ingress.rules.size() == 1


  }

  @Unroll
  def "Update ingress attribute" () {
    given:
      handler.create()

    when:
      handler."$method" value      
      handler.update()

    then:
      def ingress = client.get KubeIngress, 'test'
      def updated = new KubeIngressWrapper(client, ingress)
      updated."$field" == expectation

    where:
     field         | method            | value        | expectation
     'port'        | 'setPort'         | 9990         | 9990
     'externalUrl' | 'setExternalUrl'  | 'www.zzz.ws' | 'www.zzz.ws'
     'ssl'         | 'setSsl'          | true         | true
     'httpsOnly'   | 'setHttpsOnly'    | true         | true
     'httpsBackend'| 'setHttpsBackend' | 'lewl'       | 'lewl'
  }

  def "Update ingress name" () {
    given:
      handler.create()
    when:
      handler.name = 'newtest'
      handler.update()
      def updated = client.get KubeIngress, 'newtest'
    then:      
      updated.name == 'newtest'
  }

  def "Update without changes does nothing" () {
    given:
      handler.create()
    when:
      handler.update()
      def ingress = client.get KubeIngress, 'test'
      def updated = new KubeIngressWrapper(client, ingress)
    then:
      handler == updated
  }

  def "Fetching non-existing ingress throws ResourceNotFoundException" () {
    when:
      client.get KubeIngress, 'nonexistent'
    
    then:
      ResourceNotFoundException ex = thrown()
      ex.message == 'Cannot find ingress nonexistent'


  }

  def "KubeIngressWrapper comparison" () {

  }
}