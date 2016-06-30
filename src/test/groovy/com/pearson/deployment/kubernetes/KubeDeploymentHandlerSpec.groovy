package com.pearson.deployment.kubernetes

import spock.lang.*
import groovy.mock.interceptor.MockFor
import org.junit.Rule
import org.junit.rules.TemporaryFolder

class KubeDeploymentHandlerSpec extends Specification {

    KubeDeploymentHandler handler

    def setup() {
        def config = """
        apiVersion: extensions/v1beta1
kind: Deployment
metadata:
  annotations:
  labels:
    application: sample-app
    creator: pipeline
    name: test-service
    version: 1.1.2
  name: test-service
  namespace: sample-app-dev
spec:
  replicas: 1
  selector:
    matchLabels:
      name: test-service
  template:
    metadata:
      labels:
        creator: pipeline
        name: test-service
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
        name: test-service
        ports:
        - containerPort: 80
          protocol: TCP
        """
        handler = new KubeDeploymentHandler()
    }

    def "deploy specific deployment version" () {

    }

    def "upgrade specific deployment version" () {

    }

    def "leave deployment intact with null version" () {

    }
    
}