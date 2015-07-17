package uk.gov.gds.ier.transaction.crown.openRegister

import uk.gov.gds.ier.validation.ErrorTransformForm
import uk.gov.gds.ier.step.StepTemplate
import uk.gov.gds.ier.transaction.crown.InprogressCrown

trait OpenRegisterMustache extends StepTemplate[InprogressCrown] {

  case class OpenRegisterModel(
      question:Question,
      openRegister: Field
  ) extends MustacheData

  val mustache = MustacheTemplate("crown/openRegister") { (form, post) =>
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

