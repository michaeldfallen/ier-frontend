package uk.gov.gds.ier.model

import scala.util.Try

sealed case class ServiceType(name:String)

object ServiceType {
  val RoyalNavy = ServiceType("Royal Navy")
  val BritishArmy = ServiceType("British Army")
  val RoyalAirForce = ServiceType("Royal Air Force")

  def isValid(str:String) = {
    Try {
      parse(str)
    }.isSuccess
  }

  def parse(str:String) = {
    str match {
      case "Royal Navy" => RoyalNavy
      case "British Army" => BritishArmy
      case "Royal Air Force" => RoyalAirForce
      case _ => throw new IllegalArgumentException(s"$str not a valid ServiceType")
    }
  }
}
