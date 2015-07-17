package uk.gov.gds.ier.transaction.country

import uk.gov.gds.ier.test.FormTestSuite
import play.api.libs.json.{Json, JsNull}

class CountryFormTests
  extends FormTestSuite
  with CountryForms {

  it should "successfully bind a valid country choice (Abroad)" in {
    val js = Json.toJson(
      Map(
        "country.residence" -> "Abroad",
        "country.origin" -> "England"
      )
    )
    countryForm.bind(js).fold(
      hasErrors => fail(hasErrors.prettyPrint.mkString),
      success => {
        success.country.isDefined should be(true)
        val country = success.country.get
        country.country should be("England")
        country.abroad should be(true)
      }
    )
  }

  it should "successfully bind a valid country choice (British Islands)" in {
    val js = Json.toJson(
      Map(
        "country.residence" -> "British Islands"
      )
    )
    countryForm.bind(js).fold(
      hasErrors => fail(hasErrors.prettyPrint.mkString),
      success => {
        success.country.isDefined should be(true)
        val country = success.country.get
        country.abroad should be(false)
        country.country should be("British Islands")
      }
    )
  }

  it should "successfully bind a valid country choice (Northern Ireland)" in {
    val js = Json.toJson(
      Map(
        "country.residence" -> "Northern Ireland"
      )
    )
    countryForm.bind(js).fold(
      hasErrors => fail(hasErrors.prettyPrint.mkString),
      success => {
        success.country.isDefined should be(true)
        val country = success.country.get
        country.country should be("Northern Ireland")
        country.abroad should be(false)
      }
    )
  }

  it should "successfully bind a valid country choice (Wales)" in {
    val js = Json.toJson(
      Map(
        "country.residence" -> "Wales"
      )
    )
    countryForm.bind(js).fold(
      hasErrors => fail(hasErrors.prettyPrint.mkString),
      success => {
        success.country.isDefined should be(true)
        val country = success.country.get
        country.country should be("Wales")
        country.abroad should be(false)
      }
    )
  }

  it should "successfully bind a valid country choice (Scotland)" in {
    val js = Json.toJson(
      Map(
        "country.residence" -> "Scotland"
      )
    )
    countryForm.bind(js).fold(
      hasErrors => fail(hasErrors.prettyPrint.mkString),
      success => {
        success.country.isDefined should be(true)
        val country = success.country.get
        country.country should be("Scotland")
        country.abroad should be(false)
      }
    )
  }

  it should "successfully bind a valid country choice (England)" in {
    val js = Json.toJson(
      Map(
        "country.residence" -> "England"
      )
    )
    countryForm.bind(js).fold(
      hasErrors => fail(hasErrors.prettyPrint.mkString),
      success => {
        success.country.isDefined should be(true)
        val country = success.country.get
        country.country should be("England")
        country.abroad should be(false)
      }
    )
  }

  it should "error out on invalid country choice" in {
    val js = Json.toJson(
      Map(
        "country.residence" -> "Joe Bloggs"
      )
    )
    countryForm.bind(js).fold(
      hasErrors => {
        hasErrors.errors.size should be(2)
        hasErrors.errorMessages("country.residence") should be(Seq("ordinary_country_error_notValidCountry"))
        hasErrors.globalErrorMessages should be(Seq("ordinary_country_error_notValidCountry"))
      },
      success => fail("Should have errored out")
    )
  }

  it should "error out on missing origin when abroad" in {
    val js = Json.toJson(
      Map(
        "country.residence" -> "Abroad"
      )
    )
    countryForm.bind(js).fold(
      hasErrors => {
        hasErrors.errors.size should be(2)
        hasErrors.errorMessages("country.origin") should be(Seq("ordinary_country_error_pleaseAnswer"))
        hasErrors.globalErrorMessages should be(Seq("ordinary_country_error_pleaseAnswer"))
      },
      success => fail("Should have errored out")
    )
  }
  it should "error out on empty json" in {
    val js = JsNull

    countryForm.bind(js).fold(
      hasErrors => {
        hasErrors.errors.size should be(2)
        hasErrors.errorMessages("country.residence") should be(Seq("ordinary_country_error_pleaseAnswer"))
        hasErrors.globalErrorMessages should be(Seq("ordinary_country_error_pleaseAnswer"))
      },
      success => fail("Should have errored out")
    )
  }


  it should "error out on empty values" in {
    val js =  Json.toJson(
      Map(
        "country.residence" -> ""
      )
    )
    countryForm.bind(js).fold(
      hasErrors => {
        hasErrors.errors.size should be(2)
        hasErrors.errorMessages("country.residence") should be(Seq("ordinary_country_error_pleaseAnswer"))
        hasErrors.globalErrorMessages should be(Seq("ordinary_country_error_pleaseAnswer"))
      },
      success => fail("Should have errored out")
    )
  }
}

