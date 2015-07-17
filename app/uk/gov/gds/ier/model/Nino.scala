package uk.gov.gds.ier.model

import uk.gov.gds.ier.validation._

case class Nino(nino:Option[String],
                noNinoReason:Option[String]) {
  def toApiMap = {
    nino.map(n => Map("nino" -> n)).getOrElse(Map.empty) ++
    noNinoReason.map(nonino => Map("nonino" -> nonino)).getOrElse(Map.empty)
  }
}

object Nino extends ModelMapping with ErrorMessages {
  import playMappings._

  val mapping = playMappings.mapping(
    keys.nino.key -> optional(nonEmptyText),
    keys.noNinoReason.key -> optional(nonEmptyText)
  ) (
    Nino.apply
  ) (
    Nino.unapply
  )
}