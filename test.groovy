def output= """
Client Version: version.Info{Major:"1", Minor:"4", GitVersion:"v1.4.1", GitCommit:"33cf7b9acbb2cb7c9c72a10d6636321fb180b159", GitTreeState:"clean", BuildDate:"2016-10-10T18:19:49Z", GoVersion:"go1.7.1", Compiler:"gc", Platform:"darwin/amd64"}
Server Version: version.Info{Major:"1", Minor:"4", GitVersion:"v1.4.6", GitCommit:"e569a27d02001e343cb68086bc06d47804f62af6", GitTreeState:"clean", BuildDate:"1970-01-01T00:00:00Z", GoVersion:"go1.7.1", Compiler:"gc", Platform:"linux/amd64"}
"""


def serverLine =  (output =~ /.*Server.*/)[0]


def matcher =  ( serverLine =~ /.*GitVersion:"v(.*?)",.*/)

print matcher[0][1]
