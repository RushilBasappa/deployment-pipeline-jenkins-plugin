// Class that manages deb packages
package com.pearson.deployment.syspkg

import com.pearson.deployment.*
import com.pearson.deployment.helpers.*

class Deb extends SysCmd {

  LinkedHashMap cfg

  Deb(LinkedHashMap cfg) {
    this.cfg = cfg

    if (cfg.location != null) {
      install(cfg.location)
    }

    if (cfg.repository_key != null) {
      addRepoKey(cfg.repository_key)
    }

    if (cfg.repository != null) {
      addRepo(cfg.repository)
    }

  }

  def addRepo(String repo) {
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

  def addRepoKey(String key) {
    exe(
      [
      'sudo',
      'apt-key',
      'adv',
      '--keyserver', 'keyserver.ubuntu.com',
      '--recv-keys', key
      ])
  }

  def install(String pkg) {
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

  def resolve() {
    def cmd = ['sudo','apt-get','install', '-q','-y', cfg.package]
    if (cfg.version) {
      cmd = [
        'sudo',
        'apt-get',
        'install',
        '-q',
        '-y',
        "${cfg.package}=${cfg.version}"
      ]
    }
    try {
      return exe(cmd)
    } catch(all) {
      return "${AnsiColors.red}Failed to install dependency ${cfg.package}${AnsiColors.reset}"
    }
  }

}
