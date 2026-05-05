import sbtrelease.ReleasePlugin.autoImport.ReleaseTransformations.*

def sbt1 = "1.12.11"
def sbt2 = "2.0.0-RC12"

val commonSettings = Def.settings(
  organization := "com.github.xuwei-k",
  publishTo := (if (isSnapshot.value) None else localStaging.value),
  scalacOptions ++= {
    scalaBinaryVersion.value match {
      case "2.12" =>
        Seq(
          "-release:8",
          "-language:higherKinds",
          "-Xsource:3",
        )
      case _ =>
        Nil
    }
  },
  scalacOptions ++= Seq(
    "-deprecation",
    "-feature",
  ),
  pomExtra := (
    <developers>
      <developer>
        <id>xuwei-k</id>
        <name>Kenji Yoshida</name>
        <url>https://github.com/xuwei-k</url>
      </developer>
    </developers>
    <scm>
      <url>git@github.com:xuwei-k/scalafix-check.git</url>
      <connection>scm:git:git@github.com:xuwei-k/scalafix-check.git</connection>
    </scm>
  ),
  description := "additional scalafix tasks",
  organization := "com.github.xuwei-k",
  homepage := Some(url("https://github.com/xuwei-k/scalafix-check")),
  licenses := List(
    "MIT License" -> url("https://opensource.org/licenses/mit-license")
  ),
)

val root = rootProject.autoAggregate.settings(
  releaseProcess := Seq[ReleaseStep](
    checkSnapshotDependencies,
    inquireVersions,
    runClean,
    setReleaseVersion,
    commitReleaseVersion,
    tagRelease,
    releaseStepCommandAndRemaining("publishSigned"),
    releaseStepCommandAndRemaining("sonaRelease"),
    setNextVersion,
    commitNextVersion,
    pushChanges
  ),
  commonSettings,
  publish / skip := true
)

val `scalafix-check` = projectMatrix
  .in(file("scalafix-check"))
  .enablePlugins(SbtPlugin)
  .defaultAxes(VirtualAxis.jvm)
  .jvmPlatform(
    Seq(sbt1, sbt2).map(
      scala_version_from_sbt_version.ScalaVersionFromSbtVersion.apply
    )
  )
  .settings(
    commonSettings,
    sbtTestDirectory := sourceDirectory.value.getParentFile / "test",
    scriptedLaunchOpts ++= Seq[(String, String)](
      "plugin.version" -> version.value,
      "scalafix.version" -> _root_.scalafix.sbt.BuildInfo.scalafixVersion
    ).map { case (k, v) =>
      s"-D${k}=${v}"
    },
    scriptedBufferLog := false,
    pluginCrossBuild / sbtVersion := {
      scalaBinaryVersion.value match {
        case "2.12" =>
          sbt1
        case _ =>
          sbt2
      }
    },
  )

ThisBuild / scalafixDependencies += "com.github.xuwei-k" %% "scalafix-rules" % "0.6.24"
