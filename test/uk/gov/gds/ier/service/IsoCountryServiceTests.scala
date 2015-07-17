package uk.gov.gds.ier.service

import uk.gov.gds.ier.test.UnitTestSuite
import uk.gov.gds.ier.model.PartialNationality

class IsoCountryServiceTests extends UnitTestSuite {

  behavior of "IsoCountryService.transformToIsoCode"
  it should "fill in iso codes from country names" in {
    val nationality = PartialNationality(british = Some(true), irish = Some(true), otherCountries = List("France", "Italy"))
    val isoNationality = new IsoCountryService().transformToIsoCode(nationality)

    isoNationality.countryIsos should contain("GB")
    isoNationality.countryIsos should contain("IE")
    isoNationality.countryIsos should contain("FR")
    isoNationality.countryIsos should contain("IT")
  }

  it should "handle no iso codes instance" in {
    new IsoCountryService().transformToIsoCode(PartialNationality()).countryIsos should be(Nil)
  }

  it should "handle bad country names" in {
    new IsoCountryService().transformToIsoCode(PartialNationality(otherCountries = List("BLARGH"))).countryIsos should be(Nil)
  }

  behavior of "IsoCountryService.getFranchises"
  it should "provide the correct franchises for the checked countries" in {
    val service = new IsoCountryService()

    service.getFranchises(PartialNationality(british = Some(true))) should equal(List("Full", "EU", "Commonwealth"))
    service.getFranchises(PartialNationality(irish = Some(true))) should equal(List("Full", "EU"))
  }

  it should "provide only distinct franchises for multiple countries" in {
    val service = new IsoCountryService()

    service.getFranchises(PartialNationality(british = Some(true), irish = Some(true))) should equal(List("Full", "EU", "Commonwealth"))
    service.getFranchises(PartialNationality(british = Some(true), otherCountries = List("France"))) should equal(List("Full", "EU", "Commonwealth"))
    service.getFranchises(PartialNationality(otherCountries = List("Japan", "France"))) should equal(List("EU"))
  }

  it should "not provide a franchise for unfranchised countries" in {
    val service = new IsoCountryService()

    service.getFranchises(PartialNationality(otherCountries = List("Japan"))) should equal(List.empty)
    service.getFranchises(PartialNationality(otherCountries = List("Afghanistan"))) should equal(List.empty)
  }
}
