val nameVal = "pac4j-authorizer"
name := nameVal

val versionVal = "v0.1.0"
version := versionVal

val scalaVersionVal = "2.11.12"

lazy val testScalafmt = taskKey[Unit]("testScalafmt")

lazy val commonSettings = Seq(
  version := versionVal,
  scalaVersion := scalaVersionVal,
  resolvers += DefaultMavenRepository,
  libraryDependencies ++= Seq(
    "io.buji" % "buji-pac4j" % "4.0.0",
    "org.pac4j" % "pac4j-oauth" % "3.2.0" % "runtime",
    "org.apache.shiro" % "shiro-web" % "1.4.0",
    "org.apache.shiro" % "shiro-core" % "1.4.0",
  ),
  // Disable parallel test execution to avoid SparkSession conflicts
  parallelExecution in Test := false
)

def assemblySettings = Seq(
  assemblyMergeStrategy in assembly := {
    case PathList("org", "apache", xs @ _*) => MergeStrategy.last
    case PathList("META-INF", xs @ _*)      => MergeStrategy.discard
    case x if x.endsWith("io.netty.versions.properties") =>
      MergeStrategy.discard
    case x => MergeStrategy.first
  },
  assemblyJarName in assembly := f"${nameVal}-${versionVal}.jar",
)

lazy val root = (project in file(".")).settings(
  commonSettings,
  assemblySettings,
  libraryDependencies ++= Seq()
)
