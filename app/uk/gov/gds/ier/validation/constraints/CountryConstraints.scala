package uk.gov.gds.ier.validation.constraints

import play.api.data.validation.{Invalid, Valid, Constraint}
import uk.gov.gds.ier.model.{Country}
import uk.gov.gds.ier.validation.FormKeys
import uk.gov.gds.ier.transaction.ordinary.InprogressOrdinary

trait CountryConstraints {
  self: FormKeys =>
  lazy val validCountries = List(
    "England",
    "Scotland",
    "Wales",
    "Northern Ireland",
    "British Islands",
    "Abroad")

  lazy val isValidCountryConstraint = Constraint[Country](keys.country.residence.key) {
    country =>
      if (validCountries.contains(country.country) || country.abroad) Valid
      else Invalid("ordinary_country_error_notValidCountry", keys.country.residence)
  }

  lazy val ifAbroadOriginFilled = Constraint[Country](keys.country.origin.key) {
    case Country("", true) => Invalid("ordinary_country_error_pleaseAnswer", keys.country.origin)
    case Country(_, _) => Valid
  }

  lazy val countryIsFilledConstraint = Constraint[InprogressOrdinary](keys.country.residence.key) {
    application =>
      if (application.country.isDefined) Valid
      else Invalid("ordinary_country_error_pleaseAnswer", keys.country.residence)
  }
}
