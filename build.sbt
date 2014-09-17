import _root_.sbtrelease.ReleasePlugin._
import _root_.xerial.sbt.Sonatype._
import com.typesafe.sbt.SbtPgp._
import sbt.Keys._
import sbtrelease.ReleaseStateTransformations._
import sbtrelease._

name := "scala-automation-tstash-logger"

organization := "com.gu"

scalaVersion := "2.10.4"

resolvers ++= Seq(
  "Guardian GitHub Releases" at "http://guardian.github.io/maven/repo-releases",
  "Typesafe Releases" at "http://repo.typesafe.com/typesafe/releases/"
)

libraryDependencies ++= Seq(
  "ch.qos.logback" % "logback-classic" % "1.1.2",
  "com.typesafe.play" %% "play-ws" % "2.3.0",
  "org.scalatest" %% "scalatest" % "2.0" % "test",
  "com.typesafe.scala-logging" %% "scala-logging-slf4j" % "2.1.2" % "test",
  "org.seleniumhq.selenium" % "selenium-java" % "2.43.1" % "test"
)

releaseSettings

sonatypeSettings

description := "logger to send things to tstash server from scala-automation tests"

scmInfo := Some(ScmInfo(
  url("https://github.com/guardian/scala-automation-tstash-logger"),
  "scm:git:git@github.com:guardian/scala-automation-tstash-logger.git"
))

pomExtra := (
  <url>https://github.com/guardian/scala-automation-tstash-logger</url>
    <developers>
      <developer>
        <id>johnduffell</id>
        <name>John Duffell</name>
        <url>https://github.com/johnduffell</url>
      </developer>
      <developer>
        <id>istvanpamer</id>
        <name>Istvan Pamer</name>
        <url>https://github.com/istvanpamer</url>
      </developer>
    </developers>
  )

licenses := Seq("Apache V2" -> url("http://www.apache.org/licenses/LICENSE-2.0.html"))

ReleaseKeys.crossBuild := true

ReleaseKeys.releaseProcess := Seq[ReleaseStep](
  checkSnapshotDependencies,
  inquireVersions,
  runClean,// new
  runTest,
  setReleaseVersion,
  commitReleaseVersion,
  tagRelease,
  ReleaseStep( // instead of publishArtifacts
    action = state => Project.extract(state).runTask(PgpKeys.publishSigned, state)._1,
    enableCrossBuild = true
  ),
  setNextVersion,
  commitNextVersion,
  ReleaseStep(state => Project.extract(state).runTask(SonatypeKeys.sonatypeReleaseAll, state)._1),// new
  pushChanges
)
