package uk.gov.gds.ier.transaction.forces.nationality

import uk.gov.gds.ier.validation.{ErrorTransformForm, ErrorMessages, FormKeys}
import uk.gov.gds.ier.model.{PartialNationality}
import play.api.data.Forms._
import uk.gov.gds.ier.transaction.forces.InprogressForces
import play.api.data.validation.{Valid, Invalid, Constraint}
import uk.gov.gds.ier.validation.constants.NationalityConstants

trait NationalityForms extends NationalityConstraints {
  self:  FormKeys
    with ErrorMessages =>

  val nationalityForm = ErrorTransformForm(
    mapping(
      keys.nationality.key -> PartialNationality.mapping
    ) (
      nationality => InprogressForces(
        nationality = Some(nationality)
      )
    ) (
      inprogressApplication => inprogressApplication.nationality
    ) verifying (
      nationalityIsChosen,
      notTooManyNationalities,
      otherCountry0IsValid,
      otherCountry1IsValid,
      otherCountry2IsValid,
      atleastOneOtherCountryIfHasOtherCountry
    )
  )
}


trait NationalityConstraints extends FormKeys with ErrorMessages {

  lazy val atleastOneOtherCountryIfHasOtherCountry = Constraint[InprogressForces] (
    keys.nationality.otherCountries.key
  ) { application =>
    val numberOtherCoutries = application.nationality.foldLeft(0) {
      (zero, nationality) => nationality.otherCountries.size
    }
    val hasOtherCountry = application.nationality.flatMap(_.hasOtherCountry)

    (hasOtherCountry, numberOtherCoutries) match {
      case (Some(true), 0) => Invalid(
        "Please answer this question",
        keys.nationality.otherCountries
      )
      case _ => Valid
    }
  }

  lazy val notTooManyNationalities = Constraint[InprogressForces](keys.nationality.key) {
    application =>
      val numberOtherCoutries = application.nationality.foldLeft(0) {
        (zero, nationality) => nationality.otherCountries.size
      }
      if (numberOtherCoutries > NationalityConstants.numberMaxOfOtherCountries) {
        Invalid(
          "You can specify no more than five countries",
          keys.nationality.otherCountries
        )
      } else {
        Valid
      }
  }

  lazy val nationalityIsChosen = Constraint[InprogressForces](keys.nationality.key) {
    application =>
      val britishChecked = application.nationality.flatMap(_.british).getOrElse(false)
      val irishChecked = application.nationality.flatMap(_.irish).getOrElse(false)
      val hasOtherCountry = application.nationality.flatMap(_.hasOtherCountry).getOrElse(false)
      val otherCountryFilled = application.nationality.map{ nat =>
        nat.otherCountries.size > 0
      }.getOrElse(false)

      val nationalityFilled = britishChecked || irishChecked || otherCountryFilled || hasOtherCountry

      val excuseFilled = application.nationality.flatMap(_.noNationalityReason).exists(_.nonEmpty)

      if (nationalityFilled || excuseFilled) {
        Valid
      } else {
        Invalid(
          "Please answer this question",
          keys.nationality
        )
      }
  }

  lazy val otherCountry0IsValid = otherCountryIsValid(0)
  lazy val otherCountry1IsValid = otherCountryIsValid(1)
  lazy val otherCountry2IsValid = otherCountryIsValid(2)

  private def otherCountryIsValid(i:Int) = Constraint[InprogressForces](
    keys.nationality.otherCountries.key
  ) { application =>
    val otherCountry = application.nationality.flatMap(_.otherCountries.lift(i))
    val otherCountryValid = otherCountry.exists { country =>
      NationalityConstants.validNationalitiesList.contains(country.toLowerCase)
    }

    (otherCountry, otherCountryValid) match {
      case (Some(c), false) => Invalid(
        "This is not a valid entry",
        keys.nationality.otherCountries.item(i)
      )
      case _ => Valid
    }
  }
}
