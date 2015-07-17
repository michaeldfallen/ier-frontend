package uk.gov.gds.ier.validation.constraints

import uk.gov.gds.ier.validation.{FormKeys, ErrorMessages, Key}
import uk.gov.gds.ier.model.{OverseasParentName, Name, PreviousName}
import play.api.data.validation.{Invalid, Valid, Constraint}
import play.api.data.Mapping
import play.api.data.Forms._
import play.api.Logger
import uk.gov.gds.ier.transaction.overseas.InprogressOverseas

trait ParentNameConstraints extends CommonConstraints {
  self:  FormKeys
    with ErrorMessages =>
     
      
    lazy val parentNameNotOptional = Constraint[Option[Name]] (keys.overseasParentName.parentName.key) {
      case Some(_) => Valid
      case None => Invalid(
        "Please enter their full name",
        keys.overseasParentName.parentName.firstName,
        keys.overseasParentName.parentName.lastName,
        keys.overseasParentName.parentName)
    }
    
    lazy val parentPreviousNameNotOptionalIfHasPreviousIsTrue = Constraint[Option[PreviousName]] (
      keys.overseasParentName.parentPreviousName.key) {
      name => 
        if (name.isDefined) {
         if (name.get.hasPreviousName && !name.get.previousName.isDefined) {
           Invalid("Please enter their previous full name", 
               keys.overseasParentName.parentPreviousName.previousName.firstName,
               keys.overseasParentName.parentPreviousName.previousName.lastName)
         }
         else Valid 
        }
        else Invalid("Please answer this question", keys.overseasParentName.parentPreviousName)
    }


  lazy val parentPrevNameOptionCheck = Constraint[InprogressOverseas] (keys.overseasParentName.parentPreviousName.key) {
    application =>
      if (application.overseasParentName.isDefined && application.overseasParentName.get.previousName.isDefined) Valid
      else Invalid("Please answer this question", keys.overseasParentName.parentPreviousName)
  }
   lazy val parentFirstNameNotEmpty = Constraint[Option[Name]](keys.overseasParentName.parentName.firstName.key) {
     case Some(Name("", _, _)) => Invalid(
       "Please enter their first name",
       keys.overseasParentName.parentName.firstName)
     case _ => Valid
  }
  
  lazy val parentLastNameNotEmpty = Constraint[Option[Name]](keys.overseasParentName.parentName.lastName.key) {
    case Some(Name(_, _, "")) => Invalid(
      "Please enter their last name",
      keys.overseasParentName.parentName.lastName)
    case _ => Valid
  }
  
  lazy val parentFirstNameNotTooLong = fieldNotTooLong[Option[Name]](
    fieldKey = keys.overseasParentName.parentName.firstName,
    errorMessage = firstNameMaxLengthError,
    maxLength = maxFirstLastNameLength) {
      _.map { _.firstName } getOrElse ""
    }

  lazy val parentMiddleNamesNotTooLong = fieldNotTooLong[Option[Name]](
    fieldKey = keys.overseasParentName.parentName.middleNames,
    errorMessage = middleNameMaxLengthError,
    maxLength = maxMiddleNameLength) {
      _.map { _.middleNames.getOrElse("") } getOrElse("")
    }

  lazy val parentLastNameNotTooLong = fieldNotTooLong[Option[Name]](
    fieldKey = keys.overseasParentName.parentName.lastName,
    errorMessage = lastNameMaxLengthError,
    maxLength = maxFirstLastNameLength) {
      _.map { _.lastName } getOrElse("")
    }
  
  lazy val parentPreviousFirstNameNotEmpty = Constraint[Option[PreviousName]](
    keys.overseasParentName.parentPreviousName.previousName.firstName.key) {
    case Some(PreviousName(true, "true", Some(Name("", _, _)), _)) => Invalid(
      "Please enter their previous first name",
      keys.overseasParentName.parentPreviousName.previousName.firstName)
    case _ => Valid
  }

  
  lazy val parentPreviousLastNameNotEmpty = Constraint[Option[PreviousName]](
    keys.overseasParentName.parentPreviousName.previousName.lastName.key) {
    case Some(PreviousName(true, "true", Some(Name(_, _, "")), _)) => Invalid(
      "Please enter their previous last name",
      keys.overseasParentName.parentPreviousName.previousName.lastName)
    case _ => Valid
  }
  
  lazy val parentPrevFirstNameNotTooLong = fieldNotTooLong[Option[PreviousName]](
    fieldKey = keys.overseasParentName.parentPreviousName.previousName.firstName,
    errorMessage = previousFirstNameMaxLengthError,
    maxLength = maxFirstLastNameLength) {
      _.flatMap { _.previousName }
        .map { _.firstName }
        .getOrElse("")
    }

  lazy val parentPrevMiddleNamesNotTooLong = fieldNotTooLong[Option[PreviousName]](
    fieldKey = keys.overseasParentName.parentPreviousName.previousName.middleNames,
    errorMessage = previousMiddleNameMaxLengthError,
    maxLength = maxMiddleNameLength) {
      _.flatMap{ _.previousName }
        .map{ _.middleNames.getOrElse("") }
        .getOrElse("")
    }

  lazy val parentPrevLastNameNotTooLong = fieldNotTooLong[Option[PreviousName]](
    fieldKey = keys.overseasParentName.parentPreviousName.previousName.lastName,
    errorMessage = previousLastNameMaxLengthError,
    maxLength = maxFirstLastNameLength) {
      _.flatMap { _.previousName }
        .map { _.lastName }
        .getOrElse("")
    }

  
}
