package uk.gov.gds.ier.model

case class Statement(
    memberForcesFlag: Option[Boolean],
    partnerForcesFlag: Option[Boolean]) {

  def toApiMap =
    if (isPartner) Map("saf" -> "true")
    else Map("saf" -> "false")

  def isPartner: Boolean = {
    val isForcesPartner = Some(true)
    val isNotForcesMember = Some(false)
    ( partnerForcesFlag, memberForcesFlag ) match {
      case (`isForcesPartner`, `isNotForcesMember`) => true
      case (`isForcesPartner`, None) => true
      case _ => false
    }
  }

}
