package uk.gov.gds.ier.model

case class PossibleContactAddresses(
    contactAddressType: Option[String],
    ukAddressLine: Option[String],
    bfpoContactAddress: Option[ContactAddress],
    otherContactAddress: Option[ContactAddress]) {

  def toApiMap (address: Option[Address]) = {
    contactAddressType match {
      case Some("uk") => toApiMapFromUkAddress(address)
      case Some("bfpo") => bfpoContactAddress.get.toApiMap
      case Some("other") => otherContactAddress.get.toApiMap
      case _ => throw new IllegalArgumentException
    }
  }

  private def toApiMapFromUkAddress (address: Option[Address]) = {
    if (address.isDefined) {
      val ukAddress = address.get
      Map(
        "corrcountry" -> "uk",
        "corrpostcode" -> Postcode.toApiFormat(ukAddress.postcode)
      ) ++
      ukAddress.lineOne.map(lineOne => Map("corraddressline1" -> lineOne.toString)).getOrElse(Map.empty) ++
      ukAddress.lineTwo.map(lineTwo => Map("corraddressline2" -> lineTwo.toString)).getOrElse(Map.empty) ++
      ukAddress.lineThree.map(lineThree => Map("corraddressline3" -> lineThree.toString)).getOrElse(Map.empty) ++
      ukAddress.city.map(city => Map("corraddressline4" -> city.toString)).getOrElse(Map.empty) ++
      ukAddress.county.map(county => Map("corraddressline5" -> county.toString)).getOrElse(Map.empty)
    }
    else Map.empty
  }
}

