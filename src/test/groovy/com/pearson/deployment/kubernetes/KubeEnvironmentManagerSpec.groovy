// package com.pearson.deployment.kubernetes

// import spock.lang.*
// import org.yaml.snakeyaml.Yaml

// import com.pearson.deployment.config.bitesize.*

// class KubeEnvironmentManagerSpec extends Specification {
//   KubeAPI client
//   LinkedHashMap resource
//   Yaml yaml
//   KubeEnvironmentManager manager
//   KubeEnvironmentManager eModified
//   final String assetDir = "src/test/resources/kubernetes/environment_manager"

//   def setup() {
//     client = new FakeKubeWrapper('sample-app-dev')
//   }

//   @Unroll
//   def "KubeServiceManager modifications for #deploymentName" () {
//     given:
//       Environment bg = Environment.readFromString asset(origAsset)
//       manager = new KubeEnvironmentManager(client, 'project', bg)
//       manager.manage()

//     when:
//       Environment bgModified = Environment.readFromString asset(modAsset)
//       manager = new KubeEnvironmentManager(client, 'project', bgModified)
//       manager.manage()
//       def deployment = client.fetch "deployment", deploymentName
    
//     then:
//       deployment.spec.replicas == expectedCount

//     where:
//       origAsset                   | modAsset                    | deploymentName           | expectedCount
//       'original.yaml'             | 'modified.yaml'             | 'test-service'           | 4
//       'bg-original.yaml'          | 'bg-modified.yaml'          | 'bluegreen-service-blue' | 4
//       'bg-original.yaml'          | 'bg-modified.yaml'          | 'bluegreen-service-green'| 4    
//   }


//   @Unroll
//   def "Blue-green ingresses for #ingressHost" () {
//     given:
//       Environment bg = Environment.readFromString asset('bg-original.yaml')
//       manager = new KubeEnvironmentManager(client, 'project', bg)
//       manager.manage()
//     when:
//       def ingress = client.fetch 'ingress', ingressName
//     then:
//       ingress.spec.rules[0].host == ingressHost
//       ingress.spec.rules[0].http.paths[0].backend.serviceName == serviceName

//     where:
//       ingressName               | ingressHost               | serviceName
//       'bluegreen-service'       | 'bghost.pearson.com'      | 'bluegreen-service-blue'
//       'bluegreen-service-blue'  | 'bghost-blue.pearson.com' | 'bluegreen-service-blue'
//       'bluegreen-service-green' | 'bghost-green.pearson.com'| 'bluegreen-service-green'
//   }

//   def "Ingress controllers for thirdpartycontrollers are not created" () {
//     given:
//       Environment bg = Environment.readFromString asset('bg-original.yaml')
//       manager = new KubeEnvironmentManager(client, 'project', bg)
//       manager.manage()

//     when:
//       def ingress = client.fetch 'ingress', ingressName
//     then:
//       def ex = thrown(ResourceNotFoundException)
//       ex.message == message
//     where:
//       ingressName | message
//       'db-blue'   | 'Cannot find ingress db-blue'
//       'db'        | 'Cannot find ingress db'




//   }

//   def "exceptions" () {
//     given:
//       Environment bg = Environment.readFromString asset(origAsset)
//       manager = new KubeEnvironmentManager(client, 'project', bg)
//       manager.manage()

//     when:
//       Environment bgModified = Environment.readFromString asset(modAsset)
//       manager = new KubeEnvironmentManager(client, 'project', bgModified)
//       manager.manage()
//       def deployment = client.fetch "deployment", deploymentName

//     then:
//       def ex = thrown(ResourceNotFoundException)
//       ex.message == message

//     where:
//       origAsset                   | modAsset                    | deploymentName      | message
//       'bg-noactive-original.yaml' | 'bg-noactive-modified.yaml' | 'test-service-blue' | "Cannot find deployment test-service-blue"

//   }

//   private String asset(String filename) {
//     new File("${assetDir}/${filename}").text
//   }

// }