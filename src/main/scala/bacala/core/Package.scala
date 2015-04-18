package bacala.core

/**
  * abstract representation of a library
  */

abstract class Artifact {
  def id: String // unique identifier of the library

  override def hashCode = {
    id.hashCode * 31
  }

  override def equals(other: Any) = other match {
    case artifact: Artifact =>
      this.id == artifact.id
    case _ => false
  }
}

/**
  * abstract representation of a package
  */
abstract class Package {
  def artifact: Artifact
  def version: String // version number

  override def toString = artifact + "-" + version
}

/**
  * Abstract representation of a dependency
  */
abstract class Dependency {
  def artifact: Artifact // this dependency is on which artifact
  def versionConstraint: String // version constraint
}
