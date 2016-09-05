package com.pearson.deployment

import spock.lang.*

import com.pearson.deployment.config.bitesize.ApplicationBitesize
import com.pearson.deployment.config.bitesize.Application

class DockerfileSpec extends Specification {
  
  ApplicationBitesize cfg

  def setup() {
    String e = new File("src/test/resources/config/application.bitesize").text
    cfg = ApplicationBitesize.readConfigFromString(e)
  }


  def "Generated dockerfile should be valid" () {
    given:
      Application app = cfg.applications.first()
    when:
      Dockerfile docker = new Dockerfile(app)
    then:
      docker.contents() == """FROM bitesize-registry.default.svc.cluster.local:5000/baseimages/nginx
MAINTAINER Bitesize Project <bitesize-techops@pearson.com>
RUN echo 'deb http://apt/ bitesize main' > /etc/apt/sources.list.d/bitesize.list
RUN apt-get -q update && apt-get install -y --force-yes  static-content=1.1.2-*  different-dir=1.1.1-*  && rm -rf /var/cache/apt


ENTRYPOINT ["nginx","-g","daemon off;"]
"""
  }
}