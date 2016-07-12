package com.pearson.deployment.job

import spock.lang.*
import spock.lang.MockingApi.*
import groovy.mock.interceptor.MockFor
import org.mockito.*
import org.mockito.Mockito
import org.junit.Rule
import org.junit.rules.TemporaryFolder

import hudson.model.*
import hudson.FilePath

import com.pearson.deployment.kubernetes.*
import com.pearson.deployment.config.*
import com.pearson.deployment.config.bitesize.*


class ServiceManageSpec extends Specification {
    def dummyFile = new MockFor(File)
    @Rule final TemporaryFolder configDir = new TemporaryFolder()

    ServiceManage manager
    KubeAPI client

    def setup() {
      def envConfig = """
        project: sample
        environments:
        - name: development
          namespace: sample-app-dev
          deployment:
            method: rolling-rolling
            timeout: 3000
          services:
            - name: myservice
              ssl: true
              external_url: www.google.com 
      """

      AbstractBuild build = Mockito.mock(AbstractBuild.class)
      BuildListener listener = Mockito.mock(BuildListener.class)
      Mockito.when(listener.getLogger()).thenReturn(System.out)

      client = new FakeKubeWrapper('sample-app-dev')

      manager = new ServiceManage(build, listener, 'environments.bitesize')
      manager.setCloudClient(FakeKubeWrapper.class)
      manager.config = EnvironmentsBitesize.readConfigFromString(envConfig)
    }

    def "Basic service manage" () {
      when:
      manager.run()
      then:
      manager.project() == "sample"
    }

    def "Service change settings" () {
      given: "port is changed in environments.bitesize"
      def c = """
        project: sample
        environments:
        - name: development
          namespace: sample-app-dev
          deployment:
            method: rolling-rolling
            timeout: 3000
          services:
            - name: myservice
              ssl: false
              external_url: www.google.co.uk
              port: 81
      """
      manager.run()
      manager.config = EnvironmentsBitesize.readConfigFromString(c)

      when: "service-manage runs"
      manager.run()
      then: "service port is updated"
      def environmentManager = manager.getEnvironmentManager('sample-app-dev')
      KubeServiceManager m = environmentManager.getService('myservice')
      def h = m.service.getHandler('myservice')
      h.svc.port == 81
    }

}

