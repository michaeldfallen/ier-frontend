package uk.gov.gds.ier.model

case class Contact (
    post: Boolean,
    phone: Option[ContactDetail],
    email: Option[ContactDetail]
) {

  def toApiMap = {
    Map("post" -> post.toString) ++
      phone.filter(_.contactMe).flatMap(_.detail).map("phone" -> _).toMap ++
      email.filter(_.contactMe).flatMap(_.detail).map("email" -> _).toMap
  }
}

object Contact extends ModelMapping {
  import playMappings._

  val mapping = playMappings.mapping(
    keys.post.contactMe.key -> boolean,
    keys.phone.key -> optional(ContactDetail.mapping),
    keys.email.key -> optional(ContactDetail.mapping)
  ) (
    Contact.apply
  ) (
    Contact.unapply
  )
}

case class ContactDetail (
    contactMe:Boolean,
    detail:Option[String]
)

object ContactDetail extends ModelMapping {
  import playMappings._

  val mapping = playMappings.mapping(
    keys.contactMe.key -> boolean,
    keys.detail.key -> optional(text)
  ) (
    ContactDetail.apply
  ) (
    ContactDetail.unapply
  )
}
