package uk.gov.gds.ier.model

case class IsoNationality(countryIsos:List[String] = List.empty,
                          noNationalityReason:Option[String] = None) {
  def toApiMap = {
    val natMap = if (countryIsos.isEmpty) Map.empty else Map("nat" -> countryIsos.mkString(", "))
    val noNatMap = noNationalityReason.map(nat => Map("nonat" -> nat)).getOrElse(Map.empty)
    natMap ++ noNatMap
  }
}
