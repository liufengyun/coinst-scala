package bacala.maven

/*
 * Parse POM XML file into constraint objects
 *
 * Reference: [1] http://maven.apache.org/pom.html
 *            [2] http://docs.codehaus.org/display/MAVEN/Dependency+Mediation+and+Conflict+Resolution
 *
 * Standard Meaning of Version Specification
 *
 *    (,1.0]          x <= 1.0
 *    1.0             "Soft" requirement on 1.0 (>=1.0)
 *    [1.0]           Hard requirement on 1.0
 *    [1.2,1.3]       1.2 <= x <= 1.3
 *    [1.0,2.0)       1.0 <= x < 2.0
 *    [1.5,)          x >= 1.5
 *    (,1.0], [1.2,)  x <= 1.0 or x >= 1.2. Multiple sets are comma-separated
 *    (,1.1),(1.1,)   This excludes 1.1 if it is known not to work in combination with this library
 *
 * TODO
 *
 *  1. no version specification
 *  2. a range of versions
 *  3. exclusions
 *  4. properties
 *  5. parent/child projects
 *  6. multi-module/agregating projects
 */

import scala.xml.XML
import scala.xml.Node

object MavenPomParser extends (String => Set[Set[MavenPackage]]) {
  override def apply(spec: String) = {
    val node = XML.loadString(spec)

    val constraints = for (dep <- (node \ "dependencies" \ "dependency")) yield parseDependency(dep)
    constraints.toSet
  }

  private def parseDependency(dep: Node) = {
    Set(MavenPackage((dep \ "groupId")(0).text, (dep \ "artifactId")(0).text, (dep \ "version")(0).text))
  }
}
