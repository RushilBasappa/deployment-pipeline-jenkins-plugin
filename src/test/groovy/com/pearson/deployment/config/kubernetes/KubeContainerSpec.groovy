package  com.pearson.deployment.config.kubernetes

import spock.lang.*

class KubeContainerSpec extends Specification {
    def "Compare ports not equal" () {

      when:
      def orig = new KubeContainer( ports: [

        [containerPort: 80],
        [containerPort: 81]
      ])

      def other = new KubeContainer(ports: [
        [containerPort: 80],
        [containerPort: 82],
      ])

      then:

      orig != other
    }

    def "Compare ports equal" () {
      when:
      def orig = new KubeContainer( ports: [

        [containerPort: 80],
        [containerPort: 81]
      ])


      def other = new KubeContainer( ports: [

        [containerPort: 80],
        [containerPort: 81]
      ])

      then:
      orig == other
    }


    def "Compare environment not equal" () {
      when:

      def orig = new KubeContainer( env: [
        [name: "one", value: "a"],
        [name: "two", value: "b"],
      ])

      def other = new KubeContainer( env: [
        [name: "one", value: "a"],
        [name: "two", value: "a"],
      ])

      then:
      orig != other
    }


    def "Compare environment equal" () {
      when:

      def orig = new KubeContainer( env: [
        [name: "one", value: "a"],
        [name: "two", value: "b"],
        ])

        def other = new KubeContainer( env: [
          [name: "one", value: "a"],
          [name: "two", value: "b"],
          ])

          then:

          orig == other
    }
}
