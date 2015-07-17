package uk.gov.gds.ier.model

case class PartialManualAddress(
    lineOne: Option[String] = None,
    lineTwo: Option[String] = None,
    lineThree: Option[String] = None,
    city: Option[String] = None
)

object PartialManualAddress extends ModelMapping {
  import playMappings._

  // address mapping for manual address - the address individual lines part
  lazy val mapping = playMappings.mapping(
    keys.lineOne.key -> optional(nonEmptyText),
    keys.lineTwo.key -> optional(text),
    keys.lineThree.key -> optional(text),
    keys.city.key -> optional(nonEmptyText)
  ) (
    PartialManualAddress.apply
  ) (
    PartialManualAddress.unapply
  )
}