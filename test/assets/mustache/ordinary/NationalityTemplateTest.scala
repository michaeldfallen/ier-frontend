package assets.mustache.ordinary

import uk.gov.gds.ier.transaction.ordinary.nationality.NationalityMustache
import uk.gov.gds.ier.test._

class NationalityTemplateTest
  extends TemplateTestSuite
  with NationalityMustache {

  it should "properly render all properties from the model" in {

    running(FakeApplication()) {
      val data = NationalityModel(
        question = Question(postUrl = "/whatever-url",
        number = "1",
        title = "nationality title"
        ),
        nationality = FieldSet("nationalityClass"),
        britishOption = Field(
          id = "britishOptionId",
          name = "britishOptionName",
          attributes = "foo=\"foo\""
        ),
        irishOption = Field(
          id = "irishOptionId",
          name = "irishOptionName",
          attributes = "foo=\"foo\""
        ),
        hasOtherCountryOption = Field(
          id = "hasOtherCountryOptionId",
          name = "hasOtherCountryOptionName",
          attributes = "foo=\"foo\""
        ),
        otherCountry = FieldSet("otherCountryClass"),
        otherCountries0 = Field(
          id = "otherCountries0Id",
          name = "otherCountries0Name",
          value = "otherCountries0Value",
          classes = "otherCountries0Class"
        ),
        otherCountries1 = Field(
          id = "otherCountries1Id",
          name = "otherCountries1Name",
          value = "otherCountries1Value",
          classes = "otherCountries1Class"
        ),
        otherCountries2 = Field(
          id = "otherCountries2Id",
          name = "otherCountries2Name",
          value = "otherCountries2Value",
          classes = "otherCountries2Class"
        ),
        noNationalityReason = Field (
          id = "noNationalityReasonId",
          name = "noNationalityReasonName",
          value = "noNationalityReasonValue"
        ),
        noNationalityReasonShowFlag = "noNationalityReasonShowFlag"
      )

      val html = Mustache.render("ordinary/nationality", data)
      val doc = Jsoup.parse(html.toString)

      val nationalityFieldSet = doc.select("fieldset").first()
      nationalityFieldSet.attr("class") should include("nationalityClass")

      val britishOptionInput = doc.select("input[id=britishOptionId]").first()
      britishOptionInput.attr("id") should be("britishOptionId")
      britishOptionInput.attr("name") should be("britishOptionName")
      britishOptionInput.attr("foo") should be("foo")

      val irishOptionInput = doc.select("input[id=irishOptionId]").first()
      irishOptionInput.attr("id") should be("irishOptionId")
      irishOptionInput.attr("name") should be("irishOptionName")
      irishOptionInput.attr("foo") should be("foo")

      val otherCountryValidation = doc.select("div").first()
      otherCountryValidation.attr("class") should include("otherCountryClass")

      val otherCountry0Label = doc.select("label[for=otherCountries0Id]").first()
      otherCountry0Label.attr("for") should be("otherCountries0Id")
      val otherCountry0Input = doc.select("input[id=otherCountries0Id]").first()
      otherCountry0Input.attr("id") should be("otherCountries0Id")
      otherCountry0Input.attr("name") should be("otherCountries0Name")
      otherCountry0Input.attr("value") should be("otherCountries0Value")
      otherCountry0Input.attr("class") should include("otherCountries0Class")

      val otherCountry1Label = doc.select("label[for=otherCountries1Id]").first()
      otherCountry1Label.attr("for") should be("otherCountries1Id")
      val otherCountry1Input = doc.select("input[id=otherCountries1Id]").first()
      otherCountry1Input.attr("id") should be("otherCountries1Id")
      otherCountry1Input.attr("name") should be("otherCountries1Name")
      otherCountry1Input.attr("value") should be("otherCountries1Value")
      otherCountry1Input.attr("class") should include("otherCountries1Class")

      val otherCountry2Label = doc.select("label[for=otherCountries2Id]").first()
      otherCountry2Label.attr("for") should be("otherCountries2Id")
      val otherCountry2Input = doc.select("input[id=otherCountries2Id]").first()
      otherCountry2Input.attr("id") should be("otherCountries2Id")
      otherCountry2Input.attr("name") should be("otherCountries2Name")
      otherCountry2Input.attr("value") should be("otherCountries2Value")
      otherCountry2Input.attr("class") should include("otherCountries2Class")
    }
  }
}
