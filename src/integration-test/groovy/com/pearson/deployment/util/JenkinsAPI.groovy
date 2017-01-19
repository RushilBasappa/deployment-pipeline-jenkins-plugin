package com.pearson.deployment.util

import groovyx.net.http.RESTClient


class JenkinsAPI {
  private def restUrl
  private def client

  JenkinsAPI(String url, String username, String password) {
    restUrl = url
    client = new RESTClient(restUrl)

    String encodedStr = "${username}:${password}".bytes.encodeBase64().toString()
    client.headers.'Authorization' = "Basic ${encodedStr}"
  }

  def getLastBuild(String name) {
    client.get(uri: restUrl, path: "job/${name}/lastBuild/api/json")
  }

  def waitForBuildToFinish(String name) {
    def isBuilding = true
    def status = 200
    def response

    while(isBuilding && status == 200 ) {
      response = getLastBuild(name)
      isBuilding = response.data.building
      status = response.status
      sleep 5000
    }
    response
  }

  def waitForAvailable() {
    def retries = 0
    def maxRetries = 20
    def response
    while(retries < maxRetries) {
      response = client.get(uri: restUrl, path: "/")
      if(response.status == 200 ) {
        return
      }
      sleep 5000
      retries += 1
    }
  }

  def getHomePage() {
    client.get(uri: restUrl, path: "/")
  }
  
}
