package bacala.maven

/**
  * Fetch the POM file of a package from repository
  *
  * TODO:
  *  - cache the same query in file system for a period
  *  - multiple repos;
  *  - retries logic;
  */

import scalaj.http._


object MavenFetcher extends (MavenPackage => Option[String]) {
  val MavenRepoBase = "http://repo1.maven.org/maven2"

  override def apply(p: MavenPackage) = getResponse(pomURL(p))

  def getMetaData(artifact: MavenArtifact) = getResponse(metaDataURL(artifact))

  def getResponse(url: String) = {
    println("Downloading " + url)
    val response = Http(url).asString

    if (response.code == 200) Some(response.body) else {
      println("Error: failed to download " + url)
      None
    }
  }

  /**
    * returns POM URL for a package
    *
    * Format: $BASE_REPO/:groupId/:artifactId/:version/:artifactId-version.pom
    *
    * e.g. http://repo1.maven.org/maven2/org/scala-lang/scala-library/2.11.6/scala-library-2.11.6.pom
    */
  def pomURL(p: MavenPackage) = {
    s"${MavenRepoBase}/${p.groupId.replace(".", "/")}/${p.artifactId}/${p.version}/${p.artifactId}-${p.version}.pom"
  }

  /**
    * returns the meta-data URL for an artifact
    *
    * Format:  $BASE_REPO/:groupId/:artifactId/maven-metadata.xml
    *
    * e.g.
    * http://repo1.maven.org/maven2/org/scala-lang/scala-library/maven-metadata.xml
    */
  def metaDataURL(artifact: MavenArtifact) = {
    s"${MavenRepoBase}/${artifact.groupId.replace(".", "/")}/${artifact.artifactId}/maven-metadata.xml"
  }
}
