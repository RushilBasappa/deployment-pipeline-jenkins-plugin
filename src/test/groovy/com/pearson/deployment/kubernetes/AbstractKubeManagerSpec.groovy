package com.pearson.deployment.kubernetes

import com.pearson.deployment.config.kubernetes.*
import com.pearson.deployment.config.bitesize.*
import spock.lang.*

class AbstractKubeManagerSpec extends Specification {
  KubeAPI client
  OutputStream log


  def setup() {
    client = new FakeKubeWrapper('sample-app-dev')
    client.setVersion('1.2.2')
    log = System.out
  }

  def "basic service collect resources" () {
    given:
      def e = new Environment()
      def svc = new Service([
        name: "svc"
      ])
      svc.setupDeploymentMethod(e)
    when:
      def rsc = AbstractKubeManager.collectResources(client, svc, log)

    then:
      rsc.size == 1
  }

  def "basic service collect resources on external_url" () {
    given:
      def e = new Environment( [ deployment: [method: 'bluegreen']])
      def svc = new Service( name: "svc", external_url: 'www.url.com')
      svc.setupDeploymentMethod(e)

    when:
      def rsc = AbstractKubeManager.collectResources(client, svc, log)
      def names = rsc.collect{ r -> r.name }

    then:
      names.findAll{ n -> n == "svc-blue" }.size == 2
      names.findAll{ n -> n == "svc-green" }.size == 2
      names.findAll{ n -> n == "svc"}.size == 1 // ingress service
      rsc.size == 5
  }

  def "service collect on thirdpartyresource 1.2.2" () {
    given:
      def e = new Environment( [ deployment: [method: 'bluegreen']])
      def svc = new Service( name: "svc", type: "mysql")
      svc.setupDeploymentMethod(e)

    when:
      def rsc = AbstractKubeManager.collectResources(client, svc, log)

    then:
      rsc.first().name == "mysql-svc.default.prsn.io"
      rsc.size == 1
  }

  // def "service collect on thirdpartyresource 1.3.0" () {
  //   given:
  //     def e = new Environment( [ deployment: [method: 'bluegreen']])
  //     def svc = new Service( name: "svc", type: "mysql")
  //     svc.setupDeploymentMethod(e)
  //
  //   when:
  //     def rsc = AbstractKubeManager.collectResources(client, svc, log)
  //
  //   then:
  //     rsc.first().name == "svc"
  //     rsc.size == 1
  // }
}
