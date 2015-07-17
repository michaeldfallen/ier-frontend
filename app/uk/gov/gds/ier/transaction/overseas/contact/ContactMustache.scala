package uk.gov.gds.ier.transaction.overseas.contact

import uk.gov.gds.ier.transaction.overseas.InprogressOverseas
import uk.gov.gds.ier.step.StepTemplate

trait ContactMustache extends StepTemplate[InprogressOverseas] {

  val title = "If we have questions about your application, how should we contact you?"

  case class ContactModel (
      question:Question,
      contactFieldSet: FieldSet,
      contactEmailCheckbox: Field,
      contactPhoneCheckbox: Field,
      contactPostCheckbox: Field,
      contactEmailText: Field,
      contactPhoneText: Field
  ) extends MustacheData

  val mustache = MustacheTemplate("overseas/contact") { (form, post) =>

    implicit val progressForm = form
    val emailAddress = form(keys.postalOrProxyVote.deliveryMethod.emailAddress).value

    ContactModel(
      question = Question(
        postUrl = post.url,
        errorMessages = form.globalErrors.map{ _.message },
        title = title
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
