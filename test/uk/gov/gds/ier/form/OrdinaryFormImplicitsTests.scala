package uk.gov.gds.ier.form

import uk.gov.gds.ier.test.UnitTestSuite
import uk.gov.gds.ier.validation.FormKeys
import uk.gov.gds.ier.transaction.ordinary.nationality.NationalityForms

class OrdinaryFormImplicitsTests extends UnitTestSuite {

  val implicits = new OrdinaryFormImplicits with FormKeys {}
  import implicits._

  val forms = new NationalityForms {}
  val serialiser = jsonSerialiser

  behavior of "OrdinaryFormImplicits"
  it should "generate errors if non valid country is provided" in {
    val value = Map(
        "nationality.british" -> "true",
        "nationality.irish" -> "true",
        "nationality.hasOtherCountry" -> "true",
        "nationality.otherCountries[0]" -> "country 1")

    forms.nationalityForm.bind(value).fold(
      hasErrors => {
        hasErrors.keyedErrorsAsMap should matchMap(
          Map("nationality.otherCountries[0]" -> Seq("ordinary_nationality_error_notValid")))
      },
      success => fail("Should have thrown an error")
    )
  }

    it should "generate errors if more than " in {
    val value = Map(
        "nationality.hasOtherCountry" -> "true",
        "nationality.otherCountries[0]" -> "China",
        "nationality.otherCountries[1]" -> "Canada",
        "nationality.otherCountries[2]" -> "Australia",
        "nationality.otherCountries[3]" -> "New Zealand",
        "nationality.otherCountries[4]" -> "France",
        "nationality.otherCountries[5]" -> "Spain")

    forms.nationalityForm.bind(value).fold(
      hasErrors => {
        hasErrors.keyedErrorsAsMap should matchMap(Map(
          "nationality.otherCountries" -> Seq("ordinary_nationality_error_noMoreFiveCountries")
        ))
      },
      success => fail("Should have thrown an error")
    )
  }
  it should "bind successfully if less than 6 other countries provided are valid" in {
    val value = Map(
        "nationality.british" -> "true",
        "nationality.irish" -> "true",
        "nationality.hasOtherCountry" -> "true",
        "nationality.otherCountries[0]" -> "China",
        "nationality.otherCountries[1]" -> "Canada",
        "nationality.otherCountries[2]" -> "Australia")

    forms.nationalityForm.bind(value).fold(
      hasErrors => fail(serialiser.toJson(hasErrors.prettyPrint)),
      success => {
        val otherCountries = success.nationality.map(_.otherCountries).getOrElse(List(Nil))
        otherCountries.size should be(3)
        otherCountries(0) should be ("China")
        otherCountries(1) should be ("Canada")
        otherCountries(2) should be ("Australia")
      }
    )
  }
}
