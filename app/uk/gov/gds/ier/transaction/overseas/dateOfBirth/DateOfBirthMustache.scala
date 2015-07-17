package uk.gov.gds.ier.transaction.overseas.dateOfBirth

import uk.gov.gds.ier.transaction.overseas.InprogressOverseas
import uk.gov.gds.ier.step.StepTemplate

trait DateOfBirthMustache extends StepTemplate[InprogressOverseas] {

  val title = "What is your date of birth?"

  case class DateOfBirthModel(
      question:Question,
      day: Field,
      month: Field,
      year: Field
  ) extends MustacheData

  val mustache = MustacheTemplate("overseas/dateOfBirth") { (form, post) =>

    implicit val progressForm = form

    DateOfBirthModel(
      question = Question(
        postUrl = post.url,
        errorMessages = form.globalErrors.map{ _.message },
        title = title
      ),
      day = TextField(
        key = keys.dob.day
      ),
      month = TextField(
        key = keys.dob.month
      ),
      year = TextField(
        key = keys.dob.year
      )
    )
  }
}
