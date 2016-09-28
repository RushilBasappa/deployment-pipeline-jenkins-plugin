// package com.pearson.deployment.kubernetes

// import spock.lang.*

// import org.yaml.snakeyaml.Yaml

// import com.pearson.deployment.config.bitesize.*

// class KubeThirdpartyHandlerSpec extends Specification {
//   KubeThirdpartyHandler handler
//   KubeAPI client
//   LinkedHashMap resource
//   Yaml yaml


//   def setup() {
//     yaml = new Yaml()

//     def config = new File("src/test/resources/kubernetes/test-thirdparty.yaml").text
//     resource = yaml.load(config)
//     client = new FakeKubeWrapper('sample-app-dev')
//     handler = new KubeThirdpartyHandler(client, resource, System.out)
//   }

//   def "create new thirdpartyresource" () {
//     when:
//       handler.create()
//       def spec = client.fetch('thirdpartyresource', 'mysql-db.sample-app-dev.prsn.io')
//       def created = new KubeThirdpartyHandler(client, spec, System.out)

//     then:
//       created.svc.version == "0.1"
//       created.svc.type == "mysql"
//       created.svc.name == "mysql-db.sample-app-dev.prsn.io"
//       created.svc.template_filename == "mysql.template"
//       created.svc.parameter_filename == "mysql.parameter"
//       // created.svc.namespace == "sample-app-dev"
//       created == handler
//   }

//   def "name is updated" () {
//     given: "thirdpartyresource is created"
//       handler.create()

//     when: "we update thirdpartyresource name"
//       def newHandler = handler.getHandler("mysql-db.sample-app-dev.prsn.io")
//       newHandler.svc.name = "newname"
//       newHandler.update()

//     then: "newname thirdpartyresource exists"
//       def updated = handler.getHandler("newname")
//       updated.svc.name == "newname"
//       updated != handler
//   }

//   def "attribute is updated" () {
//     given: "thirdpartyresource is created"
//       handler.create()

//     when: "we update thirdpartyresource attribute"
//       def newHandler = handler.getHandler("mysql-db.sample-app-dev.prsn.io")
//       newHandler.svc."${attribute}" = attr_value
//       newHandler.update()

//     then: "attribute is changed"
//       def updated = handler.getHandler("mysql-db.sample-app-dev.prsn.io")
//       updated.svc."${attribute}" == expected
//       updated != handler

//     where:
//       attribute            | attr_value | expected
//       "parameter_filename" | "zorg"     | "zorg" 
//       "template_filename"  | "zorg"     | "zorg"
//       "version"            | "0.2"      | "0.2"
//       "stack_name"         | "changed"  | "changed"
//       "type"               | "database" | "database"
//   }

//   def "fetching non-existing thirdpartyresource throws ResourceNotFoundException" () {
//     when: "We fetch non-existing thirdpartyresource"
//       def n = handler.getHandler('nonexisting')

//     then: "ResourceNotFoundException is thrown"
//       ResourceNotFoundException ex = thrown()
//       ex.message == "Cannot find thirdpartyresource nonexisting"
//   }

//   // Uncomment annotation if fails
//   // @Unroll
//   def "KubeThirdpartyHandler comparison" () {
//    given:
//       handler.create()

//     when:
//       def newHandler = handler.getHandler('mysql-db.sample-app-dev.prsn.io')
//       newHandler.svc."${attribute}" = attr_value

//     then:
//       handler.equals(newHandler) == expected

//     where:
//       attribute            | attr_value                        | expected
//       "name"               | "nosql-db.sample-app-dev.prsn.io" | false
//       "name"               | "mysql-db.sample-app-dev.prsn.io" | true
//       "template_filename"  | "nosql.template"                  | false
//       "template_filename"  | "mysql.template"                  | true
//       "parameter_filename" | "nosql.parameter"                 | false
//       "parameter_filename" | "mysql.parameter"                 | true
//       "stack_name"         | "neee-mysql-4te5c4dahc8ug"        | false
//       "stack_name"         | "null-mysql-4te5c4dahc8ug"        | true
//       "version"            | "0.2"                             | false
//       "version"            | "0.1"                             | true
//       "type"               | "nosql"                           | false
//       "type"               | "mysql"                           | true
  
//   }
// }