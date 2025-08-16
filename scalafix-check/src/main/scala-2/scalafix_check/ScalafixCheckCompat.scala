package scalafix_check

private[scalafix_check] object ScalafixCheckCompat {
  implicit class DefOps(private val self: sbt.Def.type) extends AnyVal {
    def uncached[A](a: A): A = a
  }
}
