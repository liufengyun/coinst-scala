package bacala.core

/**
  * Abstract representation of the dependency closure of a group of packages
  */
abstract class Repository {
  type PackageT <: Package
  type ConflictT = (PackageT, PackageT)

  /** Returns the root package for the repository
    */
  def root: PackageT

  /** Returns the packages that p depends on directly
    */
  def apply(p: PackageT): Iterable[Iterable[PackageT]]

  /** Returns all packages in the repository
    */
  def packages: Iterable[PackageT]

  /** Returns all primitive conflicts in the repository
    */
  def conflicts: Iterable[Iterable[PackageT]]
}
