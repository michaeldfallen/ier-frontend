package uk.gov.gds.ier.model

import scala.util.Try

sealed case class LastRegisteredType(name:String)

object LastRegisteredType extends ModelMapping {
  val Ordinary = LastRegisteredType("ordinary")
  val Overseas = LastRegisteredType("overseas")
  val Forces = LastRegisteredType("forces")
  val Crown = LastRegisteredType("crown")
  val Council = LastRegisteredType("council")
  val NotRegistered = LastRegisteredType("not-registered")

  def isValid(str:String) = {
    Try {
      parse(str)
    }.isSuccess
  }

  def parse(str:String) = {
    str match {
      case "ordinary" => Ordinary
      case "overseas" => Overseas
      case "forces" => Forces
      case "crown" => Crown
      case "council" => Council
      case "not-registered" => NotRegistered
      case _ => throw new IllegalArgumentException(s"$str not a valid LastRegisteredType")
    }
  }

  import playMappings._

  def mapping = text.verifying(
    Constraint[String]("LastRegisteredType") { str =>
      if(LastRegisteredType.isValid(str)) {
        Valid
      } else {
        Invalid(
          s"$str is not a valid registration type"
        )
      }
    }
  ).transform[LastRegisteredType](
    str => LastRegisteredType.parse(str),
    lastReg => lastReg.name
  )
}
