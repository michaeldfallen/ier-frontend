package uk.gov.gds.ier.transaction.country

import uk.gov.gds.ier.validation.{ErrorTransformForm, ErrorMessages, FormKeys}
import uk.gov.gds.ier.model.{Country}
import uk.gov.gds.ier.validation.constraints.CountryConstraints
import uk.gov.gds.ier.serialiser.WithSerialiser
import play.api.data.Form
import play.api.data.Forms._
import uk.gov.gds.ier.transaction.ordinary.InprogressOrdinary

trait CountryForms extends CountryConstraints {
  self:  FormKeys
    with ErrorMessages =>
  
  lazy val countryMapping = mapping(
    keys.residence.key -> optional(text).verifying("ordinary_country_error_pleaseAnswer", _.isDefined),
    keys.origin.key -> optional(text)
  ) {
    case (Some("Abroad"), origin) => Country(origin.getOrElse(""), true)
    case (residence, _) => Country(residence.getOrElse(""), false)
  } {
    case Country(country, true) => Some(Some("Abroad"), Some(country))
    case Country(country, false) => Some(Some(country), None)
  }.verifying(isValidCountryConstraint, ifAbroadOriginFilled)

  val countryForm = ErrorTransformForm(
    mapping(
      keys.country.key -> optional(countryMapping)
    ) (
      country => InprogressOrdinary(country = country)
    ) (
      inprogress => Some(inprogress.country)
    ) verifying countryIsFilledConstraint
  )
}

