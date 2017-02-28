@Grab(group='com.github.zafarkhaja', module='java-semver', version='0.9.0')

import com.github.zafarkhaja.semver.Version;

Version v = Version.valueOf("1.2.2");
boolean result = v.satisfies("<1.3.0"); 

println "Result: ${result} ${v}"
