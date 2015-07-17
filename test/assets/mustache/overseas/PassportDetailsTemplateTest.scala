package assets.mustache.overseas

import uk.gov.gds.ier.transaction.overseas.passport.PassportDetailsMustache
import uk.gov.gds.ier.test._

class PassportDetailsTemplateTest
  extends TemplateTestSuite
  with PassportDetailsMustache {

  val data = new PassportDetailsModel(
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
    passportNumber = Field(
      id = "passportNumberId",
      name = "passportNumberName",
      value = "passportNumberValue",
      classes = "passportNumberClasses"
    ),
    authority = Field(
      id = "authorityId",
      name = "authorityName",
      value = "authorityValue",
      classes = "authorityClasses"
    ),
    issueDate = Field(
      id = "issueDateId",
      classes = "issueDateClasses"
    ),
    issueDateDay = Field(
      id = "issueDateDayId",
      name = "issueDateDayName",
      value = "issueDateDayValue",
      classes = "issueDateDayClasses"
    ),
    issueDateMonth = Field(
      id = "issueDateMonthId",
      name = "issueDateMonthName",
      value = "issueDateMonthValue",
      classes = "issueDateMonthClasses"
    ),
    issueDateYear = Field(
      id = "issueDateYearId",
      name = "issueDateYearName",
      value = "issueDateYearValue",
      classes = "issueDateYearClasses"
    )
  )

  it should "properly render all properties from the model" in {
    running(FakeApplication()) {

      val html = Mustache.render("overseas/passportDetails", data)
      val doc = Jsoup.parse(html.toString)

      val docFieldset = doc.select("fieldset").first()

      val hasPassport = doc.select("input[id=hasPassportId]").first()
      hasPassport.attr("id") should be("hasPassportId")
      hasPassport.attr("name") should be("hasPassportName")
      hasPassport.attr("value") should be("hasPassportValue")

      val bornInUk = doc.select("input[id=bornInUkId]").first()
      bornInUk.attr("id") should be("bornInUkId")
      bornInUk.attr("name") should be("bornInUkName")
      bornInUk.attr("value") should be("bornInUkValue")

      docFieldset
        .select("label[for=passportNumberId]")
        .first()
        .attr("for") should be ("passportNumberId")

      val passportNumberWrapper = doc.select("div[class*=passportNumberClasses]").first()
      passportNumberWrapper.attr("class") should include("passportNumberClasses")

      val passportNumberInput = passportNumberWrapper.select("input").first()
      passportNumberInput.attr("id") should be("passportNumberId")
      passportNumberInput.attr("name") should be("passportNumberName")
      passportNumberInput.attr("value") should be("passportNumberValue")
      passportNumberInput.attr("class") should include("passportNumberClasses")

      docFieldset
        .select("label[for=authorityId]")
        .first()
        .attr("for") should be ("authorityId")

      val authorityWrapper = doc.select("div[class*=authorityClasses]").first()
      authorityWrapper.attr("class") should include("authorityClasses")

      val authorityInput = authorityWrapper.select("input").first()
      authorityInput.attr("id") should be("authorityId")
      authorityInput.attr("name") should be("authorityName")
      authorityInput.attr("value") should be("authorityValue")
      authorityInput.attr("class") should include("authorityClasses")

      val issueDateFieldset = doc.select("fieldset[id=issueDateId").first()
      issueDateFieldset.attr("id") should be("issueDateId")
      issueDateFieldset.attr("class") should include("issueDateClasses")

      val issueDateDaySpan = issueDateFieldset.select("span[class*=day]").first()
      val issueDateDayLabel = issueDateDaySpan.select("label").first()
      issueDateDayLabel.attr("for") should be("issueDateDayId")

      val issueDateDayInput = issueDateDaySpan.select("input").first()
      issueDateDayInput.attr("id") should be("issueDateDayId")
      issueDateDayInput.attr("name") should be("issueDateDayName")
      issueDateDayInput.attr("value") should be("issueDateDayValue")
      issueDateDayInput.attr("class") should include("issueDateDayClasses")

      val issueDateMonthSpan = issueDateFieldset.select("span[class*=month]").first()
      val issueDateMonthLabel = issueDateMonthSpan.select("label").first()
      issueDateMonthLabel.attr("for") should be("issueDateMonthId")

      val issueDateMonthInput = issueDateMonthSpan.select("input").first()
      issueDateMonthInput.attr("id") should be("issueDateMonthId")
      issueDateMonthInput.attr("name") should be("issueDateMonthName")
      issueDateMonthInput.attr("value") should be("issueDateMonthValue")
      issueDateMonthInput.attr("class") should include("issueDateMonthClasses")

      val issueDateYearSpan = issueDateFieldset.select("span[class*=year]").first()
      val issueDateYearLabel = issueDateYearSpan.select("label").first()
      issueDateYearLabel.attr("for") should be("issueDateYearId")

      val issueDateYearInput = issueDateYearSpan.select("input").first()
      issueDateYearInput.attr("id") should be("issueDateYearId")
      issueDateYearInput.attr("name") should be("issueDateYearName")
      issueDateYearInput.attr("value") should be("issueDateYearValue")
      issueDateYearInput.attr("class") should include("issueDateYearClasses")
    }
  }
}
