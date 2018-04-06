name := "equoid-data-handler"

organization := "io.radanalytics"

version := "0.1.0-SNAPSHOT"

scalaVersion := "2.11.8"

crossScalaVersions := Seq("2.11.8")

val sparkVersion = "2.2.1"
val akkaVersion = "2.3.9"
val sprayVersion = "1.3.3"
val streamingAmqpVersion = "0.3.1"

licenses += ("Apache-2.0", url("http://opensource.org/licenses/Apache-2.0"))

def commonSettings = Seq(
  libraryDependencies ++= Seq(
    "org.infinispan" % "infinispan-core" % "9.1.6.Final",
    "org.infinispan" % "infinispan-client-hotrod" % "9.1.6.Final",
    ("io.radanalytics" %% "spark-streaming-amqp" % streamingAmqpVersion).exclude("com.fasterxml.jackson.core", "jackson-databind").exclude("io.netty", "netty"),
    "org.apache.spark" %% "spark-core" % sparkVersion,
    "org.apache.spark" %% "spark-sql" % sparkVersion,
    "org.apache.spark" %% "spark-streaming" % sparkVersion,
    "org.scalatest" %% "scalatest" % "3.0.4" % Test,
    "org.apache.commons" % "commons-math3" % "3.6.1" % Test,
    /*Spray*/
    "io.spray"            %%  "spray-can"     % sprayVersion,
    "io.spray"            %%  "spray-routing" % sprayVersion,
    "io.spray"            %%  "spray-testkit" % sprayVersion % Test,
    "com.typesafe.akka"   %%  "akka-actor"    % akkaVersion,
    "com.typesafe.akka"   %%  "akka-testkit"  % akkaVersion % Test,
    "org.specs2"          %%  "specs2-core"   % "2.3.11" % Test),

    scalacOptions ++= Seq("-unchecked", "-deprecation", "-feature", "-encoding", "utf8")
)

seq(commonSettings:_*)

mainClass in Compile := Some("io.radanalytics.equoid.DataHandler")

test in assembly := {}

// not sure what strategy to use for these
// see https://github.com/sbt/sbt-assembly
assemblyMergeStrategy in assembly := {
  case PathList("META-INF", "services", _*) => MergeStrategy.concat
  case PathList("META-INF", _*) => MergeStrategy.discard
  case "features.xml" => MergeStrategy.discard
 // case PathList("io", "netty", _*) => MergeStrategy.discard

  case _ => MergeStrategy.first
}

assemblyShadeRules in assembly := Seq(
//  ShadeRule.zap("META-INF/MANIFEST.MF").inLibrary("io.radanalytics" %% "spark-streaming-amqp" % streamingAmqpVersion)
  // ShadeRule.zap("scala.**").inAll,
  // ShadeRule.zap("org.slf4j.**").inAll
)

//Revolver.settings