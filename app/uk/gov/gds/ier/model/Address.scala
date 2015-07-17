package uk.gov.gds.ier.model

import scala.Predef._

case class Address(
    lineOne:Option[String],
    lineTwo:Option[String],
    lineThree:Option[String],
    city:Option[String],
    county:Option[String],
    uprn:Option[String],
    postcode:String,
    gssCode: Option[String] = None) {

  def prettyAddressLine = {
    val addressLine = lineOne ++: lineTwo ++: lineThree ++: List(postcode)
    addressLine.mkString(", ")
  }

  def toApiMap(addressKey:String) = {
    lineOne.map(x => Map(addressKey + "property" -> x)).getOrElse(Map.empty) ++
      lineTwo.map(x => Map(addressKey + "street" -> x)).getOrElse(Map.empty) ++
      lineThree.map(x => Map(addressKey + "locality" -> x)).getOrElse(Map.empty) ++
      city.map(x => Map(addressKey + "town" -> x)).getOrElse(Map.empty) ++
      county.map(x => Map(addressKey + "area" -> x)).getOrElse(Map.empty) ++
      uprn.map(x => Map(addressKey + "uprn" -> x)).getOrElse(Map.empty) ++
      Map(addressKey + "postcode" -> Postcode.toApiFormat(postcode))
  }
}

object Address {
  def apply(postcode: String): Address = Address(
    lineOne = None,
    lineTwo = None,
    lineThree = None,
    city = None,
    county = None,
    uprn = None,
    postcode = postcode,
    gssCode = None)
}
