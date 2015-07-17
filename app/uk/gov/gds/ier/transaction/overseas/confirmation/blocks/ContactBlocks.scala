package uk.gov.gds.ier.transaction.overseas.confirmation.blocks

trait ContactBlocks {
  self: ConfirmationBlock =>

  val post = if(form(keys.contact.post.contactMe).value == Some("true")){
    Some("By post")
  } else None

  val phone = if(form(keys.contact.phone.contactMe).value == Some("true")){
    form(keys.contact.phone.detail).value.map( phone => s"By phone: $phone")
  } else None

  val email = if(form(keys.contact.email.contactMe).value == Some("true")){
    form(keys.contact.email.detail).value.map {email => s"By email: $email"}
  } else None

  def contact = {
    ConfirmationQuestion(
      title = "How we should contact you",
      editLink = overseas.ContactStep.routing.editGet.url,
      changeName = "how we should contact you",
      content = ifComplete(keys.contact) {
        List(post, phone, email).flatten
      }
    )
  }
}
