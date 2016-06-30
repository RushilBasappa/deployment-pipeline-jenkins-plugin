package com.pearson.deployment.config.bitesize

import org.yaml.snakeyaml.Yaml

import java.io.InputStream
import java.io.OutputStream


class EnvironmentsBitesize implements Serializable {
    public EnvironmentList config
    private OutputStream log

    EnvironmentsBitesize( def stream, OutputStream log=System.out) {
        this.log = log
        Yaml yaml = new Yaml()
        this.config = yaml.loadAs(stream, EnvironmentList)
    }
} 