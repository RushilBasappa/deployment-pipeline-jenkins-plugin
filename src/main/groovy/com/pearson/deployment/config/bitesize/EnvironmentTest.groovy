package com.pearson.deployment.config.bitesize

class EnvironmentTest implements Serializable {
    String name
    String repository
    String branch
    List<? extends Map<String,String>> commands
}