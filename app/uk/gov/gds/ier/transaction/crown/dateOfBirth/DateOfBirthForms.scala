package uk.gov.gds.ier.transaction.crown.dateOfBirth

import uk.gov.gds.ier.validation.{FormKeys, ErrorMessages, ErrorTransformForm}
import uk.gov.gds.ier.validation.constraints.DateOfBirthConstraints
import play.api.data.Forms._
import uk.gov.gds.ier.model.DateOfBirth
import uk.gov.gds.ier.model.noDOB
import uk.gov.gds.ier.model.DOB
import scala.Some
import play.api.data.validation.{Invalid, Valid, Constraint}
import uk.gov.gds.ier.transaction.crown.InprogressCrown

trait DateOfBirthForms extends DateOfBirthCrownConstraints {
    self:  FormKeys
      with ErrorMessages =>

  lazy val dobMapping = mapping(
    keys.year.key -> text
      .verifying("Please enter your year of birth", _.nonEmpty)
      .verifying("The year you provided is invalid", year => year.isEmpty || year.matches("\\d+")),
    keys.month.key -> text
      .verifying("Please enter your month of birth", _.nonEmpty)
      .verifying("The month you provided is invalid", month => month.isEmpty || month.matches("\\d+")),
    keys.day.key -> text
      .verifying("Please enter your day of birth", _.nonEmpty)
      .verifying("The day you provided is invalid", day => day.isEmpty || day.matches("\\d+"))
  ) {
    (year, month, day) => DOB(year.toInt, month.toInt, day.toInt)
  } {
    dateOfBirth => 
      Some(
        dateOfBirth.year.toString, 
        dateOfBirth.month.toString, 
        dateOfBirth.day.toString
      )
  }.verifying(validDate)

  lazy val noDobMapping = mapping(
    keys.reason.key -> optional(text),
    keys.range.key -> optional(text)
  ) (
    noDOB.apply
  ) (
    noDOB.unapply
  )

  lazy val dobAndReasonMapping = mapping(
    keys.dob.key -> optional(dobMapping),
    keys.noDob.key -> optional(noDobMapping)
  ) (
    DateOfBirth.apply
  ) (
    DateOfBirth.unapply
  ) verifying(dobOrNoDobIsFilled, ifDobEmptyRangeIsValid, ifDobEmptyReasonIsNotEmpty)

  val dateOfBirthForm = ErrorTransformForm(
    mapping(
      keys.dob.key -> optional(dobAndReasonMapping)
    ) (
      dob => InprogressCrown(dob = dob)
    ) (
      inprogress => Some(inprogress.dob)
    ) verifying dateOfBirthRequiredCrown
  )
}


trait DateOfBirthCrownConstraints extends DateOfBirthConstraints{
  self: ErrorMessages
    with FormKeys =>

  lazy val dateOfBirthRequiredCrown = Constraint[InprogressCrown](keys.dob.key) {
    application => application.dob match {
      case Some(dob) => Valid
      case None => Invalid(
        "Please enter your date of birth",
        keys.dob.dob.day,
        keys.dob.dob.month,
        keys.dob.dob.year
      )
    }
  }
}