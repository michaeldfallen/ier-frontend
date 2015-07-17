package uk.gov.gds.ier.model

case class PostalOrProxyVote (
    typeVote: WaysToVoteType,
    postalVoteOption: Option[Boolean],
    deliveryMethod: Option[PostalVoteDeliveryMethod]) {

  def toApiMap = {
    val voteMap = postalVoteOption match {
      case Some(pvote) => typeVote match {
        case WaysToVoteType.ByPost => Map("pvote" -> pvote.toString)
        case WaysToVoteType.ByProxy => Map("proxyvote" -> pvote.toString)
        case _ => Map.empty
      }
      case _ => Map.empty
    }
    val emailMap = deliveryMethod.flatMap(_.emailAddress) match {
      case Some(email) => typeVote match {
        case WaysToVoteType.ByPost => Map("pvoteemail" -> email)
        case WaysToVoteType.ByProxy => Map("proxyvoteemail" -> email)
        case _ => Map.empty
      }
      case _ => Map.empty
    }
    voteMap ++ emailMap
  }
}

object PostalOrProxyVote extends ModelMapping {
  import playMappings._ 

  def mapping = playMappings.mapping(
    keys.voteType.key -> text,
    keys.optIn.key -> optional(boolean),
    keys.deliveryMethod.key -> optional(PostalVoteDeliveryMethod.mapping)
  ) (
    (voteType, postalVoteOption, deliveryMethod) => PostalOrProxyVote(
      WaysToVoteType.parse(voteType),
      postalVoteOption,
      deliveryMethod
    )
  ) (
    postalVote => Some(
      postalVote.typeVote.name,
      postalVote.postalVoteOption,
      postalVote.deliveryMethod
    )
  )
}
