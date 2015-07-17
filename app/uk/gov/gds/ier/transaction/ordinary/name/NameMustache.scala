package uk.gov.gds.ier.transaction.ordinary.name

import uk.gov.gds.ier.validation.ErrorTransformForm
import uk.gov.gds.ier.step.StepTemplate
import uk.gov.gds.ier.transaction.ordinary.InprogressOrdinary

trait NameMustache extends StepTemplate[InprogressOrdinary] {

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
    previousLastName: Field
  ) extends MustacheData

  val mustache = MultilingualTemplate("ordinary/name") { implicit lang =>
    (form, post) =>
    implicit val progressForm = form

    NameModel(
      question = Question(
        postUrl = post.url,
        number = s"4 ${Messages("step_of")} 11",
        title = Messages("ordinary_name_title"),
        errorMessages = Messages.translatedGlobalErrors(form)),

      firstName = TextField(
        key = keys.name.firstName),
      middleNames = TextField(
        key = keys.name.middleNames),
      lastName = TextField(
        key = keys.name.lastName),

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
        key = keys.previousName.previousName.lastName)
    )
  }
}

