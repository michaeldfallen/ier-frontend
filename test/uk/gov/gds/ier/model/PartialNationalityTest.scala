package uk.gov.gds.ier.model

import uk.gov.gds.ier.test.UnitTestSuite

class PartialNationalityTest extends UnitTestSuite {

  behavior of "PartialNationality.toNiceString"

  it should "no nationality" in {
    val nationality = PartialNationality(
      british = Some(false),
      irish = Some(false),
      hasOtherCountry = Some(false),
      otherCountries = Nil,
      noNationalityReason = None
    )
    nationality.toNiceString should be(None)
  }

  it should "one nationality - British" in {
    val nationality = PartialNationality(
      british = Some(true),
      irish = Some(false),
      hasOtherCountry = Some(false),
      otherCountries = Nil,
      noNationalityReason = None
    )
    nationality.toNiceString should be(Some("British"))
  }

  it should "one nationality - other" in {
    val nationality = PartialNationality(
      british = Some(false),
      irish = Some(false),
      hasOtherCountry = Some(true),
      otherCountries = "Czech" :: Nil,
      noNationalityReason = None
    )
    nationality.toNiceString should be(Some("Czech"))
  }

  it should "multiple nationalities - British and Irish" in {
    val nationality = PartialNationality(
      british = Some(true),
      irish = Some(true),
      hasOtherCountry = Some(false),
      otherCountries = Nil,
      noNationalityReason = None
    )
    nationality.toNiceString should be(Some("British and Irish"))
  }

  it should "multiple nationalities - British and Irish and other" in {
    val nationality = PartialNationality(
      british = Some(true),
      irish = Some(true),
      hasOtherCountry = Some(true),
      otherCountries = "Czech" :: Nil,
      noNationalityReason = None
    )
    nationality.toNiceString should be(Some("British, Irish and Czech"))
  }

  it should "not display other nationalities when hasOtherCountry flag is false" in {
    val nationality = PartialNationality(
      british = Some(true),
      irish = Some(true),
      hasOtherCountry = Some(false),
      otherCountries = "Czech" :: Nil,
      noNationalityReason = None
    )
    nationality.toNiceString should be(Some("British and Irish"))
  }

  it should "not display other nationalities when hasOtherCountry flag is None" in {
    val nationality = PartialNationality(
      british = Some(true),
      irish = Some(true),
      hasOtherCountry = None,
      otherCountries = "Czech" :: Nil,
      noNationalityReason = None
    )
    nationality.toNiceString should be(Some("British and Irish"))
  }

  it should "display multiple other nationalities but skipping empty items" in {
    val nationality = PartialNationality(
      british = Some(false),
      irish = Some(false),
      hasOtherCountry = Some(true),
      otherCountries = "Czech" :: "   " :: "Slovak" :: "Spanish" :: Nil,
      noNationalityReason = None
    )
    nationality.toNiceString should be(Some("Czech, Slovak and Spanish"))
  }

  it should "default string" in {
    val nationalities = Some(PartialNationality(
      british = Some(false),
      irish = Some(false),
      hasOtherCountry = Some(false),
      otherCountries = Nil,
      noNationalityReason = None
    ))
    nationalities.flatMap(_.toNiceString).getOrElse("unspecified") should be("unspecified")
  }
}
