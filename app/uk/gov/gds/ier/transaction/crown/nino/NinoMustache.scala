package uk.gov.gds.ier.transaction.crown.nino

import uk.gov.gds.ier.validation.ErrorTransformForm
import play.api.mvc.Call
import play.api.templates.Html
import uk.gov.gds.ier.step.StepTemplate
import uk.gov.gds.ier.transaction.crown.InprogressCrown


trait NinoMustache extends StepTemplate[InprogressCrown] {

  case class NinoModel (
      question:Question,
      nino: Field,
      noNinoReason: Field,
      noNinoReasonShowFlag: Text
  ) extends MustacheData

  val mustache = MustacheTemplate("crown/nino") { (form, postEndpoint) =>

    implicit val progressForm = form

    val title = "What is your National Insurance number?"

    NinoModel(
      question = Question(
        postUrl = postEndpoint.url,
        errorMessages = form.globalErrors.map(_.message),
        title = title
      ),
      nino = TextField(
        key = keys.nino.nino
      ),
      noNinoReason = TextField(
        key = keys.nino.noNinoReason
      ),
      noNinoReasonShowFlag = Text (
        value = progressForm(keys.nino.noNinoReason).value.fold("")(noNinoReason => "-open")
      )
    )
  }
}
