package com.pearson.deployment.kubernetes

import com.pearson.deployment.config.kubernetes.*

import spock.lang.*
import org.yaml.snakeyaml.Yaml

class KubeThirdPartyWrapperSpec extends Specification {
  KubeThirdPartyWrapper handler
  KubeAPI client

  def setup() {
    Yaml yaml = new Yaml()

    String config = new File("src/test/resources/kubernetes/test-thirdparty.yaml").text
    LinkedHashMap map = yaml.load(config)
    def resource = new KubeThirdPartyResource(map)

    client = new FakeKubeWrapper('sample-app-dev')
    handler = new KubeThirdPartyWrapper(client, resource)
    handler.create()
  }

  def "Create new ThirdPartyResource" () {
    when:
      def rsc = client.get KubeThirdPartyResource, 'mysql-db.sample-app-dev.prsn.io'
      def created = new KubeThirdPartyWrapper(client, rsc)

    then:
      created.version == "0.1"
      created.type == "mysql"
      created.name == "mysql-db.sample-app-dev.prsn.io"
      created.templateFilename == "mysql.template"
      created.parameterFilename == "mysql.parameter"
  }

  def "Update resource attribute" () {
    given:
      handler.version = "0.2"
      handler.update()

    when:
      def rsc = client.get KubeThirdPartyResource, 'mysql-db.sample-app-dev.prsn.io'
      def updated = new KubeThirdPartyWrapper(client, rsc)
    
    then:
      updated.version == "0.2"
      updated == handler
  }

  def "Update without changes does nothing" () {
    given:
      handler.update()

    when:
      def rsc = client.get KubeThirdPartyResource, 'mysql-db.sample-app-dev.prsn.io'
      def updated = new KubeThirdPartyWrapper(client, rsc)
  
    then:
      handler == updated 
  }

  def "Fetching non-existent resource throws ResourceNotFoundException" () {
    when:
      client.get KubeThirdPartyResource, 'nonexistent'
    
    then:
      ResourceNotFoundException ex = thrown()
      ex.message == 'Cannot find thirdpartyresource nonexistent'
  }

}