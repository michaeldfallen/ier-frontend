package uk.gov.gds.ier.transaction.ordinary.otherAddress

import uk.gov.gds.ier.model.OtherAddress._
import uk.gov.gds.ier.step.StepTemplate
import uk.gov.gds.ier.transaction.ordinary.InprogressOrdinary

trait OtherAddressMustache extends StepTemplate[InprogressOrdinary] {

  case class OtherAddressModel(
      question: Question,
      hasOtherAddress: FieldSet,
      hasOtherAddressStudent: Field,
      hasOtherAddressHome: Field,
      hasOtherAddressNone: Field
  ) extends MustacheData

  val mustache = MultilingualTemplate("ordinary/otherAddress") { implicit lang => (form, post) =>
    implicit val progressForm = form

    OtherAddressModel(
      question = Question(
        postUrl = post.url,
        number = Messages("step_a_of_b", 7, 11),
        title = Messages("ordinary_otheraddr_title"),
        errorMessages = Messages.translatedGlobalErrors(form)
      ),
      hasOtherAddress = FieldSet(
        classes = if (form(keys.otherAddress).hasErrors) "invalid" else ""
      ),
      hasOtherAddressStudent = RadioField(
        key = keys.otherAddress.hasOtherAddress, value = StudentOtherAddress.name
      ),
      hasOtherAddressHome = RadioField(
        key = keys.otherAddress.hasOtherAddress, value = HomeOtherAddress.name
      ),
      hasOtherAddressNone = RadioField(
        key = keys.otherAddress.hasOtherAddress, value = NoOtherAddress.name
      )
    )
  }
}

