Setup
=====

Instal Chrome driver for your system:

https://github.com/SeleniumHQ/selenium/wiki/ChromeDriver

Next, you will need to setup the following environment variables that are used
during the tests:
  * GIT_PUBLIC_KEY
  * GIT_PRIVATE_KEY
  * JENKINS_IMAGE
  * JENKINS_ADMIN_USER (Optional. Default: admin)
  * JENKINS_ADMIN_PASSWORD (Optional. Default: pass)
  * JENKINS_URL (Optional. Default: http://jenkins.sample-app.io/)

Point jenkins.sample-app.io DNS record (or any other record if it is overriden)
to your active kubernetes cluster.

Run integration tests:
```
% gradle integrationTest
```
