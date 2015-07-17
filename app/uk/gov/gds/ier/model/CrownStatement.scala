package uk.gov.gds.ier.model

case class CrownStatement(
    crownServant: Boolean,
    crownPartner: Boolean,
    councilEmployee: Boolean,
    councilPartner: Boolean) {

  def toApiMap = {
    Map(
      "crwn" -> crownServant.toString,
      "scrwn" -> crownPartner.toString,
      "bc" -> councilEmployee.toString,
      "sbc" -> councilPartner.toString
    )
  }
}
