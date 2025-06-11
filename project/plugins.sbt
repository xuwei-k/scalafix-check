addSbtPlugin("ch.epfl.scala" % "sbt-scalafix" % "0.14.3")

addSbtPlugin("com.eed3si9n" % "sbt-projectmatrix" % "0.11.0")

addSbtPlugin("com.github.sbt" % "sbt-pgp" % "2.3.1")

addSbtPlugin("org.scalameta" % "sbt-scalafmt" % "2.5.4")

addSbtPlugin("com.github.sbt" % "sbt-release" % "1.4.0")

Compile / sources ++= {
  // dogfooding
  val xs = ((baseDirectory.value.getParentFile / "scalafix-check/src/main/scala") ** "*.scala").get
  assert(xs.nonEmpty)
  xs
}
