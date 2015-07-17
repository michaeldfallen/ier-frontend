package uk.gov.gds.ier.transaction.overseas.nino

import uk.gov.gds.ier.transaction.overseas.InprogressOverseas
import uk.gov.gds.ier.step.StepTemplate


trait NinoMustache extends StepTemplate[InprogressOverseas] {

  val title = "What is your National Insurance number?"

  case class NinoModel(
      question:Question,
      nino: Field,
      noNinoReason: Field
  ) extends MustacheData

  val mustache = MustacheTemplate("overseas/nino") { (form, post) =>

    implicit val progressForm = form

    NinoModel(
      question = Question(
        postUrl = post.url,
        errorMessages = form.globalErrors.map{ _.message },
        title = title
      ),
      nino = TextField(
        key = keys.nino.nino
      ),
      noNinoReason = TextField(
        key = keys.nino.noNinoReason
      )
    )
  }
}
