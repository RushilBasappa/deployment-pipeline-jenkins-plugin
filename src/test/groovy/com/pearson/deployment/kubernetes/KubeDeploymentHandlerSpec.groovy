package com.pearson.deployment.kubernetes

import spock.lang.*
import groovy.mock.interceptor.MockFor
import org.junit.Rule
import org.junit.rules.TemporaryFolder

import org.yaml.snakeyaml.Yaml

import com.pearson.deployment.config.bitesize.*

class KubeDeploymentHandlerSpec extends Specification {

    KubeDeploymentHandler handler

    def setup() {
      def yaml = new Yaml()
      def config = """
apiVersion: extensions/v1beta1
kind: Deployment
metadata:
  annotations:
  labels:
    application: sample-app
    creator: pipeline
    name: test
    version: 1.1.2
  name: test
  namespace: sample-app-dev
spec:
  replicas: 1
  selector:
    matchLabels:
      name: test
  template:
    metadata:
      labels:
        creator: pipeline
        name: test
    spec:
      containers:
      - env:
        - name: NODE_ENV
          value: production
        - name: SOMETHING
          value: new_value
        - name: BOO
          value: ok
        - name: tos_url
          value: nononono
        image: bitesize-registry.default.svc.cluster.local:5000/example/sample-app:1.1.2
        imagePullPolicy: Always
        name: test
        ports:
        - containerPort: 80
          protocol: TCP
        """
        def resource = yaml.load(config)
        handler = new KubeDeploymentHandler(resource, System.out)
    }

    def "deploy specific deployment version" () {
      when:
      handler.create()
      def spec = handler.client.fetch('test')
      def created = new KubeDeploymentHandler(spec, System.out) 
      then:
      created.svc.version == '1.1.2'
      // s = KubeWrapper.fetch()
      // s.version == 1.1.2

    }

    def "upgrade specific deployment version" () {
      given:
      handler.svc.version = '1.1.3'
      when:
      handler.update()
      def spec = handler.client.fetch('test')
      def updated = new KubeDeploymentHandler(spec, System.out)
      then:
      updated.svc.version == '1.1.3'
    }

    def "leave deployment intact with null version" () {

    }
    
}