package com.pearson.deployment.config.bitesize

class BuildRepository implements Serializable {
  String git
  String branch = "refs/heads/master"
  String credentials
} 