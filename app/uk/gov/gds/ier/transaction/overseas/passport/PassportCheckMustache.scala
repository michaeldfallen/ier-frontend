package uk.gov.gds.ier.transaction.overseas.passport

import uk.gov.gds.ier.transaction.overseas.InprogressOverseas
import uk.gov.gds.ier.step.StepTemplate

trait PassportCheckMustache extends StepTemplate[InprogressOverseas] {

  val title = "Do you have a British Passport?"

  case class PassportCheckModel(
      question: Question,
      hasPassport: Field,
      hasPassportTrue: Field,
      hasPassportFalse: Field,
      bornInUk: Field,
      bornInUkTrue: Field,
      bornInUkFalse: Field
  ) extends MustacheData

  val mustache = MustacheTemplate("overseas/passportCheck") { (form, post) =>

    implicit val progressForm = form

    PassportCheckModel(
      question = Question(
        postUrl = post.url,
        errorMessages = form.globalErrors.map{ _.message },
        number = "",
        title = title
      ),
      hasPassport = Field(
        classes = if (form(keys.passport.hasPassport).hasErrors) {
          "invalid"
        } else ""
      ),
      hasPassportTrue = RadioField (
        key = keys.passport.hasPassport,
        value = "true"
      ),
      hasPassportFalse = RadioField (
        key = keys.passport.hasPassport,
        value = "false"
      ),
      bornInUk = Field(
        classes = if (form(keys.passport.bornInsideUk).hasErrors) {
          "invalid"
        } else ""
      ),
      bornInUkTrue = RadioField (
        key = keys.passport.bornInsideUk,
        value = "true"
      ),
      bornInUkFalse = RadioField (
        key = keys.passport.bornInsideUk,
        value = "false"
      )
    )
  }
}

