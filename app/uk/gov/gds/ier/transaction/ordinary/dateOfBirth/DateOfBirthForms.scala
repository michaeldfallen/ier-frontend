package uk.gov.gds.ier.transaction.ordinary.dateOfBirth

import uk.gov.gds.ier.validation.{DateValidator, FormKeys, ErrorMessages, ErrorTransformForm}
import play.api.data.validation.{Invalid, Valid, Constraint}
import play.api.data.Forms._
import uk.gov.gds.ier.model.DateOfBirth
import uk.gov.gds.ier.model.noDOB
import uk.gov.gds.ier.model.DOB
import scala.Some
import uk.gov.gds.ier.transaction.ordinary.InprogressOrdinary
import uk.gov.gds.ier.validation.constants.DateOfBirthConstants
import org.joda.time.DateMidnight

trait DateOfBirthForms {
    self:  FormKeys
      with ErrorMessages =>

  lazy val dobMapping = mapping(
    keys.year.key -> text
      .verifying("ordinary_dob_error_enterYear", _.nonEmpty)
      .verifying("ordinary_dob_error_invalidYear", year => year.isEmpty || year.matches("\\d+")),
    keys.month.key -> text
      .verifying("ordinary_dob_error_enterMonth", _.nonEmpty)
      .verifying("ordinary_dob_error_invalidMonth", month => month.isEmpty || month.matches("\\d+")),
    keys.day.key -> text
      .verifying("ordinary_dob_error_enterDay", _.nonEmpty)
      .verifying("ordinary_dob_error_invalidDay", day => day.isEmpty || day.matches("\\d+"))
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
      dob => InprogressOrdinary(dob = dob)
    ) (
      inprogress => Some(inprogress.dob)
    ) verifying dateOfBirthRequired
  )

  lazy val dateOfBirthRequired = Constraint[InprogressOrdinary](keys.dob.key) {
    application => application.dob match {
      case Some(dob) => Valid
      case None => Invalid(
        "ordinary_dob_error_enterDateOfBirth",
        keys.dob.dob.day,
        keys.dob.dob.month,
        keys.dob.dob.year
      )
    }
  }

  lazy val dobOrNoDobIsFilled = Constraint[DateOfBirth](keys.dob.key) {
    dateOfBirth =>
      if (dateOfBirth.dob.isDefined || dateOfBirth.noDob.isDefined) {
        Valid
      } else {
        Invalid("ordinary_dob_error_answerThis", keys.dob.dob)
      }
  }

  lazy val ifDobEmptyRangeIsValid = Constraint[DateOfBirth](keys.noDob.key) {
    case DateOfBirth(Some(dob), _) => {
      Valid
    }
    case DateOfBirth(None, None) => {
      Valid
    }
    case DateOfBirth(_, Some(noDob)) => {
      if (noDob.range.exists(DateOfBirthConstants.noDobRanges.contains)) {
        Valid
      } else {
        Invalid("ordinary_dob_error_selectRange", keys.dob.noDob.range)
      }
    }
  }

  lazy val ifDobEmptyReasonIsNotEmpty = Constraint[DateOfBirth](keys.noDob.key) {
    case DateOfBirth(Some(dob), _) => {
      Valid
    }
    case DateOfBirth(None, None) => {
      Valid
    }
    case DateOfBirth(_, Some(noDob)) => {
      if (noDob.reason.exists(!_.isEmpty)) {
        Valid
      } else {
        Invalid("ordinary_dob_error_provideReason", keys.dob.noDob.reason)
      }
    }
  }

  lazy val validDate = Constraint[DOB](keys.dob.key) {
    dateOfBirth =>
      val validDate = DateValidator.isExistingDate(dateOfBirth)

      validDate match {
        case Some(dateMidnight:DateMidnight) => {

          if (!DateValidator.isExistingDateInThePast(dateMidnight)) {
            Invalid(
              "ordinary_dob_error_dateInTheFuture",
              keys.dob.dob.day,
              keys.dob.dob.month,
              keys.dob.dob.year)
          } else if (DateValidator.isTooOldToBeAlive(dateMidnight)) {
            Invalid("ordinary_dob_error_tooOld", keys.dob.dob.year)
          } else {
            Valid
          }
        }
        case None => Invalid(
          "ordinary_dob_error_invalidDate",
          keys.dob.dob.day,
          keys.dob.dob.month,
          keys.dob.dob.year)
      }
  }
}
