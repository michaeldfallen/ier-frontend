package uk.gov.gds.ier.transaction.forces.name

import uk.gov.gds.ier.validation.{ErrorTransformForm, ErrorMessages, FormKeys}
import uk.gov.gds.ier.model.{PreviousName, Name}
import play.api.data.Forms._
import uk.gov.gds.ier.validation.constraints.NameCommonConstraints
import uk.gov.gds.ier.transaction.forces.InprogressForces

trait NameForms extends NameConstraints {
  self:  FormKeys
    with ErrorMessages =>

  val nameForm = ErrorTransformForm(
    mapping(
      keys.name.key -> optional(Name.mapping)
        .verifying(
          firstNameNotTooLong,
          middleNamesNotTooLong,
          lastNameNotTooLong),
      keys.previousName.key -> optional(PreviousName.mapping)
        .verifying(
          prevFirstNameNotTooLong,
          prevMiddleNamesNotTooLong,
          prevLastNameNotTooLong)
    ) (
      (name, previousName) => InprogressForces(
        name = name,
        previousName = previousName
      )
    ) (
      inprogress => Some(
        inprogress.name,
        inprogress.previousName
      )
    ) verifying (
      nameRequired,
      previousNameAnswered,
      firstNameRequired,
      lastNameRequired,
      prevNameRequiredIfHasPrevNameTrue,
      prevFirstNameRequired,
      prevLastNameRequired
    )
  )
}

trait NameConstraints extends NameCommonConstraints with FormKeys {

  lazy val nameRequired = Constraint[InprogressForces](keys.name.key) {
    _.name match {
      case Some(_) => Valid
      case None => Invalid(
        "Please enter your full name",
        keys.name.firstName,
        keys.name.lastName
      )
    }
  }

  lazy val previousNameAnswered = Constraint[InprogressForces](
    keys.previousName.key
  ) {
    _.previousName match {
      case Some(_) => Valid
      case _ => Invalid(
        "Please answer this question",
        keys.previousName
      )
    }
  }

  lazy val lastNameRequired = Constraint[InprogressForces] (
    keys.name.lastName.key
  ) {
    _.name match {
      case Some(Name(_, _, "")) => Invalid (
        "Please enter your last name",
        keys.name.lastName
      )
      case _ => Valid
    }
  }

  lazy val firstNameRequired = Constraint[InprogressForces] (
    keys.name.firstName.key
  ) {
    _.name match {
      case Some(Name("", _, _)) => Invalid (
        "Please enter your first name",
        keys.name.firstName
      )
      case _ => Valid
    }
  }

  lazy val prevFirstNameRequired = Constraint[InprogressForces] (
    keys.previousName.previousName.firstName.key
  ) {
    _.previousName match {
      case Some(PreviousName(true, "true",Some(Name("", _, _)), _)) => Invalid (
        "Please enter your first name",
        keys.previousName.previousName.firstName
      )
      case _ => Valid
    }
  }

  lazy val prevLastNameRequired = Constraint[InprogressForces] (
    keys.previousName.previousName.lastName.key
  ) {
    _.previousName match {
      case Some(PreviousName(true, "true", Some(Name(_, _, "")), _)) => Invalid (
        "Please enter your last name",
        keys.previousName.previousName.lastName
      )
      case _ => Valid
    }
  }

  lazy val prevNameRequiredIfHasPrevNameTrue = Constraint[InprogressForces] (
    keys.previousName.previousName.key
  ) {
    _.previousName match {
      case Some(PreviousName(true, "true", None, _)) => Invalid (
        "Please enter your full previous name",
        keys.previousName.previousName,
        keys.previousName.previousName.firstName,
        keys.previousName.previousName.lastName
      )
      case _ => Valid
    }
  }
}

