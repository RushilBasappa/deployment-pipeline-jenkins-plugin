package com.pearson.deployment

class DependencyResolver {
  LinkedHashMap dependency

  DependencyResolver(LinkedHashMap dependency) {
    this.dependency = dependency
  }

  def resolve() {
    if (this.dependency.type == 'debian-package') {
      resolveDebianPackage()
    } else if ( this.dependency.type == 'gem-package') {
      resolveGemPackage()
    }
  }

  private def resolveDebianPackage() {
    def cmd = ['sudo','apt-get','install', '-q','-y', dependency.package]
    if (dependency.version) {
      cmd = [
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
    println exe(cmd)
  }

  def exe(cmd) {
    Process command
    command = cmd.execute()
    command.waitFor()
    def errOutput = command.err.text
    if (errOutput) {
      println("${AnsiColors.red}${errOutput}${AnsiColors.reset}")
      throw new Exception("Error executing")
    }
    return command.text
  }
}
