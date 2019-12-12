import mill._
import mill.scalalib._
import mill.scalalib.scalafmt.ScalafmtModule
import $file.dependencies
import $file.settings

object commonTest extends ScalaModule {
  override def scalaVersion = settings.scalaVersion
  override def scalacOptions = settings.scalacOptions

  override def ivyDeps = Agg(dependencies.scalatest)
}

trait AocModule extends ScalaModule with ScalafmtModule {
  override def scalaVersion = settings.scalaVersion
  override def scalacOptions = settings.scalacOptions

  override def ivyDeps = Agg(
    dependencies.enumeratum,
    dependencies.cats,
  )

  object test extends Tests with ScalafmtModule {
    override def testFrameworks = Seq("org.scalatest.tools.Framework")
    override def moduleDeps = commonTest +: super.moduleDeps
  }
}

object day1 extends AocModule
object day2 extends AocModule
object day6 extends AocModule
object day7 extends AocModule
object day8 extends AocModule
object day9 extends AocModule
