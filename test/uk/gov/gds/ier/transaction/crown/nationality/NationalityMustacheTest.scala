package uk.gov.gds.ier.transaction.crown.nationality

import uk.gov.gds.ier.test._
import uk.gov.gds.ier.model.{PartialNationality}
import uk.gov.gds.ier.transaction.crown.InprogressCrown

class NationalityMustacheTest
  extends MustacheTestSuite
  with NationalityForms
  with NationalityMustache {

  it should "empty progress form should produce empty Model" in {
    val emptyApplicationForm = nationalityForm
    val nationalityModel = mustache.data(
      emptyApplicationForm,
      Call("POST", "/register-to-vote/crown/nationality"),
      InprogressCrown()
    ).asInstanceOf[NationalityModel]

    nationalityModel.question.title should be("What is your nationality?")
    nationalityModel.question.postUrl should be("/register-to-vote/crown/nationality")

    nationalityModel.britishOption.attributes should be("")
    nationalityModel.irishOption.attributes should be("")
    nationalityModel.hasOtherCountryOption.attributes should be("")
    nationalityModel.otherCountries0.value should be("")
    nationalityModel.otherCountries1.value should be("")
    nationalityModel.otherCountries2.value should be("")
    nationalityModel.noNationalityReason.value should be("")
    nationalityModel.noNationalityReasonShowFlag should be("")


  }

  it should "progress form with british option should produce Mustache Model with values present" in {

    val partiallyFilledApplication = InprogressCrown(
      nationality = Some(PartialNationality(
        british = Some(true),
        irish = None,
        hasOtherCountry = None,
        otherCountries = List.empty,
        noNationalityReason = None)))

    val partiallyFilledApplicationForm = nationalityForm.fill(partiallyFilledApplication)

    val nationalityModel = mustache.data(
      partiallyFilledApplicationForm,
      Call("POST", "/register-to-vote/crown/nationality"),
      InprogressCrown()
    ).asInstanceOf[NationalityModel]

    nationalityModel.question.title should be("What is your nationality?")
    nationalityModel.question.postUrl should be("/register-to-vote/crown/nationality")

    nationalityModel.britishOption.attributes should be("checked=\"checked\"")
    nationalityModel.irishOption.attributes should be("")
    nationalityModel.hasOtherCountryOption.attributes should be("")
    nationalityModel.otherCountries0.value should be("")
    nationalityModel.otherCountries1.value should be("")
    nationalityModel.otherCountries2.value should be("")
    nationalityModel.noNationalityReason.value should be("")
    nationalityModel.noNationalityReasonShowFlag should be("")
  }

  it should "progress form with irish option should produce Mustache Model with values present" in {

    val partiallyFilledApplication = InprogressCrown(
      nationality = Some(PartialNationality(
        british = None,
        irish = Some(true),
        hasOtherCountry = None,
        otherCountries = List.empty,
        noNationalityReason = None)))

    val partiallyFilledApplicationForm = nationalityForm.fill(partiallyFilledApplication)

    val nationalityModel = mustache.data(
      partiallyFilledApplicationForm,
      Call("POST", "/register-to-vote/crown/nationality"),
      InprogressCrown()
    ).asInstanceOf[NationalityModel]

    nationalityModel.question.title should be("What is your nationality?")
    nationalityModel.question.postUrl should be("/register-to-vote/crown/nationality")

    nationalityModel.britishOption.attributes should be("")
    nationalityModel.irishOption.attributes should be("checked=\"checked\"")
    nationalityModel.hasOtherCountryOption.attributes should be("")
    nationalityModel.otherCountries0.value should be("")
    nationalityModel.otherCountries1.value should be("")
    nationalityModel.otherCountries2.value should be("")
    nationalityModel.noNationalityReason.value should be("")
    nationalityModel.noNationalityReasonShowFlag should be("")
  }

  it should "progress form with other countries option should produce Mustache Model with values present" in {

    val partiallyFilledApplication = InprogressCrown(
      nationality = Some(PartialNationality(
        british = None,
        irish = None,
        hasOtherCountry = Some(true),
        otherCountries = List("Spain", "France"),
        noNationalityReason = None)))

    val partiallyFilledApplicationForm = nationalityForm.fill(partiallyFilledApplication)

    val nationalityModel = mustache.data(
      partiallyFilledApplicationForm,
      Call("POST", "/register-to-vote/crown/nationality"),
      InprogressCrown()
    ).asInstanceOf[NationalityModel]

    nationalityModel.question.title should be("What is your nationality?")
    nationalityModel.question.postUrl should be("/register-to-vote/crown/nationality")

    nationalityModel.britishOption.attributes should be("")
    nationalityModel.irishOption.attributes should be("")
    nationalityModel.hasOtherCountryOption.attributes should be("checked=\"checked\"")
    nationalityModel.otherCountries0.value should be("Spain")
    nationalityModel.otherCountries1.value should be("France")
    nationalityModel.otherCountries2.value should be("")
    nationalityModel.noNationalityReason.value should be("")
    nationalityModel.noNationalityReasonShowFlag should be("")
  }

  it should "progress form with other countries and british option should produce Mustache Model with values present" in {

    val partiallyFilledApplication = InprogressCrown(
      nationality = Some(PartialNationality(
        british = Some(true),
        irish = None,
        hasOtherCountry = Some(true),
        otherCountries = List("Spain", "France"),
        noNationalityReason = None)))

    val partiallyFilledApplicationForm = nationalityForm.fill(partiallyFilledApplication)

    val nationalityModel = mustache.data(
      partiallyFilledApplicationForm,
      Call("POST", "/register-to-vote/crown/nationality"),
      InprogressCrown()
    ).asInstanceOf[NationalityModel]

    nationalityModel.question.title should be("What is your nationality?")
    nationalityModel.question.postUrl should be("/register-to-vote/crown/nationality")

    nationalityModel.britishOption.attributes should be("checked=\"checked\"")
    nationalityModel.irishOption.attributes should be("")
    nationalityModel.hasOtherCountryOption.attributes should be("checked=\"checked\"")
    nationalityModel.otherCountries0.value should be("Spain")
    nationalityModel.otherCountries1.value should be("France")
    nationalityModel.otherCountries2.value should be("")
    nationalityModel.noNationalityReason.value should be("")
    nationalityModel.noNationalityReasonShowFlag should be("")
  }

  it should "progress form with validation errors should produce Model with error list present" in {

    val partiallyFilledApplication = InprogressCrown(
      nationality = Some(PartialNationality(
        british = None,
        irish = None,
        hasOtherCountry = None,
        otherCountries = List.empty,
        noNationalityReason = None)))

    val partiallyFilledApplicationForm = nationalityForm.fillAndValidate(partiallyFilledApplication)

    val nationalityModel = mustache.data(
      partiallyFilledApplicationForm,
      Call("POST", "/register-to-vote/crown/nationality"),
      InprogressCrown()
    ).asInstanceOf[NationalityModel]

    nationalityModel.question.title should be("What is your nationality?")
    nationalityModel.question.postUrl should be("/register-to-vote/crown/nationality")

    nationalityModel.britishOption.attributes should be("")
    nationalityModel.irishOption.attributes should be("")
    nationalityModel.hasOtherCountryOption.attributes should be("")
    nationalityModel.otherCountries0.value should be("")
    nationalityModel.otherCountries1.value should be("")
    nationalityModel.otherCountries2.value should be("")
    nationalityModel.noNationalityReason.value should be("")
    nationalityModel.noNationalityReasonShowFlag should be("")

    nationalityModel.question.errorMessages.mkString(", ") should be("Please answer this question")
  }

  it should "progress form with excuse should produce Mustache Model with values present" in {

    val partiallyFilledApplication = InprogressCrown(
      nationality = Some(PartialNationality(
        british = None,
        irish = None,
        hasOtherCountry = None,
        otherCountries = List.empty,
        noNationalityReason = Some("no nationality fake excuse"))))

    val partiallyFilledApplicationForm = nationalityForm.fill(partiallyFilledApplication)

    val nationalityModel = mustache.data(
      partiallyFilledApplicationForm,
      Call("POST", "/register-to-vote/crown/nationality"),
      InprogressCrown()
    ).asInstanceOf[NationalityModel]

    nationalityModel.question.title should be("What is your nationality?")
    nationalityModel.question.postUrl should be("/register-to-vote/crown/nationality")

    nationalityModel.britishOption.attributes should be("")
    nationalityModel.irishOption.attributes should be("")
    nationalityModel.hasOtherCountryOption.attributes should be("")
    nationalityModel.otherCountries0.value should be("")
    nationalityModel.otherCountries1.value should be("")
    nationalityModel.otherCountries2.value should be("")
    nationalityModel.noNationalityReason.value should be("no nationality fake excuse")
    nationalityModel.noNationalityReasonShowFlag should be("-open")
  }
}
