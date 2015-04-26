package bacala.maven

import bacala.core._
import bacala.util.{Worker, DependencyTree}
import bacala.alg.SatSolver

object MavenDependencyManager {
  type PackageT = MavenPackage
  type TreeT = Tree[PackageT, DependencyEdge[PackageT, MavenDependency]]

  var repo: MavenRepository with DependencyTree = null

  def resolve: Either[Set[PackageT], TreeT] = {
    new SatSolver(repo).solve
  }

  def createRepo(spec: String) = {
    val pom = MavenPomParser(spec, Workers.chainPomFetchers(Workers.DefaultPomFetcher))
    repo = new MavenRepository(pom) with DependencyTree {
      override def makePomResolver(resolvers: Iterable[MavenResolver]) = {
        val fetcher = Workers.chainPomFetchers(Workers.DefaultPomFetcher)(resolvers)
        Workers.PomFileResolverCache or Workers.createPomResolver(fetcher)
      }

      override def makeMetaResolver(resolvers: Iterable[MavenResolver]) = {
        val fetcher = Workers.chainMetaFetchers(Workers.DefaultMetaFetcher)(resolvers)
        Workers.MetaFileResolverCache or Workers.createMetaResolver(fetcher)
      }
    }

    repo.construct(Scope.COMPILE)
  }

  def printUsage = {
    println(
      """*****************************\n""" +
      """Usage: sbt "run filename.xml"\n""" +
      """*****************************\n"""
    )
  }

  def printTree(tree: TreeT, level: Int = 0): Unit = {
    def tip = "  " * level + (if (level == 0) "" else "~>")
    tree match {
      case Node(pkg, children) =>
        children.foreach { case (edge, child) =>
          edge match {
            case InfectedEdge(dep, to) =>
              println(tip +  pkg + " dependency " + dep + " resolved to:" + to)
              printTree(child, level+1) // depth-first
            case ConflictEdge(dep, conflicts) =>
              println(tip +  pkg + " dependency " + dep + " leads to conflict")
            case MissingEdge(dep) =>
              println(tip +  pkg + " dependency " + dep + " can't be resolved")
            case HealthyEdge(dep, to) =>
              println(tip +  pkg + " dependency " + dep + " resolved to:" + to)
              printTree(child, level+1) // depth-first
          }
        }
      case Leaf =>
    }
  }

  def main(args: Array[String]) = {
    if (args.length != 1) {
      printUsage
    } else {
      val source = io.Source.fromFile(args(0))
      val content = source.mkString
      source.close()

      createRepo(content)

      println("\n\n######## resolution result #########")
      resolve match {
        case Left(set) => printTree(repo.buildTree(set))
        case Right(tree) => printTree(tree)
      }
    }
  }
}
