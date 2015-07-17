package uk.gov.gds.ier.service

import uk.gov.gds.ier.validation.constraints.NationalityConstraints
import uk.gov.gds.ier.validation.{ErrorMessages, FormKeys}
import uk.gov.gds.ier.model.PartialNationality
import uk.gov.gds.ier.model.IsoNationality
import uk.gov.gds.ier.validation.constants.NationalityConstants._

class IsoCountryService
  extends NationalityConstraints
  with FormKeys
  with ErrorMessages {

  def isValidCountry(country:String):Boolean = {
    countryNameToCodes.contains(country)
  }

  def transformToIsoCode(nationality:PartialNationality):IsoNationality = {
    val nationalities = nationality.isoCheckedNationalities ++ nationality.otherCountries
    val isoCountries = nationalities.flatMap{
      country => countryNameToCodes.get(country.toLowerCase)
    }
    val isoCodes = isoCountries map(_.isoCode)
    IsoNationality(countryIsos = isoCodes, nationality.noNationalityReason)
  }

  def getFranchises(nationality:PartialNationality):List[Franchise] = {
    val nationalities = nationality.isoCheckedNationalities ++ nationality.otherCountries
    val isoCodes = nationalities.flatMap{
      country => countryNameToCodes.get(country.toLowerCase)
    }
    val franchises = isoCodes.flatMap(_.franchise)
    franchises.distinct
  }
}
