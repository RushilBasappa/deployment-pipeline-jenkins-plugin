package com.pearson.deployment

class Env {
  static def get(String variable) {
    def e = System.getenv()
    e[variable]
  }
}
