# Kubernetes CI/CD Pipeline

A custom Jenkins pipeline tool built for Kubernetes.

Jenkins uses a custom workflow to build and deploy applications. The whole CI/CD pipeline is built using just three manifest files, which have very distinctive roles in the build process. First, let’s define some terms to understand what exactly they mean in our context:

| Term | Definition |
| --- | :--- |
| *Component* | Individual code repository necessary to create an Application. Your application will have one or more internal components. |
| *Application*      | Artifacts as a single Artifact necessary to create a running instance (a collection of components). Application includes the full stack required to run an instance. |
| *Build* | Process that outputs artifacts (Debian Package, Docker Image, etc…) |
| *Job* | An instance (success or fail) of a Build |
| *Build Definition* | What actions need to be executed to generate the output artifact. |
| *Job Definition* | Specifics related to the Job, generally Version Number, Tags, .... |
| *Build Dependency* | An tool or utility necessary to create a Component Artifact. |
| *Service* | Instance (or multiple grouped instances in HA mode) of  running application. |
| *Environment* | Collection of services, grouped together to represent a fully working application stack. |



|  Term 	|   Definition	|
|---	|---	|
|   	|   	|
|   	|   	|
|   	|   	|
|   	|   	|
|   	|   	|
|   	|   	|
|   	|   	|
|   	|   	|
|   	|   	|
|   	|   	|
|   	|   	|
|   	|   	|
|   	|   	|
|   	|   	|
