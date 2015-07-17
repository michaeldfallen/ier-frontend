package assets.mustache.overseas

import uk.gov.gds.ier.transaction.overseas.passport.CitizenDetailsMustache
import uk.gov.gds.ier.test._

class CitizenDetailsTemplateTest
  extends TemplateTestSuite
  with CitizenDetailsMustache {

  val data = new CitizenDetailsModel(
    question = Question(),
    hasPassport = Field(
      id = "hasPassportId",
      name = "hasPassportName",
      value = "hasPassportValue",
      classes = "hasPassportClasses"
    ),
    bornInUk = Field(
      id = "bornInUkId",
      name = "bornInUkName",
      value = "bornInUkValue",
      classes = "bornInUkClasses"
    ),
    citizenDate = Field(
      id = "citizenDateId",
      name = "citizenDateName",
      value = "citizenDateValue",
      classes = "citizenDateClasses"
    ),
    citizenDateDay = Field(
      id = "citizenDateDayId",
      name = "citizenDateDayName",
      value = "citizenDateDayValue",
      classes = "citizenDateDayClasses"
    ),
    citizenDateMonth = Field(
      id = "citizenDateMonthId",
      name = "citizenDateMonthName",
      value = "citizenDateMonthValue",
      classes = "citizenDateMonthClasses"
    ),
    citizenDateYear = Field(
      id = "citizenDateYearId",
      name = "citizenDateYearName",
      value = "citizenDateYearValue",
      classes = "citizenDateYearClasses"
    ),
    howBecameCitizen = Field(
      id = "howBecameCitizenId",
      name = "howBecameCitizenName",
      value = "howBecameCitizenValue",
      classes = "howBecameCitizenClasses"
    ),
    birthplace = Field(
      id = "birthplaceId",
      name = "birthplaceName",
      value = "birthplaceValue",
      classes = "birthplaceClasses"
    )
  )

  it should "properly render all properties from the model" in {
    running(FakeApplication()) {

      val html = Mustache.render("overseas/citizenDetails", data)
      val doc = Jsoup.parse(html.toString)

      val docFieldset = doc.select("form").first()

      val hasPassport = doc.select("input[id=hasPassportId]").first()
      hasPassport.attr("id") should be("hasPassportId")
      hasPassport.attr("name") should be("hasPassportName")
      hasPassport.attr("value") should be("hasPassportValue")

      val bornInUk = doc.select("input[id=bornInUkId]").first()
      bornInUk.attr("id") should be("bornInUkId")
      bornInUk.attr("name") should be("bornInUkName")
      bornInUk.attr("value") should be("bornInUkValue")

      docFieldset
        .select("label[for=howBecameCitizenId]")
        .first()
        .attr("for") should be ("howBecameCitizenId")

      val howBecameCitizenWrapper = doc.select("div[class*=howBecameCitizenClasses]").first()
      howBecameCitizenWrapper.attr("class") should include("howBecameCitizenClasses")

      val howBecameCitizenInput = howBecameCitizenWrapper.select("input").first()
      howBecameCitizenInput.attr("id") should be("howBecameCitizenId")
      howBecameCitizenInput.attr("name") should be("howBecameCitizenName")
      howBecameCitizenInput.attr("value") should be("howBecameCitizenValue")
      howBecameCitizenInput.attr("class") should include("howBecameCitizenClasses")

      val citizenDateFieldset = doc.select("fieldset[id=citizenDateId").first()
      citizenDateFieldset.attr("id") should be("citizenDateId")
      citizenDateFieldset.attr("class") should include("citizenDateClasses")

      val citizenDateDaySpan = citizenDateFieldset.select("span[class*=day]").first()
      val citizenDateDayLabel = citizenDateDaySpan.select("label").first()
      citizenDateDayLabel.attr("for") should be("citizenDateDayId")

      val citizenDateDayInput = citizenDateDaySpan.select("input").first()
      citizenDateDayInput.attr("id") should be("citizenDateDayId")
      citizenDateDayInput.attr("name") should be("citizenDateDayName")
      citizenDateDayInput.attr("value") should be("citizenDateDayValue")
      citizenDateDayInput.attr("class") should include("citizenDateDayClasses")

      val citizenDateMonthSpan = citizenDateFieldset.select("span[class*=month]").first()
      val citizenDateMonthLabel = citizenDateMonthSpan.select("label").first()
      citizenDateMonthLabel.attr("for") should be("citizenDateMonthId")

      val citizenDateMonthInput = citizenDateMonthSpan.select("input").first()
      citizenDateMonthInput.attr("id") should be("citizenDateMonthId")
      citizenDateMonthInput.attr("name") should be("citizenDateMonthName")
      citizenDateMonthInput.attr("value") should be("citizenDateMonthValue")
      citizenDateMonthInput.attr("class") should include("citizenDateMonthClasses")

      val citizenDateYearSpan = citizenDateFieldset.select("span[class*=year]").first()
      val citizenDateYearLabel = citizenDateYearSpan.select("label").first()
      citizenDateYearLabel.attr("for") should be("citizenDateYearId")

      val citizenDateYearInput = citizenDateYearSpan.select("input").first()
      citizenDateYearInput.attr("id") should be("citizenDateYearId")
      citizenDateYearInput.attr("name") should be("citizenDateYearName")
      citizenDateYearInput.attr("value") should be("citizenDateYearValue")
      citizenDateYearInput.attr("class") should include("citizenDateYearClasses")

      val birthplaceWrapper = doc.select("div[class*=birthplaceClasses]").first()
      birthplaceWrapper.attr("class") should include("birthplaceClasses")

      val birthplaceInput = birthplaceWrapper.select("input").first()
      birthplaceInput.attr("id") should be("birthplaceId")
      birthplaceInput.attr("name") should be("birthplaceName")
      birthplaceInput.attr("value") should be("birthplaceValue")
      birthplaceInput.attr("class") should include("birthplaceClasses")
    }
  }
}
