package com.pearson.deployment.config.bitesize

import com.pearson.deployment.validation.*


class SystemPackage implements Serializable {
    @ValidString(regexp='[a-z:\\.\\d\\-]*', message='field "name" has invalid value')
    String name

    @ValidString(regexp='[a-z:\\.\\d\\-]*', message='field "version" has invalid value')
    String version = null

    @ValidString(regexp='[a-z:\\.\\d\\-]*', message='field "type" has invalid value')
    String type
}
