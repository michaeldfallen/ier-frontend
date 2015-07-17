package uk.gov.gds.ier.transaction.forces.name

import uk.gov.gds.ier.transaction.forces.InprogressForces
import uk.gov.gds.ier.step.StepTemplate

trait NameMustache extends StepTemplate[InprogressForces] {

  val pageTitle = "What is your full name?"

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

  val mustache = MustacheTemplate("forces/name") { (form, postUrl) =>
    implicit val progressForm = form
    NameModel(
      question = Question(
        postUrl = postUrl.url,
        title = pageTitle,
        errorMessages = form.globalErrors.map ( _.message )
      ),

      firstName = TextField(key = keys.name.firstName),
      middleNames = TextField(key = keys.name.middleNames),
      lastName = TextField(key = keys.name.lastName),

      hasPreviousNameOption = FieldSet(
        classes = if (form(keys.previousName).hasErrors) "invalid" else ""
      ),
      hasPreviousNameOptionFalse = RadioField(
        key = keys.previousName.hasPreviousNameOption,
        value = "false"
      ),
      hasPreviousNameOptionTrue = RadioField(
        key = keys.previousName.hasPreviousNameOption,
        value = "true"
      ),
      hasPreviousNameOptionOther = RadioField(
        key = keys.previousName.hasPreviousNameOption,
        value = "other"
      ),

      previousFirstName = TextField(
        key = keys.previousName.previousName.firstName
      ),
      previousMiddleNames = TextField(
        key = keys.previousName.previousName.middleNames
      ),
      previousLastName = TextField(
        key = keys.previousName.previousName.lastName
      )
    )
  }
}
