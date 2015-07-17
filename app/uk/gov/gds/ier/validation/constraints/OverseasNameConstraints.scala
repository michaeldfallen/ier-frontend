package uk.gov.gds.ier.validation.constraints

import uk.gov.gds.ier.validation.{FormKeys, ErrorMessages, Key}
import uk.gov.gds.ier.model.{Name, PreviousName}
import play.api.data.validation.{Invalid, Valid, Constraint}
import play.api.data.Mapping
import play.api.data.Forms._

trait OverseasNameConstraints extends CommonConstraints {
  self:  FormKeys
    with ErrorMessages =>

  lazy val nameNotOptional = Constraint[Option[Name]](keys.overseasName.name.key) {
    name =>
      if (name.isDefined) Valid
      else Invalid("Please enter your full name", keys.overseasName.name.firstName, keys.overseasName.name.lastName)
  }
  
  lazy val firstNameNotTooLong = fieldNotTooLong[Name](keys.overseasName.name.firstName,
    firstNameMaxLengthError) {
    name => name.firstName
  }

  lazy val middleNamesNotTooLong = fieldNotTooLong[Name](keys.overseasName.name.middleNames,
    middleNameMaxLengthError) {
    name => name.middleNames.getOrElse("")
  }

  lazy val lastNameNotTooLong = fieldNotTooLong[Name](keys.overseasName.name.lastName,
    lastNameMaxLengthError) {
    name => name.lastName
  }

  lazy val prevFirstNameNotTooLong = fieldNotTooLong[Name](
    keys.overseasName.previousName.previousName.firstName,
    firstNameMaxLengthError) {
    name => name.firstName
  }

  lazy val prevMiddleNamesNotTooLong = fieldNotTooLong[Name](
    keys.overseasName.previousName.previousName.middleNames,
    middleNameMaxLengthError) {
    name => name.middleNames.getOrElse("")
  }

  lazy val prevLastNameNotTooLong = fieldNotTooLong[Name](
    keys.overseasName.previousName.previousName.lastName,
    lastNameMaxLengthError) {
    name => name.lastName
  }

  lazy val prevNameFilledIfHasPrevIsTrue = Constraint[PreviousName](keys.overseasName.previousName.previousName.key) {
    prevName =>
      if ((prevName.hasPreviousName && prevName.previousName.isDefined) || !prevName.hasPreviousName){
        Valid
      } else {
        Invalid("Please enter your previous name", 
            keys.overseasName.previousName.previousName.firstName, 
            keys.overseasName.previousName.previousName.lastName)
      }
  }
}
