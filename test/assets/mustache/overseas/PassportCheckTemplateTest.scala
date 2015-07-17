package assets.mustache.overseas

import uk.gov.gds.ier.transaction.overseas.passport.PassportCheckMustache
import uk.gov.gds.ier.test._

class PassportCheckTemplateTest
  extends TemplateTestSuite
  with PassportCheckMustache {

  val data = new PassportCheckModel(
    question = Question(),
    hasPassport = Field(
      classes = "hasPassportClasses"
    ),
    hasPassportTrue = Field(
      id = "hasPassportTrueId",
      name = "hasPassportTrueName",
      classes = "hasPassportTrueClasses",
      attributes = "foo=\"foo\""
    ),
    hasPassportFalse = Field(
      id = "hasPassportFalseId",
      name = "hasPassportFalseName",
      classes = "hasPassportFalseClasses",
      attributes = "foo=\"foo\""
    ),
    bornInUk = Field(
      classes = "bornInUkClasses"
    ),
    bornInUkTrue = Field(
      id = "bornInUkTrueId",
      name = "bornInUkTrueName",
      classes = "bornInUkTrueClasses",
      attributes = "foo=\"foo\""
    ),
    bornInUkFalse = Field(
      id = "bornInUkFalseId",
      name = "bornInUkFalseName",
      classes = "bornInUkFalseClasses",
      attributes = "foo=\"foo\""
    )
  )

  it should "properly render all properties from the model" in {
    running(FakeApplication()) {

      val html = Mustache.render("overseas/passportCheck", data)
      val doc = Jsoup.parse(html.toString)

      val passportFieldset = doc.select("fieldset[class*=hasPassportClasses]").first()
      passportFieldset.attr("class") should include("hasPassportClasses")

      val passportTrueLabel = passportFieldset.select("label[for=hasPassportTrueId]").first()
      passportTrueLabel.attr("for") should be("hasPassportTrueId")

      val passportTrueInput = passportTrueLabel.select("input").first()
      passportTrueInput.attr("id") should be("hasPassportTrueId")
      passportTrueInput.attr("name") should be("hasPassportTrueName")
      passportTrueInput.attr("foo") should be("foo")

      val passportFalseLabel = passportFieldset.select("label[for=hasPassportFalseId]").first()
      passportFalseLabel.attr("for") should be("hasPassportFalseId")

      val passportFalseInput = passportFalseLabel.select("input").first()
      passportFalseInput.attr("id") should be("hasPassportFalseId")
      passportFalseInput.attr("name") should be("hasPassportFalseName")
      passportFalseInput.attr("foo") should be("foo")


      val bornFieldset = doc.select("fieldset[class*=bornInUkClasses]").first()
      bornFieldset.attr("class") should include("bornInUkClasses")

      val bornTrueLabel = bornFieldset.select("label[for=bornInUkTrueId]").first()
      bornTrueLabel.attr("for") should be("bornInUkTrueId")

      val bornTrueInput = bornTrueLabel.select("input").first()
      bornTrueInput.attr("id") should be("bornInUkTrueId")
      bornTrueInput.attr("name") should be("bornInUkTrueName")
      bornTrueInput.attr("foo") should be("foo")

      val bornFalseLabel = bornFieldset.select("label[for=bornInUkFalseId]").first()
      bornFalseLabel.attr("for") should be("bornInUkFalseId")

      val bornFalseInput = bornFalseLabel.select("input").first()
      bornFalseInput.attr("id") should be("bornInUkFalseId")
      bornFalseInput.attr("name") should be("bornInUkFalseName")
      bornFalseInput.attr("foo") should be("foo")
    }
  }
}
