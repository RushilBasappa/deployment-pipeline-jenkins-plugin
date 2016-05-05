package com.pearson.deployment

class DependencyResolver {
  LinkedHashMap dependency

  DependencyResolver(LinkedHashMap dependency) {
    this.dependency = dependency
  }

  def resolve() {
    if (this.dependency.type == 'debian-package') {
      if (this.dependency.location != null ) {
        installDebianPackage(this.dependency.location)
      }

      if (this.dependency.repository != null ) {
        if (this.dependency.repository_key != null) {
          addDebianRepositoryKey(this.dependency.repository_key)
        }
          addDebianRepository(this.dependency.repository)
      }
      resolveDebianPackage()
    } else if ( this.dependency.type == 'gem-package') {
      resolveGemPackage()
    }
  }

  private def installDebianPackage(def pkg) {
    String tmpdir  = System.getProperty("java.io.tmpdir")
    String debfile = (pkg =~ /.*\/(.*\.deb)/)[0][1]
    File   debpath = new File("${tmpdir}/${debfile}")

    if ( ! debpath.exists() ) {
      exe(
        [
        'curl', '-k', '-o', "${tmpdir}/${debfile}", '-s', '-L', pkg
        ]
        )
      exe(['sudo', 'dpkg', '-i', "${tmpdir}/${debfile}"])
    }

  }

  private def addDebianRepository(def repo) {
    println "Adding repository ${repo}"
    def retval
    if (repo =~ /ppa:.*/) {
      retval = exe(['sudo','add-apt-repository', repo])
    } else  if (repo =~ /deb .*/){
      // this means we have the whole deb xxx version main line
      retval = exe(['sudo','add-apt-repository', repo])
    } else {
      // determine ubuntu version
      // add deb xxxxx version main
      def version = (exe(['lsb_release', '-c']) =~ /Codename:\s+(.*)/)[0][1]
      retval = exe(['sudo','add-apt-repository', "deb ${repo} ${version} main"])
    }
    def r = exe(['sudo','apt-get','update'])
    
    return "${retval}${r}"
  }

  private def addDebianRepositoryKey(key) {
    exe(
      [
      'sudo',
      'apt-key',
      'adv',
      '--keyserver', 'keyserver.ubuntu.com',
      '--recv-keys', key
      ])
  }

  private def resolveDebianPackage() {
    def cmd = ['sudo','apt-get','install', '-q','-y', dependency.package]
    if (dependency.version) {
      cmd = [
        'sudo',
        'apt-get',
        'update',
        '&&',
        'sudo',
        'apt-get',
        'install',
        '-q',
        '-y',
        "${dependency.package}=${dependency.version}"
      ]
    }
    try {
      return exe(cmd)
    } catch(all) {
      return "${AnsiColors.red}Failed to install dependency ${dependency.package}${AnsiColors.reset}"
    }
  }

  private def resolveGemPackage() {
    def cmd = ['sudo', 'gem', 'install', dependency.package]
    if (dependency.version) {
      cmd = ['sudo','gem','install',dependency.package,'-v', dependency.version]
    }
    return exe(cmd)
  }

  def exe(cmd) {
    Process command
    command = cmd.execute()
    command.waitFor()
    def errOutput = command.err.text

    if (errOutput) {
      errOutput = "${AnsiColors.red}${errOutput}${AnsiColors.reset}"
    }

    if (command.exitValue()) {
      println errOutput
      throw new Exception("Error executing ${cmd}: ${errOutput}")
    }

    def txtOutput = "${AnsiColors.green}${command.text}${AnsiColors.reset}"
    return "${errOutput}${txtOutput}"
  }
}
