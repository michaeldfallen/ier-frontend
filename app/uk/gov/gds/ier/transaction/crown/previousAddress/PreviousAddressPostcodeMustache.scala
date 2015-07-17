package uk.gov.gds.ier.transaction.crown.previousAddress

import uk.gov.gds.ier.step.StepTemplate
import uk.gov.gds.ier.transaction.crown.InprogressCrown

trait PreviousAddressPostcodeMustache
  extends StepTemplate[InprogressCrown] {

    val title = "What was your previous UK address?"

    case class PostcodeModel (
        question: Question,
        postcode: Field
    ) extends MustacheData

  val mustache = MustacheTemplate("crown/previousAddressPostcode") {
    (form, post) =>
    implicit val progressForm = form
    PostcodeModel(
      question = Question(
        postUrl = post.url,
        title = title,
        errorMessages = form.globalErrors.map(_.message)
      ),
      postcode = TextField(keys.previousAddress.previousAddress.postcode)
    )
  }
}

