package com.pearson.deployment.syspkg

import jenkins.util.*
import hudson.remoting.Callable


class DebInfo extends SysCmd implements Serializable {
  VirtualFile vfile
  String info

  DebInfo(VirtualFile f) {
    def filename = f.toURI().getPath()
    this.info = f.run(new GetCommandOutput("dpkg --info ${filename}" ))
  }

  public name() {
    extractInfo(/^ Package: (.*)/)
  }

  public version() {
    extractInfo(/^ Version: (.*)/)
  }

  def arch() {
    extractInfo(/^ Architecture: (.*)/)
  }

  private extractInfo(def regexp) {
    def line = info().grep(~regexp)?.first()
    def matcher = ( line =~ regexp)
    return matcher[0][1]
  }

  private info() {
    this.info.split("\\r?\\n")
  }
}
