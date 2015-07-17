package uk.gov.gds.ier.validation.constraints

import uk.gov.gds.ier.validation.{FormKeys, ErrorMessages, Key}
import uk.gov.gds.ier.model.{Name, PreviousName}
import play.api.data.validation.{Invalid, Valid, Constraint}
import play.api.data.Mapping
import play.api.data.Forms._

trait NameCommonConstraints extends CommonConstraints {
  self:  FormKeys
    with ErrorMessages =>

  lazy val firstNameNotTooLong = fieldNotTooLong[Option[Name]](
    fieldKey = keys.name.firstName,
    errorMessage = firstNameMaxLengthError,
    maxLength = maxFirstLastNameLength) {
      _.map { _.firstName } getOrElse ""
    }

  lazy val middleNamesNotTooLong = fieldNotTooLong[Option[Name]](
    fieldKey = keys.name.middleNames,
    errorMessage = middleNameMaxLengthError,
    maxLength = maxMiddleNameLength) {
      _.map { _.middleNames.getOrElse("") } getOrElse("")
    }

  lazy val lastNameNotTooLong = fieldNotTooLong[Option[Name]](
    fieldKey = keys.name.lastName,
    errorMessage = lastNameMaxLengthError,
    maxLength = maxFirstLastNameLength) {
      _.map { _.lastName } getOrElse("")
    }

  lazy val prevFirstNameNotTooLong = fieldNotTooLong[Option[PreviousName]](
    fieldKey = keys.previousName.previousName.firstName,
    errorMessage = previousFirstNameMaxLengthError,
    maxLength = maxFirstLastNameLength) {
      _.flatMap { _.previousName }
        .map { _.firstName }
        .getOrElse("")
    }

  lazy val prevMiddleNamesNotTooLong = fieldNotTooLong[Option[PreviousName]](
    fieldKey = keys.previousName.previousName.middleNames,
    errorMessage = previousMiddleNameMaxLengthError,
    maxLength = maxMiddleNameLength) {
      _.flatMap{ _.previousName }
        .map{ _.middleNames.getOrElse("") }
        .getOrElse("")
    }

  lazy val prevLastNameNotTooLong = fieldNotTooLong[Option[PreviousName]](
    fieldKey = keys.previousName.previousName.lastName,
    errorMessage = previousLastNameMaxLengthError,
    maxLength = maxFirstLastNameLength) {
      _.flatMap { _.previousName }
        .map { _.lastName }
        .getOrElse("")
    }

}
