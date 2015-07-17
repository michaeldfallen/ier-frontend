package uk.gov.gds.ier.model

sealed abstract class ApplicationType

object ApplicationType {
  case object YoungVoter extends ApplicationType
  case object NewVoter extends ApplicationType
  case object SpecialVoter extends ApplicationType
  case object RenewerVoter extends ApplicationType
  case object DontKnow extends ApplicationType
}