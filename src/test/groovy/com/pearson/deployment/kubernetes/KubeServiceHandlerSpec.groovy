// package com.pearson.deployment.kubernetes

// import spock.lang.*

// import org.yaml.snakeyaml.Yaml

// import com.pearson.deployment.config.bitesize.*

// class KubeServiceHandlerSpec extends Specification {
//   KubeServiceHandler handler
//   KubeAPI client
//   LinkedHashMap resource
//   Yaml yaml


//   def setup() {
//     yaml = new Yaml()

//     def config = new File("src/test/resources/kubernetes/test-service.yaml").text
//     resource = yaml.load(config)
//     client = new FakeKubeWrapper('sample-app-dev')
//     handler = new KubeServiceHandler(client, resource, System.out)
//   }

//   def "create new service" () {
//     when:
//       handler.create()
//       def spec = client.fetch('service', 'test')
//       def created = new KubeServiceHandler(client, spec, System.out)

//     then:
//       created.svc.port == 80
//       created.svc.name == 'test'
//       created.svc.namespace == 'sample-app-dev'
//       created == handler
//   }

//   def "update service attribute" () {
//     given: "service is running"
//       handler.create()

//     when: "we update service's attribute'"
//       def config = new File("src/test/resources/kubernetes/test-service-updated.yaml").text
//       resource = yaml.load(config)
//       def newHandler = new KubeServiceHandler(client, resource, System.out)
//       newHandler.svc."${attribute}" = attr_value
//       newHandler.update()

//     then: "Attribute should be changed"
//       def updated = handler.getHandler('test')
//       updated.svc."${attribute}" == expected
//       updated != handler

//     where:
//       attribute | attr_value | expected
//       "port"    | 81         | 81
      
    
//   }

//   def "update without changes does nothing" () {
//     given: "Service is created"
//       handler.create()

//     when: "We run update"
//       handler.update()

//     then: "Object does not change"
//       def newHandler = handler.getHandler('test')
//       newHandler == handler
//   }

//   def "fetching non-existing service throws ResourceNotFoundException" () {
//     when: "We fetch non-existing service"
//       def n = handler.getHandler('nonexisting')

//     then: "ResourceNotFoundException is thrown"
//       ResourceNotFoundException ex = thrown()
//       ex.message == "Cannot find service nonexisting"
//   }

//   // Uncomment annotation if fails
//   // @Unroll
//   def "KubeServiceHandler comparison" () {
//    given:
//       handler.create()

//     when:
//       def newHandler = handler.getHandler('test')
//       newHandler.svc."${attribute}" = attr_value

//     then:
//       handler.equals(newHandler) == expected

//     where:
//       attribute     | attr_value            | expected
//       "name"        | "other"               | false
//       "name"        | "test"                | true
//       "namespace"   | "sample-app-prod"     | false
//       "namespace"   | "sample-app-dev"      | true
//       "port"         | 88                   | false
//       "port"         | 80                   | true
//   }
// }