package uk.gov.gds.ier.transaction.ordinary.contact

import uk.gov.gds.ier.step.StepTemplate
import uk.gov.gds.ier.transaction.ordinary.InprogressOrdinary

trait ContactMustache extends StepTemplate[InprogressOrdinary] {

  case class ContactModel (
      question:Question,
      contactFieldSet: FieldSet,
      contactEmailCheckbox: Field,
      contactPhoneCheckbox: Field,
      contactPostCheckbox: Field,
      contactEmailText: Field,
      contactPhoneText: Field
  ) extends MustacheData

  val mustache = MultilingualTemplate("ordinary/contact") { implicit lang => (form, post) =>
    implicit val progressForm = form

    val emailAddress = form(keys.postalVote.deliveryMethod.emailAddress).value

    ContactModel(
      question = Question(
        postUrl = post.url,
        errorMessages = Messages.translatedGlobalErrors(form),
        number = s"11 ${Messages("step_of")} 11",
        title = Messages("ordinary_contact_title")
      ),
      contactFieldSet = FieldSet(
        classes = if (progressForm(keys.contact).hasErrors) "invalid" else ""
      ),
      contactEmailCheckbox = CheckboxField(
        key = keys.contact.email.contactMe, value = "true"
      ),
      contactPhoneCheckbox = CheckboxField(
        key = keys.contact.phone.contactMe, value = "true"
      ),
      contactPostCheckbox = CheckboxField(
        key = keys.contact.post.contactMe, value = "true"
      ),
      contactEmailText = TextField(
        key = keys.contact.email.detail,
        default = emailAddress
      ),
      contactPhoneText = TextField(
        key = keys.contact.phone.detail
      )
    )
  }
}
