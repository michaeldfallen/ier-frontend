package uk.gov.gds.ier.transaction.forces.openRegister

import uk.gov.gds.ier.transaction.forces.InprogressForces
import uk.gov.gds.ier.step.StepTemplate

trait OpenRegisterMustache extends StepTemplate[InprogressForces] {

  case class OpenRegisterModel(
    question:Question,
    openRegister: Field
  ) extends MustacheData

  val mustache = MustacheTemplate("forces/openRegister") { (form, post) =>
    implicit val progressForm = form
    val title = "Do you want to include your name and address on the open register?"
    OpenRegisterModel(
      question = Question(
        postUrl = post.url,
        errorMessages = form.globalErrors.map{ _.message },
        title = title
      ),
      openRegister = CheckboxField (
        key = keys.openRegister.optIn,
        value = "false"
      )
    )
  }
}
