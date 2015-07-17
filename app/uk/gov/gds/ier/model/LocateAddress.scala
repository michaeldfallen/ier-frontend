package uk.gov.gds.ier.model

case class LocateAddress(
    property:Option[String],
    street:Option[String],
    locality:Option[String],
    town:Option[String],
    area:Option[String],
    postcode:String,
    uprn:Option[String],
    gssCode:Option[String]) {
  def prettyAddressLine = {
    val addressLine = property ++: street ++: locality ++: List(postcode)
    addressLine.mkString(", ")
  }

  def toApiMap(addressKey:String) = {
    property.map(x => Map(addressKey + "property" -> x)).getOrElse(Map.empty) ++
    street.map(x => Map(addressKey + "street" -> x)).getOrElse(Map.empty) ++
    locality.map(x => Map(addressKey + "locality" -> x)).getOrElse(Map.empty) ++
    town.map(x => Map(addressKey + "town" -> x)).getOrElse(Map.empty) ++
    area.map(x => Map(addressKey + "area" -> x)).getOrElse(Map.empty) ++
    Map(addressKey + "postcode" -> postcode.replaceAllLiterally(" ", "").toLowerCase) ++
    uprn.map(x => Map(addressKey + "uprn" -> x)).getOrElse(Map.empty) ++
    gssCode.map(x => Map(addressKey + "gssCode" -> x)).getOrElse(Map.empty)
  }
}
