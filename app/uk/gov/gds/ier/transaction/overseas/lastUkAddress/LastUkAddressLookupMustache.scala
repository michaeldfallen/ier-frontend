package uk.gov.gds.ier.transaction.overseas.lastUkAddress

import uk.gov.gds.ier.transaction.overseas.InprogressOverseas
import uk.gov.gds.ier.step.StepTemplate

trait LastUkAddressLookupMustache extends StepTemplate[InprogressOverseas] {

  val title = "What was the UK address where you were last registered to vote?"
  val questionNumber = ""

  case class LookupModel (
      question: Question,
      postcode: Field
  ) extends MustacheData

  val mustache = MustacheTemplate("overseas/lastUkAddressLookup") { (form, post) =>

    implicit val progressForm = form

    LookupModel(
      question = Question(
        postUrl = post.url,
        number = questionNumber,
        title = title,
        errorMessages = form.globalErrors.map(_.message)
      ),
      postcode = Field(
        id = keys.lastUkAddress.postcode.asId(),
        name = keys.lastUkAddress.postcode.key,
        value = form(keys.lastUkAddress.postcode).value.getOrElse(""),
        classes = if (form(keys.lastUkAddress.postcode).hasErrors) {
          "invalid"
        } else {
          ""
        }
      )
    )
  }
}
