package uk.gov.gds.ier.transaction.overseas.parentName

import uk.gov.gds.ier.transaction.overseas.InprogressOverseas
import uk.gov.gds.ier.step.StepTemplate

trait ParentNameMustache extends StepTemplate[InprogressOverseas] {

  val title = "Parent or guardian's registration details"

  case class ParentNameModel(
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

  val mustache = MustacheTemplate("overseas/parentName") { (form, post) =>

    implicit val progressForm = form

    ParentNameModel(
      question = Question(
        postUrl = post.url,
        title = title,
        errorMessages = form.globalErrors.map { _.message }
      ),

      firstName = TextField(
        key = keys.overseasParentName.parentName.firstName),
      middleNames = TextField(
        key = keys.overseasParentName.parentName.middleNames),
      lastName = TextField(
        key = keys.overseasParentName.parentName.lastName),

      hasPreviousNameOption = FieldSet(
        classes = if (form(keys.overseasParentName.parentPreviousName).hasErrors) "invalid" else ""
      ),
      hasPreviousNameOptionFalse = RadioField(
        key = keys.overseasParentName.parentPreviousName.hasPreviousNameOption, value = "false"),
      hasPreviousNameOptionTrue = RadioField(
        key = keys.overseasParentName.parentPreviousName.hasPreviousNameOption, value = "true"),
      hasPreviousNameOptionOther = RadioField(
        key = keys.overseasParentName.parentPreviousName.hasPreviousNameOption, value = "other"),

      previousFirstName = TextField(
        key = keys.overseasParentName.parentPreviousName.previousName.firstName),
      previousMiddleNames = TextField(
        key = keys.overseasParentName.parentPreviousName.previousName.middleNames),
      previousLastName = TextField(
        key = keys.overseasParentName.parentPreviousName.previousName.lastName)
    )
  }
}
