# Kubernetes Continuous Deployment Pipeline

An open source Jenkins pipeline tool purpose built for Kubernetes containers.

* [Quickstart Sample App](quickstart.md)
* [Project Details](#details)


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

Jenkins uses a custom workflow to build and deploy applications. The whole CI/CD pipeline is built using just three manifest files, which have very distinctive roles in the build process.

-   **build.bitesize** - defines how to build one or more components
-   **application.bitesize** - defines how to build one or more
    applications using required components and
    external dependencies
-   **environments.bitesize** - defines how to layout environments,
    which applications (services) to run in them, what tests to run
    against your applications and the method to deploy applications.
    Builds out the whole CI/CD pipeline


Your project will store these files in a git repository. It can be either your source repository, or a repository dedicated just to managing these three files. At Pearson we've found our dev teams prefer to manage these files separately from their code repositories.<br><br>

**Global Definition** - <br>
`project` - every .bitesize config file must specify the project name. This allows Jenkins to tie the various config files together as one complete Jenkins workflow.<br>

Ex. `project: docs`

## environments.bitesize

environments.bitesize contains building blocks for each environment you intend to deploy/manage. In order for Jenkins to manage multiple environments from a single Jenkins container, a standard naming convention for Kubernetes Namespaces are required.

naming convention:<br>
`<project_name>-<three_letter_env_name>`<br>
Ex. example-dev<br>
Ex. example-tst<br>
Ex. example-prd<br>
<br>







## application.bitesize



## build.bitesize






<br>
First, let’s define some terms to understand what exactly they mean in our context:

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
