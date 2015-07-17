package uk.gov.gds.ier.transaction.crown.dateOfBirth

import uk.gov.gds.ier.validation.ErrorTransformForm
import uk.gov.gds.ier.step.StepTemplate
import uk.gov.gds.ier.transaction.crown.InprogressCrown

trait DateOfBirthMustache extends StepTemplate[InprogressCrown] {

  case class DateOfBirthModel(
      question:Question,
      day: Field,
      month: Field,
      year: Field,
      noDobReason: Field,
      rangeFieldSet: FieldSet,
      rangeUnder18: Field,
      rangeOver70: Field,
      range18to70: Field,
      rangeDontKnow: Field,
      noDobReasonShowFlag: Text
  ) extends MustacheData

  val mustache = MustacheTemplate("crown/dateOfBirth") { (form, post) =>
    implicit val progressForm = form

    val title = "What is your date of birth?"

    DateOfBirthModel(
      question = Question(
        postUrl = post.url,
        errorMessages = form.globalErrors.map{ _.message },
        title = title
      ),
      day = TextField(
        key = keys.dob.dob.day
      ),
      month = TextField(
        key = keys.dob.dob.month
      ),
      year = TextField(
        key = keys.dob.dob.year
      ),
      noDobReason = TextField(
        key = keys.dob.noDob.reason
      ),
      rangeFieldSet = FieldSet (
        classes = if (form(keys.dob.noDob.range).hasErrors) "invalid" else ""
      ),
      rangeUnder18 = RadioField(
        key = keys.dob.noDob.range,
        value = "under18"
      ),
      range18to70 = RadioField(
        key = keys.dob.noDob.range,
        value = "18to70"
      ),
      rangeOver70 = RadioField(
        key = keys.dob.noDob.range,
        value = "over70"
      ),
      rangeDontKnow = RadioField(
        key = keys.dob.noDob.range,
        value = "dontKnow"
      ),
      noDobReasonShowFlag = Text (
        value = progressForm(keys.dob.noDob.reason).value.map(noDobReason => "-open").getOrElse("")
      )
    )
  }
}

