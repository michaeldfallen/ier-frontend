package uk.gov.gds.ier.validation.constraints

import play.api.data.validation.{Invalid, Valid, Constraint}
import uk.gov.gds.ier.model.PartialNationality
import uk.gov.gds.ier.validation.{FormKeys, ErrorMessages}

import uk.gov.gds.ier.validation.constants.NationalityConstants._
import scala.Some

trait NationalityConstraints {
  self: ErrorMessages
    with FormKeys =>

  lazy val notTooManyNationalities = Constraint[PartialNationality](keys.nationality.key) {
    nationality =>
      if (nationality.otherCountries.size <= numberMaxOfOtherCountries) Valid
      else Invalid("You can specifiy no more than five countries", keys.nationality)
  }

  lazy val nationalityIsChosen = Constraint[PartialNationality](keys.nationality.key) {
    nationality =>
      if (nationality.british == Some(true) || nationality.irish == Some(true)) Valid
      else if (nationality.otherCountries.exists(_.nonEmpty) && nationality.hasOtherCountry.exists(b => b)) Valid
      else if (nationality.noNationalityReason.isDefined) Valid
      else Invalid("Please answer this question", keys.nationality)
  }

  lazy val otherCountry0IsValid = otherCountryIsValid(0)
  lazy val otherCountry1IsValid = otherCountryIsValid(1)
  lazy val otherCountry2IsValid = otherCountryIsValid(2)

  private def otherCountryIsValid(i:Int) = Constraint[PartialNationality](keys.nationality.otherCountries.key) {
    nationality =>
      if (nationality.otherCountries.isEmpty || !nationality.hasOtherCountry.exists(b => b)) Valid
      else if (nationality.otherCountries.size != i+1) Valid
      else if (nationality.otherCountries.size > i
        && validNationalitiesList.contains(nationality.otherCountries(i).toLowerCase)) Valid
      else Invalid("This is not a valid entry", keys.nationality.otherCountries.item(i))
  }

}


