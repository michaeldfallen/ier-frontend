package uk.gov.gds.ier.service.apiservice

case class IerApiApplicationResponse (
  id: Option[String],
  localAuthority: EroAuthorityDetails
)

case class EroAuthorityDetails(
  name: String,
  urls: List[String],
  email: Option[String],
  phone: Option[String],
  addressLine1: Option[String],
  addressLine2: Option[String],
  addressLine3: Option[String],
  addressLine4: Option[String],
  postcode: Option[String]
)
