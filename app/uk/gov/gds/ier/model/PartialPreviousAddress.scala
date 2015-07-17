package uk.gov.gds.ier.model

case class PartialPreviousAddress (
  movedRecently:Option[MovedHouseOption],
  previousAddress:Option[PartialAddress]
)

object PartialPreviousAddress extends ModelMapping {

  import playMappings._

  lazy val mapping = playMappings.mapping(
    keys.movedRecently.key -> optional(MovedHouseOption.mapping),
    keys.previousAddress.key -> optional(PartialAddress.mapping)
  ) (
    PartialPreviousAddress.apply
  ) (
    PartialPreviousAddress.unapply
  )
}