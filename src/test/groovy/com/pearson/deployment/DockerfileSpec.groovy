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
      String expected = new File('src/test/resources/Dockerfile.generated').text
      
    then:
      docker.contents() == expected
  }
}