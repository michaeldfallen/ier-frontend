package uk.gov.gds.ier.transaction.crown.nationality

import uk.gov.gds.ier.test.FormTestSuite

class NationalityFormTests
  extends FormTestSuite
  with NationalityForms {

  it should "succesfully bind json" in {
    val js = JsObject(
      Seq(
        "nationality.british" -> JsBoolean(true),
        "nationality.irish" -> JsBoolean(true),
        "nationality.otherCountries" -> Json.toJson(List("Italy", "France")),
        "nationality.hasOtherCountry" -> JsBoolean(true)
      )
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
    val js = JsObject(
      Seq(
        "nationality.british" -> JsBoolean(true),
        "nationality.irish" -> JsBoolean(true)
      )
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
    val js = JsObject(Seq(
        "nationality.british" -> JsBoolean(true),
        "nationality.irish" -> JsBoolean(true),
        "nationality.otherCountries" -> Json.toJson(List("Italy", "France")),
        "nationality.hasOtherCountry" -> JsBoolean(true)
      )
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
        success.contactAddress should be(None)
        success.postalOrProxyVote should be(None)

      }
    )
  }

  it should "handle no nationality or other country correctly" in {
    val js = Json.toJson(
      Map("nationality.noNationalityReason" -> "I don't have a nationality. I am stateless.")
    )
    nationalityForm.bind(js).fold(
      hasErrors => fail(hasErrors.prettyPrint.mkString(", ")),
      success => {
        val nationality = success.nationality.get
        nationality.hasOtherCountry should be(Some(false))
        nationality.british should be(Some(false))
        nationality.irish should be(Some(false))
        nationality.otherCountries should be(List.empty)
        nationality.noNationalityReason should be(Some("I don't have a nationality. I am stateless."))
      }
    )
  }

  it should "error out on empty json" in {
    val js = JsNull
    nationalityForm.bind(js).fold(
      hasErrors => {
        hasErrors.errorMessages("nationality").head should be("Please answer this question")
        hasErrors.globalErrorMessages.head should be("Please answer this question")
        hasErrors.errors.size should be(2)
      },
      success => fail("Should have errored out.")
    )
  }
}
