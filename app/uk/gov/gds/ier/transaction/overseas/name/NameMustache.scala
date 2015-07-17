package uk.gov.gds.ier.transaction.overseas.name

import uk.gov.gds.ier.transaction.overseas.InprogressOverseas
import uk.gov.gds.ier.step.StepTemplate

trait NameMustache extends StepTemplate[InprogressOverseas] {

  val title = "What is your full name?"

  case class NameModel(
    question: Question,
    firstName: Field,
    middleNames: Field,
    lastName: Field,
    hasPreviousNameOption: FieldSet,
    hasPreviousNameOptionFalse: Field,
    hasPreviousNameOptionTrue: Field,
    hasPreviousNameOptionOther: Field,
    previousFirstName: Field,
    previousMiddleNames: Field,
    previousLastName: Field,
    nameChangeReason: Field
  ) extends MustacheData

  val mustache = MustacheTemplate("overseas/name") { (form, post) =>

    implicit val progressForm = form

    NameModel(
      question = Question(
        postUrl = post.url,
        title = title,
        errorMessages = form.globalErrors.map { _.message }),

      firstName = TextField(key = keys.name.firstName),
      middleNames = TextField(key = keys.name.middleNames),
      lastName = TextField(key = keys.name.lastName),

      hasPreviousNameOption = FieldSet(
        classes = if (form(keys.previousName).hasErrors) "invalid" else ""
      ),
      hasPreviousNameOptionFalse = RadioField(
        key = keys.previousName.hasPreviousNameOption, value = "false"),
      hasPreviousNameOptionTrue = RadioField(
        key = keys.previousName.hasPreviousNameOption, value = "true"),
      hasPreviousNameOptionOther = RadioField(
        key = keys.previousName.hasPreviousNameOption, value = "other"),

      previousFirstName = TextField(
        key = keys.previousName.previousName.firstName),
      previousMiddleNames = TextField(
        key = keys.previousName.previousName.middleNames),
      previousLastName = TextField(
        key = keys.previousName.previousName.lastName),
      nameChangeReason = TextField(
        key = keys.previousName.reason)
    )
  }
}
