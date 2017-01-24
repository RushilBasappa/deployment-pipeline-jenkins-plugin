# Adding Alpine Linux support to deployment pipeline

## Short overview of build process in deployment pipeline
Our build process is broken down into three parts:
* building application component
* packaging application
* deploying application

Application deployment is OS-independent and is a wrapper around Kubernetes API, so this is not a concern of this document.

Application component is an artifact that is produced by customer defined build process, and currently we support only debian packages as an outcome of this. Build process is defined in `build.bitesize` file. 

Application then is packaged  into docker image, which is a deployable artifact for kubernetes cluster. Docker image includes application component (as described above) with addition of any other dependencies application has (e.g. nodejs, libraries, web server, etc.).  This process is defined in `application.bitesize` file.

Application deployment is out of scope for this document as it is independent of underlying customer OS or application.

### Kubernetes plugin
For interactions with Kubernetes cluster, Jenkins uses  [Jenkins Kubernetes plugin](https://github.com/jenkinsci/kubernetes-plugin).  Jenkins configuration for the plugin lives in [pearsontechnology/jenkins-bootstrap](https://github.com/pearsontechnology/jenkins-bootstrap) repository. Check [005-kubernetes-cloud.groovy](https://github.com/pearsontechnology/jenkins-bootstrap/blob/master/docker_build/init_scripts/005-kubernetes-cloud.groovy) setup script  for more information. It sets up two jenkins slave templates, more details on them below. Plugin assumes Jenkins is running in Kubernetes environment, and uses [Kubernetes service accounts](https://kubernetes.io/docs/user-guide/service-accounts/) for authentication.

### build.bitesize setup
When component build is triggered (e.g. by git change in application's repository), Jenkins launches new short-lived slave machine in Kubernetes cluster. Currently, code  responsible for build jobs can be found in jenkins-bootstrap repository, [a005_custom_dsl.groovy](https://github.com/pearsontechnology/jenkins-bootstrap/blob/master/docker_build/jobdsl/a005_custom_dsl.groovy#L52) script.

This short-lived Jenkins slave machine is labelled as  `generic`  in Kubernetes plugin, and is a basic Ubuntu 14.04 docker image with Jenkins jnlp slave installed on top of it. See [here](https://github.com/pearsontechnology/jenkins-bootstrap/tree/master/docker_build/base_images/generic) for more details on how `generic` image is built.

Once slave machine is launched, component build dependencies (from `build.bitesize` file) are installed on it. Currently we support `debian-package`, `gem-package` and `pip-package` as dependencies. You can see them defined in  `a005_custom_dsl.groovy`, hooked into deployment-pipeline-plugin via `bitesize_build_dependency` DSL definition. See [BuildDependencyBuilder.groovy](https://github.com/pearsontechnology/deployment-pipeline-jenkins-plugin/blob/master/src/main/groovy/com/pearson/deployment/builder/BuildDependencyBuilder.groovy) ) perform method and [PackageInstallerFactory.groovy](https://github.com/pearsontechnology/deployment-pipeline-jenkins-plugin/blob/master/src/main/groovy/com/pearson/deployment/syspkg/PackageInstallerFactory.groovy) for more information on how these dependencies are setup.

After all build dependencies are installed on `generic` slave, "build" block  steps are run sequentially. When build is successful, "artifact" part  from `build.bitesize` is archived (i.e. sent from "slave" Jenkins instance to the master for storage) and aptly plugin is invoked to send these archived artifacts to aptly repository. Jenkins slave instance is terminated and all intermediate build information is destroyed and not persisted between builds.

### application.bitesize setup
Application build steps are represented by  "*-docker-image" jobs in Jenkins and, currently, are built with [a005_custom_dsl.groovy](https://github.com/pearsontechnology/jenkins-bootstrap/blob/master/docker_build/jobdsl/a005_custom_dsl.groovy#L107). Script uses  [Dockerfile.groovy](https://github.com/pearsontechnology/deployment-pipeline-jenkins-plugin/blob/master/src/main/groovy/com/pearson/deployment/Dockerfile.groovy)  to generate Dockerfile, which is later on used to build application's docker image on jenkins slave with a  `dind` label (build process launches `dind` slave via kubernetes plugin using the same method as in component build). 

## Adding Alpine Linux
To successfully build Alpine based applications, the following steps are necessary:

1. **Identify that component is built on alpine.** Currently `build.bitesize` definition has an 'os' field, which is unused, as we only support one os (and specific distribution and version) - it could be reused to define custom OS. For example, default could be `linux:ubuntu:14.04` and this formatting could be used  to detect other flavours.
2. **Have alp slave template defined in kubernetes plugin settings.** We need to be able to perform builds on top of Alpine - image, similar to `generic` should be built for alpine and added to kubernetes plugin properties.
3. **Have alp package dependency defined**. We need `alp-package` installer support.  In addition, alp build image needs to be able to perform `gem-package` and `pip-package` installs.
4. **Jenkins master needs to have alp repository.** For debian packages, we use aptly publisher plugin. We need alp publisher as well.
5. **Dockerfile has to differentiate between ubuntu and alpine.** We need to build out different dockerfiles for different distributions. This logic needs to live in Dockerfile.groovy.
6. **Base images need to be built for alpine**. In addition to that, we need a clear way of naming/locating base images, to avoid confusion between Ubuntu and Alpine images (e.g. baseimage named `nginx:1.3.7` is not enough anymore).