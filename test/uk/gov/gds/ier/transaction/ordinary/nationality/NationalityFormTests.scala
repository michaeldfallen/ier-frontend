package uk.gov.gds.ier.transaction.ordinary.nationality

import uk.gov.gds.ier.test.FormTestSuite

class NationalityFormTests
  extends FormTestSuite
  with NationalityForms {

  it should "succesfully bind json" in {
    val js = Map (
      "nationality.british" -> "true",
      "nationality.irish" -> "true",
      "nationality.otherCountries[0]" -> "Italy",
      "nationality.otherCountries[1]" -> "France",
      "nationality.hasOtherCountry" -> "true"
    )
    nationalityForm.bind(js).fold(
      hasErrors => {
        fail(hasErrors.prettyPrint.mkString(", "))
      },
      success => {
        success.nationality.isDefined should be(true)
        val nationality = success.nationality.get

        nationality.british should be(Some(true))
        nationality.irish should be(Some(true))

        nationality.otherCountries should contain("Italy")
        nationality.otherCountries should contain("France")

        nationality.hasOtherCountry should be(Some(true))
      }
    )
  }

  it should "succesfully bind json with only checked nationalities" in {
    val js = Map (
      "nationality.british" -> "true",
      "nationality.irish" -> "true"
    )
    nationalityForm.bind(js).fold(
      hasErrors => {
        fail(hasErrors.prettyPrint.mkString(", "))
      },
      success => {
        success.nationality.isDefined should be(true)
        val nationality = success.nationality.get

        nationality.british should be(Some(true))
        nationality.irish should be(Some(true))

        nationality.otherCountries should be(List.empty)
        nationality.hasOtherCountry should be(Some(false))
      }
    )
  }

  it should "only bind to nationality in InProgressApplication" in {
    val js = Map (
      "nationality.british" -> "true",
      "nationality.irish" -> "true",
      "nationality.otherCountries[0]" -> "Italy",
      "nationality.otherCountries[1]" -> "France",
      "nationality.hasOtherCountry" -> "true"
    )
    nationalityForm.bind(js).fold(
      hasErrors => {
        fail(hasErrors.prettyPrint.mkString(", "))
      },
      success => {
        success.nationality.isDefined should be(true)

        success.address should be(None)
        success.contact should be(None)
        success.dob should be(None)
        success.name should be(None)
        success.nino should be(None)
        success.openRegisterOptin should be(None)
        success.otherAddress should be(None)
        success.postalVote should be(None)
        success.previousAddress should be(None)
        success.previousName should be(None)
      }
    )
  }

  it should "handle no nationality or other country correctly" in {
    val js = Map(
      "nationality.noNationalityReason" -> "I don't have a nationality. I am stateless."
    )
    nationalityForm.bind(js).fold(
      hasErrors => fail(hasErrors.prettyPrint.mkString(", ")),
      success => {
        val nationality = success.nationality.get
        nationality.hasOtherCountry should be(Some(false))
        nationality.british should be(Some(false))
        nationality.irish should be(Some(false))
        nationality.otherCountries should be(List.empty)
        nationality.noNationalityReason should be(
          Some("I don't have a nationality. I am stateless.")
        )
      }
    )
  }

  it should "error out on empty json" in {
    val js = JsNull
    nationalityForm.bind(js).fold(
      hasErrors => {
        hasErrors.keyedErrorsAsMap should matchMap(Map(
          "nationality" -> Seq("ordinary_nationality_error_pleaseAnswer")
        ))
      },
      success => fail("Should have errored out.")
    )
  }

  it should "only support 3 other countries" in {
    val js = Map (
      "nationality.british" -> "true",
      "nationality.irish" -> "true",
      "nationality.otherCountries[0]" -> "Italy",
      "nationality.otherCountries[1]" -> "France",
      "nationality.otherCountries[2]" -> "Spain",
      "nationality.otherCountries[3]" -> "Japan",
      "nationality.hasOtherCountry" -> "true"
    )
    nationalityForm.bind(js).fold (
      hasErrors => {
        hasErrors.keyedErrorsAsMap should matchMap(Map(
          "nationality.otherCountries" -> Seq("ordinary_nationality_error_noMoreFiveCountries")
        ))
      },
      success => fail("should have errored out")
    )
  }

  it should "fail when no other countries if hasOtherCountry = true" in {
    val js = Map (
      "nationality.british" -> "true",
      "nationality.irish" -> "true",
      "nationality.hasOtherCountry" -> "true",
      "nationality.otherCountries[0]" -> "",
      "nationality.otherCountries[1]" -> "",
      "nationality.otherCountries[2]" -> ""
    )
    nationalityForm.bind(js).fold (
      hasErrors => {
        hasErrors.keyedErrorsAsMap should matchMap(Map(
          "nationality.otherCountries" -> Seq("ordinary_nationality_error_pleaseAnswer")
        ))
      },
      success => fail("should have errored out")
    )
  }

  it should "accept other countries provided if hasOtherCountry = false" in {
    val js = Map (
      "nationality.british" -> "true",
      "nationality.irish" -> "true",
      "nationality.otherCountries[0]" -> "Spain",
      "nationality.otherCountries[1]" -> "",
      "nationality.otherCountries[2]" -> ""
    )
    nationalityForm.bind(js).fold (
      hasErrors => fail(hasErrors.prettyPrint.mkString(", ")),
      success => {
        val Some(nationality) = success.nationality
        nationality should have(
          'british (Some(true)),
          'irish (Some(true)),
          'hasOtherCountry (Some(true)),
          'otherCountries (List("Spain"))
        )
      }
    )
  }

  it should "ignore empty otherCountry fields if hasOtherCountry = false" in {
    val js = Map (
      "nationality.british" -> "true",
      "nationality.irish" -> "true",
      "nationality.otherCountries[0]" -> "",
      "nationality.otherCountries[1]" -> "",
      "nationality.otherCountries[2]" -> ""
    )
    nationalityForm.bind(js).fold (
      hasErrors => fail(hasErrors.prettyPrint.mkString(", ")),
      success => {
        val Some(nationality) = success.nationality
        nationality should have(
          'british (Some(true)),
          'irish (Some(true)),
          'hasOtherCountry (Some(false)),
          'otherCountries (List.empty)
        )
      }
    )
  }
}
