import mill._
import mill.scalalib._
import mill.scalalib.scalafmt.ScalafmtModule
import $file.dependencies
import $file.settings
import mill.api.Loose
import mill.define.Target

object commonTest extends ScalaModule with ScalafmtModule {
  override def scalaVersion = settings.scalaVersion
  override def scalacOptions = settings.scalacOptions

  override def ivyDeps = Agg(dependencies.scalatest)
}

trait DefaultScalaModule extends ScalaModule with ScalafmtModule {
  override def scalaVersion = settings.scalaVersion
  override def scalacOptions = settings.scalacOptions

  trait Tests extends super.Tests with ScalafmtModule {
    override def testFrameworks = Seq("org.scalatest.tools.Framework")
    override def moduleDeps = commonTest +: super.moduleDeps
  }
}

object intcode extends DefaultScalaModule {
  override def ivyDeps = Agg(dependencies.enumeratum)

  object test extends Tests
}

trait AocModule extends DefaultScalaModule {
  override def ivyDeps = Agg(
    dependencies.enumeratum,
    dependencies.cats,
  )

  object test extends Tests
}

object day1 extends AocModule
object day2 extends AocModule
object day6 extends AocModule
object day7 extends AocModule
object day8 extends AocModule
object day9 extends AocModule {
  override def moduleDeps = Seq(intcode)
}
object day10 extends AocModule {
  override def ivyDeps = Agg(dependencies.commonsMath)
}
