package uk.gov.gds.ier.transaction.forces.nino

import uk.gov.gds.ier.step.StepTemplate
import uk.gov.gds.ier.transaction.forces.InprogressForces


trait NinoMustache extends StepTemplate[InprogressForces] {

  case class NinoModel (
      question:Question,
      nino: Field,
      noNinoReason: Field,
      noNinoReasonShowFlag: Text
  ) extends MustacheData

  val mustache = MustacheTemplate("forces/nino") { (form, postEndpoint) =>

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
