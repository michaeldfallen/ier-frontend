package uk.gov.gds.ier.transaction.forces.previousAddress

import uk.gov.gds.ier.step.StepTemplate
import uk.gov.gds.ier.transaction.forces.InprogressForces

trait PreviousAddressPostcodeMustache
  extends StepTemplate[InprogressForces] {

    val title = "What was your previous UK address?"

    case class PostcodeModel (
        question: Question,
        postcode: Field
    ) extends MustacheData

  val mustache = MustacheTemplate("forces/previousAddressPostcode") {
    (form, post) =>
    implicit val progressForm = form
    PostcodeModel(
      question = Question(
        postUrl = post.url,
        title = title,
        errorMessages = form.globalErrors.map(_.message)
      ),
      postcode = TextField(keys.previousAddress.postcode)
    )
  }
}

