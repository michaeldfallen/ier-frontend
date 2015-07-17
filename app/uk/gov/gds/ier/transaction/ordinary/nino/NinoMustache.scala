package uk.gov.gds.ier.transaction.ordinary.nino

import uk.gov.gds.ier.validation.ErrorTransformForm
import uk.gov.gds.ier.step.StepTemplate
import uk.gov.gds.ier.transaction.ordinary.InprogressOrdinary


trait NinoMustache extends StepTemplate[InprogressOrdinary] {

  case class NinoModel (
      question:Question,
      nino: Field,
      noNinoReason: Field,
      noNinoReasonShowFlag: Text
  ) extends MustacheData

  val mustache = MultilingualTemplate("ordinary/nino") { implicit lang =>
    (form, postEndpoint) =>

    implicit val progressForm = form

    NinoModel(
      question = Question(
        postUrl = postEndpoint.url,
        errorMessages = Messages.translatedGlobalErrors(form),
        number = "5 " + Messages("step_of") + " 11",
        title = Messages("ordinary_nino_heading")
      ),
      nino = TextField(
        key = keys.nino.nino
      ),
      noNinoReason = TextField(
        key = keys.nino.noNinoReason
      ),
      noNinoReasonShowFlag = Text (
        value = progressForm(keys.nino.noNinoReason).value.map(noNinoReason => "-open").getOrElse("")
      )
    )
  }
}
