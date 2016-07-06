package com.pearson.deployment.syspkg
import com.pearson.deployment.*
import com.pearson.deployment.helpers.*

class Gem extends SysCmd {

  LinkedHashMap cfg

  Gem(LinkedHashMap cfg) {
    this.cfg = cfg
  }

  def resolve() {
    if (!cfg.package) { return false }
    def cmd = ['sudo', 'gem', 'install', cfg.package ]
    if (cfg.version) {
      cmd = ['sudo','gem','install', cfg.package, '-v', cfg.version]
    }
    return exe(cmd)
  }
}
