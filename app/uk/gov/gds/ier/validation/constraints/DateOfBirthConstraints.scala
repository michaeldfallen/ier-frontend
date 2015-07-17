package uk.gov.gds.ier.validation.constraints

import uk.gov.gds.ier.validation._
import uk.gov.gds.ier.validation.constants.DateOfBirthConstants
import play.api.data.validation.{Invalid, Valid, Constraint}
import uk.gov.gds.ier.model._
import org.joda.time.DateMidnight
import uk.gov.gds.ier.model.DateOfBirth
import uk.gov.gds.ier.model.DOB
import scala.Some
import uk.gov.gds.ier.transaction.ordinary.InprogressOrdinary
import uk.gov.gds.ier.transaction.overseas.InprogressOverseas

trait DateOfBirthConstraints extends CommonConstraints{
  self: ErrorMessages
    with FormKeys =>

  lazy val dateOfBirthRequired = Constraint[InprogressOrdinary](keys.dob.key) {
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
  
    lazy val dateOfBirthOverseasRequired = Constraint[InprogressOverseas](keys.dob.key) {
    application => application.dob match {
      case Some(dob) => Valid
      case None => Invalid(
        "Please enter your date of birth", 
        keys.dob.day, 
        keys.dob.month, 
        keys.dob.year
      )
    }
  }

  lazy val isOverTheMinimumAgeToVote = Constraint[DOB](keys.dob.key) {
    dateOfBirth =>

      val validDate = DateValidator.isExistingDate(dateOfBirth)

      validDate match {

        case Some(dateMidnight:DateMidnight) => {
          if (DateValidator.isExistingDateInThePast(dateMidnight) &&
            DateValidator.isTooYoungToRegister(dateOfBirth)) {
            Invalid(
              s"Minimum age to register to vote is ${DateValidator.minimumAge}",
              keys.dob.dob.day,
              keys.dob.dob.month,
              keys.dob.dob.year
            )
          } else {
            Valid
          }
        }

        case None => Invalid(
          "You have entered an invalid date",keys.dob.dob.day,keys.dob.dob.month,keys.dob.dob.year)
      }
  }

  lazy val validDate = Constraint[DOB](keys.dob.key) {
    dateOfBirth =>
      val validDate = DateValidator.isExistingDate(dateOfBirth)

      validDate match {
        case Some(dateMidnight:DateMidnight) => {

          if (!DateValidator.isExistingDateInThePast(dateMidnight)) {
            Invalid("You have entered a date in the future",keys.dob.dob.day,keys.dob.dob.month,keys.dob.dob.year)
          } else if (DateValidator.isTooOldToBeAlive(dateMidnight)) {
            Invalid("Please check the year you were born", keys.dob.dob.year)
          } else {
            Valid
          }
        }
        case None => Invalid(
          "You have entered an invalid date",keys.dob.dob.day,keys.dob.dob.month,keys.dob.dob.year)
      }
  }
    lazy val validDateOverseas = Constraint[DOB](keys.dob.key) {
    dateOfBirth =>
      val validDate = DateValidator.isExistingDate(dateOfBirth)

      validDate match {
        case Some(dateMidnight:DateMidnight) => {

          if (!DateValidator.isExistingDateInThePast(dateMidnight)) {
            Invalid("You have entered a date in the future",keys.dob.day,keys.dob.month,keys.dob.year)
          } else if (DateValidator.isTooOldToBeAlive(dateMidnight)) {
            Invalid("Please check the year you were born", keys.dob.year)
          } else {
            Valid
          }
        }
        case None => Invalid(
          "You have entered an invalid date",keys.dob.day,keys.dob.month,keys.dob.year)
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
        Invalid("Please select a rough age range", keys.dob.noDob.range)
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
        Invalid("Please provide a reason", keys.dob.noDob.reason)
      }
    }
  }

  lazy val dobOrNoDobIsFilled = Constraint[DateOfBirth](keys.dob.key) {
    dateOfBirth => 
      if (dateOfBirth.dob.isDefined || dateOfBirth.noDob.isDefined) {
        Valid
      } else {
        Invalid("Please answer this question", keys.dob.dob)
      }
  }
}
