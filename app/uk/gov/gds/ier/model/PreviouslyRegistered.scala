package uk.gov.gds.ier.model

case class LastRegisteredToVote (
    lastRegisteredType:LastRegisteredType
) {
  def toApiMap = Map("lastcategory" -> lastRegisteredType.name)
}

object LastRegisteredToVote extends ModelMapping {
  import playMappings._

  def mapping = playMappings.mapping(
    keys.registeredType.key -> LastRegisteredType.mapping
  ) (
    registeredType => LastRegisteredToVote(registeredType)
  ) (
    lastRegistered => Some(lastRegistered.lastRegisteredType)
  )
}
