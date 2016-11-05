# Kubernetes Continuous Deployment Pipeline

An open source Jenkins pipeline tool purpose built for Kubernetes containers.

Our latest images can be found [here](https://hub.docker.com/r/pearsontechnology/deployment-pipeline-jenkins-plugin/)

| Features |
| :--- |
| YAML config |
| Common build tools are a dependency |
| Build Once, Run anywhere |
| Blue/Green deploys |
| Jenkins as Cattle |
| Pipeline as code |
| Continuous or Manual |
| Versioning |
| Branching |
| Dependency management |
| Docker Image management |

<br>

Get started with the [Quickstart Sample App](quickstart.md).<br>
Or get into the weeds with [Project Details](#details).<br>

[environments.bitesize](#environmentsbitesize)
* [project name](#projectname)<br>
* [environments](#environments)<br>
  * [name](#environmentname)
  * [deployment method](#deploymentmethod)<br>
  * [services](#services)<br>
  * [tests](#tests)<br>
  * [health checks](#healthchecks)<br>

[application.bitesize](#applicationbitesize)
* [project name](#projectname)<br>
* [applications](#applications)<br>
  * [name](#applicationname)<br>
  * [runtime](#applicationruntime)<br>
  * [version](#applicationversion)<br>
  * [dependencies](#applicatondependencies)<br>
  * [command](#applicationcommand)<br>

[build.bitesize](#buildbitesize)
* [project name](#projectname)<br>
* [components](#components)<br>
  * [name](#componentname)
  * [os](#os)<br>
  * [build dependencies](#builddependencies)<br>
  * [repository](#buildrepo)<br>
  * [environment vars for build](#envvars)<br>
  * [build commands](#buildcommands)
  * [artifacts](#artifact)

## Requirements:
Our Jenkins plugin deploys all containers to Kubernetes minions that have a label of

```
role=minion
```
This can be added to your workers/minions with
```
kubectl label nodes <node_name> role=minion
```

<a id="details"></a>
## Project Details

<br>
<br>
First, let’s define some terms to understand what exactly they mean in context:

| Term | Definition |
| --- | :--- |
| *Component* | Individual code repository necessary to create an Application. Your application will have one or more internal components. |
| *Application*      | Artifacts as a single Artifact necessary to create a running instance (a collection of components). Application includes the full stack required to run an instance. |
| *Build* | Process that outputs artifacts (Debian Package, Docker Image, etc…) |
| *Job* | An instance (success or fail) of a Build |
| *Build Definition* | What actions need to be executed to generate the output artifact. |
| *Job Definition* | Specifics related to the Job, generally Version Number, Tags, .... |
| *Build Dependency* | A tool or utility necessary to create a Component Artifact. |
| *Service* | Instance (or multiple grouped instances in HA mode) of a running application. |
| *Environment* | Collection of services, grouped together to represent a fully working application stack. |

<br><br>

Jenkins uses a custom workflow to build and deploy applications. The whole CI/CD pipeline is built using just three manifest files, which have very distinctive roles in the build process.

-   **[build.bitesize](#buildbitesize)** - defines how to build one or more components
-   **[application.bitesize](#applicationbitesize)** - defines how to build one or more
    applications using required components and
    external dependencies
-   **[environments.bitesize](#environmentsbitesize)** - defines how to layout environments,
    which applications (services) to run in them, what tests to run
    against your applications and the method to deploy applications.
    Builds out the whole CI/CD pipeline

Your project will store these files in a git repository. It can be either your source repository, or a repository dedicated just to managing these three files. At Pearson we've found our dev teams prefer to manage these files separately from their code repositories.<br><br>

**Global Definition** - <br>
<a id="projectname"></a>
### `project`
- every .bitesize manifest file must specify the project name. This allows Jenkins to tie the various manifest files together as one complete Jenkins workflow.<br>

Ex. `project: docs`
<br><br>
<a id="environmentsbitesize"></a>
# environments.bitesize

Consists of:<br>
  * [project name](#projectname)<br>
  * [environments](#environments)<br>
    * [name](#environmentname)
    * [deployment method](#deploymentmethod)<br>
    * [services](#services)<br>
    * [tests](#tests)<br>
    * [health checks](#healthchecks)<br>


environments.bitesize contains building blocks for each environment you intend to deploy/manage. In order for Jenkins to manage multiple environments from a single Jenkins container, a standard naming convention for Kubernetes namespaces are required.<br><br>
<a id="projectname"></a>
naming convention:<br>
`<project_name>-<three_letter_env_name>`<br><br>
Ex. example-dev<br>
Ex. example-tst<br>
Ex. example-prd<br>
<br>

Here is an example of a complete environments.bitesize manifest.<br>

```
project: docs-dev
environments:
  - name: production
    namespace: docs-dev
    deployment:
      method: rolling-upgrade
    services:
      - name: docs-app
        external_url: kubecon.dev-bite.io
        port: 80 # this is the port number the application responds on in each container/instance/pod
        ssl: "true"
        replicas: 2
    tests:
      - name: docs site tests
        repository: git@github.com:pearsontechnology/kubecon_docs_tests.git
        branch: master
        commands:
          - shell: echo serviceBaseUrl=http://escrow.reg.svc.cluster.local:3004 > integration/test.properties
    health_check:
      command:
        - /bin/health_script.sh # Path to your script. Exit code 0 means success
      initial_delay: 30 # Time in seconds to wait for a fresh instance
      timeout: 60 # Time in seconds to wait before health check script times out
```
<br>
Breaking down the environments.bitesize manifest.<br>

<a id="environments"></a>
<a id="environmentname"></a>
### `name`
Every environment starts with a `name`.<br>
Within each environment we must specify the namespace in which the environment deploys to. <br>
```
- name: production
  namespace: docs-dev
```
<br>
<a id="deploymentmethod"></a>
### `deployment method`
<br>
Currently the only available deployment method is `rolling-update`.
A `mode` (optional) can also be specified with the deployment method. This is generally used if a manual deployment is desired.
```
deployment:
  method: rolling-upgrade
  mode: manual
```
<br>
<a id="services"></a>
### `services`
in the pipeline make up multiple Kubernetes Resources.
  * Kubernetes Service
  * Ingress (Optional)
  * Number of replicas in the replica set.<br>

The name for each service must match the application name for the given app in applications.bitesize.<br>
Notice we also need to specify what port(s) the containers will use for each service.<br>
Not specifying an `external_url` will result in a Kubernetes Ingress not being created.<br>
Environment vars can be specified here with `env`.<br>

**Note** : `env` is not recommended because it requires a redeploy of the app to modify the environment variable specified.

```
services:
  - name: docs-app
    external_url: kubecon.dev-bite.io
    port: 80 # this is the port number the application responds on in each container/instance/pod
    replicas: 2
    env:
    - name: MONGO_DB_1
      value: escrow
```

<a id="tests"></a>
### `tests`
Tests assume a repository will be required to pull in the tests to run.<br>
These tests can be anything so long as the correct dependencies for Jenkins to execute the tests have been installed.<br>
There is no limit on the number of tests that can be created so long as they have a unique name.<br>
As you can see below, a different `branch` can be specified for flexibility.<br>

```
tests:
  - name: docs site tests
    repository: git@github.com:pearsontechnology/kubecon_docs_tests.git
    branch: master
    commands:
      - shell: echo serviceBaseUrl=http://docs.default.svc.cluster.local:80 > integration/test.properties
```
<br>

<a id="healthchecks"></a>
### `health_check`
are directly associated to liveness probes in Kubernetes.<br>
Notice how `initial_delay` and `timeout` set by seconds.<br>

```
health_check:
  command:
    - /bin/health_script.sh # Path to your script. Exit code 0 means success
  initial_delay: 30 # Time in seconds to wait for a fresh instance
  timeout: 60 # Time in seconds to wait before health check script times out
```
<br><br>
<a id="applicationbitesize"></a>
# application.bitesize
<br>
defines how to build one or more applications using required components and external dependencies.

Consists of:<br>
  * [project name](#projectname)<br>
  * [applications](#applications)<br>
    * [name](#applicationname)<br>
    * [runtime](#applicationruntime)<br>
    * [version](#applicationversion)<br>
    * [dependencies](#applicatondependencies)<br>
    * [command](#applicationcommand)<br>

All in all, application.bitesize is relatively simple.
<a id="applications"></a>
`name` - name of the application<br>
`runtime` - is the base image. In this example we are using ubuntu with httpd installed.<br>
`version` - the version of the application.<br>
<a id="applicationcmd"></a>
`command` - the command to run the container. Correlates directly to kubernetes `cmd`.<br><br>
<a id="applicationdependencies"></a>
Within each application we specify dependencies which is key. In the example below we specify the docswebsite deb package be built before and then added on top of the `runtime` base image.<br>


<a id="applicationname"></a>
### `name`
Every environment starts with a `name`.<br>
Within each environment we must specify the namespace in which the environment deploys to. <br>
```
- name: production
  namespace: docs-dev
```

<a id="applicationruntime"></a>
### `runtime`
`runtime` is the base image we will build a new docker image from. Often this is a specific java, nodejs or web-proxy.<br>
```
runtime: ubuntu-httpdfcgi:1.3
```

<a id="applicationversion"></a>
### `version`
A `version` of the application can be specified. Make sure doublequotes ("") are used as this is interpolated as a string. <br>
```
version: "0.8.35"
```

<a id="applicationdependencies"></a>
### `dependencies`
Notice -<br>
```
dependencies:
  - name: docswebsite
    type: debian-package
    origin:
      build: docs-app
    version: 1.0
```
<br>
which correlates to the component within build.bitesize.<br><br>

<a id="applicationcommand"></a>
### `command`
A `command` of the application can be specified. Much like `cmd` within a kubernetes config. Make sure doublequotes ("") are used as this is interpolated as a string. <br>
```
command: "/var/run.sh"
```
<br>

Here is an example of a complete application.bitesize manifest.<br>

```
project: docs-dev
applications:
  - name: docs-app
    runtime: ubuntu-httpdfcgi:1.3
    version: "0.8.35"
    dependencies:
      - name: docswebsite
        type: debian-package
        origin:
          build: docs-app
        version: 1.0
    command: "/var/run.sh"
```
<br><br>




<a id="buildbitesize"></a>
# build.bitesize

Consists of:
* [project name](#projectname)<br>
* [components](#components)<br>
  * [name](#componentname)
  * [os](#os)<br>
  * [build dependencies](#builddependencies)<br>
  * [repository](#buildrepo)<br>
  * [environment vars for build](#envvars)<br>
  * [build commands](#buildcommands)
  * [artifacts](#artifact)

Components are the basic building block of the build.bitesize manifest.<br>
Each component describes how to build a given application.<br>
There will be as many components in build.bitesize as there are applications in application.bitesize.<br><br>

Here is an example of a complete build.bitesize manifest for a single application.<br>

```
project: docs-dev
components:
  - name: docs-app
    os: linux
    dependencies:
      - type: debian-package
        package: php5
        repository: ppa:ondrej/php
      - type: debian-package
        package: libapache2-mod-php5
      - type: debian-package
        package: python2.7
      - type: debian-package
        package: python-pip
      - type: pip-package
        package: PyGithub
      - type: pip-package
        package: pyyaml
      - type: debian-package
        package: couscous
        location: https://s3.amazonaws.com/bitesize-sandbox-files/couscous.deb_1.0_amd64.deb
    repository:
      git: git@github.com:pearsontechnology/kubecon_docs.git
      branch: master
    env:
      - name: GIT_USERNAME
        value: kubecondemos@gmail.com
      - name: GIT_PASSWORD
        value: 27438ded25cfba57ba87
    build:
      - shell: cat /dev/null > couscous.yml
      - shell: python docsgen.py
      - shell: couscous generate
      - shell: mkdir -p var/www/html
      - shell: cp run.sh var/
      - shell: cp -a .couscous/generated/* var/www/html
      - shell: fpm -s dir -n docswebsite --iteration $(date "+%Y%m%d%H%M%S") -t deb var
    artifacts:
      - location: "*.deb"
```
<br>

Breaking down the build.bitesize manifest.<br>

<a id="components"></a>
### `components`

As with everything we specify a name for each component which correlates directly to the application name in application.bitesize. <br>
But we also specify an `os`. Currently the only available `os` is linux but we intend to allow for the MicroContainer Framework by MicroSoft when supported.<br>

```
components:
  - name: docs-app
    os: linux
```
<br>
<a id="builddependencies"></a>
### `dependencies`

For each component we need to specify the build dependencies. This ensures Jenkins has the necessary packages installed in order to build the properly.
<br>
There are three package types currently supported.<br>
  * debian-package
  * pip-package
  * gem-package
<br>
Notice the first dependency is install from an alternate ppa `repository`.<br>
And the last line has a `location` called out for a package that exists in an S3 repository.<br>
If nothing is specified Jenkins will get packages from default repos.<br>

```
dependencies:
  - type: debian-package
    package: php5
    repository: ppa:ondrej/php
  - type: debian-package
    package: libapache2-mod-php5
  - type: debian-package
    package: python2.7
  - type: debian-package
    package: python-pip
  - type: pip-package
    package: PyGithub
  - type: pip-package
    package: pyyaml
  - type: debian-package
    package: couscous
    location: https://s3.amazonaws.com/bitesize-sandbox-files/couscous.deb_1.0_amd64.deb
```

<br>
<a id="buildrepo"></a>
### `repository`

The repository block tells Jenkins where to pull all application code from.<br>
Notice a specific branch can be specified.<br>

```
repository:
  git: git@github.com:pearsontechnology/kubecon_docs.git
  branch: master
```
<br>
<a id="envvars"></a>
### `environment variables`

environment variables can be specified for Jenkins and its slaves to take advantage of. One example is below.
Again its not best practice to use these but for demo purposes this is what it looks like below.<br>

```
env:
  - name: GIT_USERNAME
    value: kubecondemos@gmail.com
  - name: GIT_PASSWORD
    value: 218ba57a341687
```
<br>
<a id="buildcommands"></a>
### `build commands`
<br>

These are commands for each component that are executed as though in a linux shell. This sequence of commands will result in one or more build artifacts.

```
build:
  - shell: cat /dev/null > couscous.yml
  - shell: python docsgen.py
  - shell: couscous generate
  - shell: mkdir -p var/www/html
  - shell: cp run.sh var/
  - shell: cp -a .couscous/generated/* var/www/html
  - shell: fpm -s dir -n docswebsite --iteration $(date "+%Y%m%d%H%M%S") -t deb var
```
<br>
### `artifacts`
<br>
Currently all build artifacts are debian packages. These artifacts will be added to the base image and then a docker image will be created and versioned with a datetimestamp.

```
artifacts:
  - location: "*.deb"
```
<br>
