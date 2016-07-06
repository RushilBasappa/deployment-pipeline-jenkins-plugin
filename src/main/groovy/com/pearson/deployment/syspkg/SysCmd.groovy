package com.pearson.deployment.syspkg
import com.pearson.deployment.*
import com.pearson.deployment.helpers.*

class SysCmd {
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
