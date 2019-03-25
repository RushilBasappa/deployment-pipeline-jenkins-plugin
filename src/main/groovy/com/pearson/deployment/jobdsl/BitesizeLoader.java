package com.pearson.deployment.jobdsl;


import com.pearson.deployment.config.bitesize.ApplicationBitesize;
import com.pearson.deployment.config.bitesize.BuildBitesize;
import com.pearson.deployment.config.bitesize.EnvironmentsBitesize;
import hudson.Extension;
import javaposse.jobdsl.plugin.ContextExtensionPoint;

@Extension
public class BitesizeLoader extends ContextExtensionPoint {


    public static ApplicationBitesize LoadApplications(String applicationPath) {

        ApplicationBitesize applicationBitesize = ApplicationBitesize.readConfigFromString(applicationPath);

        return applicationBitesize;
    }

    public static EnvironmentsBitesize LoadEnvironments(String environmentPath) {

        EnvironmentsBitesize environmentsBitesize = EnvironmentsBitesize.readConfigFromString(environmentPath);

        return environmentsBitesize;
    }

    public static BuildBitesize LoadBuilds(String environmentPath) {
        BuildBitesize buildBitesize = BuildBitesize.readConfigFromString(environmentPath);
        return buildBitesize;
    }


}
