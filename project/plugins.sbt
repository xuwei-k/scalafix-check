addSbtPlugin("ch.epfl.scala" % "sbt-scalafix" % "0.14.5")

addSbtPlugin("com.eed3si9n" % "sbt-projectmatrix" % "0.11.0")

addSbtPlugin("com.github.sbt" % "sbt-pgp" % "2.3.1")

addSbtPlugin("org.scalameta" % "sbt-scalafmt" % "2.5.6")

addSbtPlugin("com.github.sbt" % "sbt-release" % "1.4.0")

Compile / sources ++= {
  // dogfooding
  Seq("scala", "scala-2").flatMap { dir =>
    val xs = ((baseDirectory.value.getParentFile / "scalafix-check/src/main" / dir) ** "*.scala").get
    assert(xs.nonEmpty)
    xs
  }
}
