libraryDependencies += "com.github.xuwei-k" %% "scala-version-from-sbt-version" % "0.1.0"

addSbtPlugin("ch.epfl.scala" % "sbt-scalafix" % "0.14.6")

addSbtPlugin("com.github.sbt" % "sbt-pgp" % "2.3.1")

addSbtPlugin("org.scalameta" % "sbt-scalafmt" % "2.6.0")

addSbtPlugin("com.github.sbt" % "sbt-release" % "1.4.0")

Compile / sources ++= {
  // dogfooding
  Seq("scala", "scala-3").flatMap { dir =>
    val xs = ((baseDirectory.value.getParentFile / "scalafix-check/src/main" / dir) ** "*.scala").get()
    assert(xs.nonEmpty)
    xs
  }
}
