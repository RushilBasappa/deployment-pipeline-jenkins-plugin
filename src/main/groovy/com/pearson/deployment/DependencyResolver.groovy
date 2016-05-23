package com.pearson.deployment

import com.pearson.deployment.syspkg.*

class DependencyResolver {
  LinkedHashMap dependency

  DependencyResolver(LinkedHashMap dependency) {
    this.dependency = dependency
  }

  def resolve() {
    switch(this.dependency.type) {
      case 'debian-package':
        def deb = new Deb(this.dependency)
        deb.resolve()
        break
      case 'gem-package':
        def gem = new Gem(this.dependency)
        gem.resolve()
        break
    }
  }
}
