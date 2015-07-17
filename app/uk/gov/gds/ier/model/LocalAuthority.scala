package uk.gov.gds.ier.model

case class LocalAuthority (
  gssCode: Option[String],
  eroIdentifier: Option[String],
  eroDescription: Option[String],
  contactDetails: Option[LocalAuthorityContactDetails])

case class LocalAuthorityContactDetails(
  name: Option[String] = None,
  url: Option[String] = None,
  addressLine1: Option[String] = None,
  addressLine2: Option[String] = None,
  addressLine3: Option[String] = None,
  addressLine4: Option[String] = None,
  postcode: Option[String] = None,
  emailAddress: Option[String] = None,
  phoneNumber: Option[String] = None)
