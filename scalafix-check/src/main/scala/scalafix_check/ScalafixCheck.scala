package scalafix_check

import sbt._
import scala.jdk.CollectionConverters._
import scala.reflect.NameTransformer.MODULE_INSTANCE_NAME
import scala.reflect.NameTransformer.MODULE_SUFFIX_STRING

object ScalafixCheck extends AutoPlugin {

  override def trigger: PluginTrigger = allRequirements

  override def requires: AutoPlugin = PluginRef("scalafix.sbt.ScalafixPlugin")

  object autoImport {
    val scalafixCheckAll = taskKey[Unit]("alias `scalafixAll --check`")
    val scalafixCheckAllSyntactic = taskKey[Unit]("alias `scalafixAll --check --syntactic`")
    val scalafixAllSyntactic = taskKey[Unit]("alias `scalafixAll --syntactic`")
    val scalafixConfigRuleNamesSortCheck = taskKey[Unit]("")
  }

  import autoImport._

  private val scalafixAll = InputKey[Unit]("scalafixAll")

  override val projectSettings: Seq[Def.Setting[?]] = Def.settings(
    scalafixCheckAll := scalafixAll.toTask(" --check").value,
    scalafixCheckAllSyntactic := scalafixAll.toTask(" --check --syntactic").value,
    scalafixAllSyntactic := scalafixAll.toTask(" --syntactic").value,
  )

  override val buildSettings: Seq[Def.Setting[?]] = Def.settings(
    scalafixConfigRuleNamesSortCheck := {
      val configFile = SettingKey[Option[File]]("scalafixConfig").?.value.flatten.getOrElse(file(".scalafix.conf"))
      val log = Keys.streams.value
      if (configFile.isFile) {
        val ruleNames = com.typesafe.config.ConfigFactory.parseFile(configFile).getStringList("rules").asScala.toList
        val sorted = ruleNames.sorted
        if (ruleNames != sorted) {
          sys.error(
            Seq(
              s"please sort scalafix rule names in ${configFile.getAbsolutePath}",
              ruleNames.mkString("actual = [", " ", "]"),
              sorted.mkString("expect = [", " ", "]"),
            ).mkString("\n")
          )
        }
      } else {
        log.log(s"not found ${configFile.getCanonicalPath}")
      }
    }
  )

  private def PluginRef(className: String): AutoPlugin = {
    try {
      val name = if (className.endsWith(MODULE_SUFFIX_STRING)) className else s"$className${MODULE_SUFFIX_STRING}"
      Class.forName(name).getDeclaredField(MODULE_INSTANCE_NAME).get(null) match {
        case plugin: AutoPlugin =>
          plugin
        case _ =>
          println(s"[$name] is not an AutoPlugin object")
          DummyEmptyPlugin
      }
    } catch {
      case _: ClassNotFoundException =>
        DummyEmptyPlugin
      case e: Throwable =>
        println(e)
        DummyEmptyPlugin
    }
  }

  private object DummyEmptyPlugin extends AutoPlugin
}
