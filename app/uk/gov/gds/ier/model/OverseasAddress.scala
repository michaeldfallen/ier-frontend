package uk.gov.gds.ier.model

case class OverseasAddress(
    country: Option[String],
    addressLine1: Option[String],
    addressLine2: Option[String],
    addressLine3: Option[String],
    addressLine4: Option[String],
    addressLine5: Option[String]) {

  def toApiMap =
    Map("corrcountry" -> country.getOrElse("")) ++
    addressLine1.map(addressLine => Map("corraddressline1" -> addressLine.toString)).getOrElse(Map.empty) ++
    addressLine2.map(addressLine => Map("corraddressline2" -> addressLine.toString)).getOrElse(Map.empty) ++
    addressLine3.map(addressLine => Map("corraddressline3" -> addressLine.toString)).getOrElse(Map.empty) ++
    addressLine4.map(addressLine => Map("corraddressline4" -> addressLine.toString)).getOrElse(Map.empty) ++
    addressLine5.map(addressLine => Map("corraddressline5" -> addressLine.toString)).getOrElse(Map.empty)
}
