package uk.gov.gds.ier.transaction.forces.contact

import uk.gov.gds.ier.validation.ErrorTransformForm
import uk.gov.gds.ier.transaction.forces.InprogressForces
import uk.gov.gds.ier.step.StepTemplate

trait ContactMustache extends StepTemplate[InprogressForces] {

  case class ContactModel (
      question:Question,
      contactFieldSet: FieldSet,
      contactEmailCheckbox: Field,
      contactPhoneCheckbox: Field,
      contactPostCheckbox: Field,
      contactEmailText: Field,
      contactPhoneText: Field
  ) extends MustacheData

  val mustache = MustacheTemplate("forces/contact") { (form, postUrl) =>
    implicit val progressForm = form
    val emailAddress = form(keys.postalOrProxyVote.deliveryMethod.emailAddress).value

    val title = "If we have questions about your application, how should we contact you?"
    ContactModel(
      question = Question(
        postUrl = postUrl.url,
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
