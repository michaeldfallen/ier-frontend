package uk.gov.gds.ier.model

case class LastAddress(
    hasAddress:Option[HasAddressOption],
    address:Option[PartialAddress]
)

object LastAddress extends ModelMapping {

  import playMappings._

  lazy val mapping = playMappings.mapping(
    keys.hasAddress.key -> optional(HasAddressOption.mapping),
    keys.address.key -> optional(PartialAddress.mapping)
  ) (
    LastAddress.apply
  ) (
    LastAddress.unapply
  )
}
