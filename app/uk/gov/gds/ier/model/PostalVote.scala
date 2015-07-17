package uk.gov.gds.ier.model

case class PostalVote (
    postalVoteOption: Option[PostalVoteOption],
    deliveryMethod: Option[PostalVoteDeliveryMethod]
)

object PostalVote extends ModelMapping {
  import playMappings._

  val mapping = playMappings.mapping(
    keys.optIn.key -> optional(PostalVoteOption.mapping),
    keys.deliveryMethod.key -> optional(PostalVoteDeliveryMethod.mapping)
  ) (
    (optIn, delivery) => PostalVote(
      postalVoteOption = optIn,
      deliveryMethod = optIn match {
        case Some(PostalVoteOption.Yes) => delivery
        case _ => None
      }
    )
  ) (
    PostalVote.unapply
  )
}
case class PostalVoteDeliveryMethod(
  deliveryMethod: Option[String],
  emailAddress: Option[String]) {

  def isEmail = deliveryMethod.exists(_ == "email")
}

object PostalVoteDeliveryMethod extends ModelMapping {
  import playMappings._

  val mapping = playMappings.mapping(
    keys.methodName.key -> optional(nonEmptyText),
    keys.emailAddress.key -> optional(nonEmptyText)
  )(
    (method, email) => PostalVoteDeliveryMethod(
      deliveryMethod = method,
      emailAddress = method match {
        case Some("email") => email
        case _ => None
      }
    )
  )(
    PostalVoteDeliveryMethod.unapply
  )
}
