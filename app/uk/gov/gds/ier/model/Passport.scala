package uk.gov.gds.ier.model

case class Passport(
    hasPassport: Boolean,
    bornInsideUk: Option[Boolean],
    details: Option[PassportDetails],
    citizen: Option[CitizenDetails]) {

  def toApiMap = {
    Map("bpass" -> hasPassport.toString) ++
      details.map(_.toApiMap).getOrElse(Map.empty) ++
      citizen.map(_.toApiMap).getOrElse(Map.empty)
  }
}


