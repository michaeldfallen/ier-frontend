package uk.gov.gds.ier.transaction.overseas.openRegister

import uk.gov.gds.ier.transaction.overseas.InprogressOverseas
import uk.gov.gds.ier.step.StepTemplate

trait OpenRegisterMustache extends StepTemplate[InprogressOverseas] {

  val title = "Do you want to include your name and address on the open register?"

  case class OpenRegisterModel(
      question:Question,
      openRegister: Field
  ) extends MustacheData

  val mustache = MustacheTemplate("overseas/openRegister") { (form, post) =>

    implicit val progressForm = form

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
