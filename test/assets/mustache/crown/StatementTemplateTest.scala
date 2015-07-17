package assets.mustache.crown

import uk.gov.gds.ier.transaction.crown.statement.StatementMustache
import uk.gov.gds.ier.test._

class StatementTemplateTest
  extends TemplateTestSuite
  with StatementMustache {

  it should "properly render all properties from the model" in {
    running(FakeApplication()) {
      val data = StatementModel(
        question = Question(),
        crown = Field(
          id = "crownId",
          classes = "crownClasses"
        ),
        crownServant = Field(
          id = "crownServantId",
          name = "crownServantName",
          classes = "crownServantClasses",
          value = "crownServantValue",
          attributes = "foo=\"foo\""
        ),
        crownPartner = Field(
          id = "crownPartnerId",
          name = "crownPartnerName",
          classes = "crownPartnerClasses",
          value = "crownPartnerValue",
          attributes = "foo=\"foo\""
        ),
        council = Field(
          id = "councilId",
          classes = "councilClasses"
        ),
        councilEmployee = Field(
          id = "councilEmployeeId",
          name = "councilEmployeeName",
          classes = "councilEmployeeClasses",
          value = "councilEmployeeValue",
          attributes = "foo=\"foo\""
        ),
        councilPartner = Field(
          id = "councilPartnerId",
          name = "councilPartnerName",
          classes = "councilPartnerClasses",
          value = "councilPartnerValue",
          attributes = "foo=\"foo\""
        )
      )

      val html = Mustache.render("crown/statement", data)
      val doc = Jsoup.parse(html.toString)

      val crownFieldset = doc.select("fieldset[id=crownId]").first()
      crownFieldset should not be(null)
      crownFieldset.attr("id") should be("crownId")
      crownFieldset.attr("class") should include("crownClasses")

      val crownServantDivWrapper = crownFieldset.select("div").first()
      crownServantDivWrapper.attr("class") should include("crownServantClasses")

      val crownServantLabel = crownFieldset.select("label[for=crownServantId]").first()
      crownServantLabel should not be(null)
      crownServantLabel.attr("for") should be("crownServantId")

      val crownServantInput = crownServantLabel.select("input").first()
      crownServantInput should not be(null)
      crownServantInput.attr("id") should be("crownServantId")
      crownServantInput.attr("name") should be("crownServantName")
      crownServantInput.attr("value") should be("crownServantValue")
      crownServantInput.attr("foo") should be("foo")

      val crownPartnerDivWrapper = crownFieldset.select("div").get(1)
      crownPartnerDivWrapper.attr("class") should include("crownPartnerClasses")

      val crownPartnerLabel = crownFieldset.select("label[for=crownPartnerId]").first()
      crownPartnerLabel should not be(null)
      crownPartnerLabel.attr("for") should be("crownPartnerId")

      val crownPartnerInput = crownPartnerLabel.select("input").first()
      crownPartnerInput should not be(null)
      crownPartnerInput.attr("id") should be("crownPartnerId")
      crownPartnerInput.attr("name") should be("crownPartnerName")
      crownPartnerInput.attr("value") should be("crownPartnerValue")
      crownPartnerInput.attr("foo") should be("foo")


      val councilFieldset = doc.select("fieldset[id=councilId]").first()
      councilFieldset should not be(null)
      councilFieldset.attr("id") should be("councilId")
      councilFieldset.attr("class") should include("councilClasses")

      val councilEmployeeDivWrapper = councilFieldset.select("div").first()
      councilEmployeeDivWrapper.attr("class") should include("councilEmployeeClasses")

      val councilEmployeeLabel = councilFieldset.select("label[for=councilEmployeeId]").first()
      councilEmployeeLabel should not be(null)
      councilEmployeeLabel.attr("for") should be("councilEmployeeId")

      val councilEmployeeInput = councilEmployeeLabel.select("input").first()
      councilEmployeeInput should not be(null)
      councilEmployeeInput.attr("id") should be("councilEmployeeId")
      councilEmployeeInput.attr("name") should be("councilEmployeeName")
      councilEmployeeInput.attr("value") should be("councilEmployeeValue")
      councilEmployeeInput.attr("foo") should be("foo")

      val councilPartnerDivWrapper = councilFieldset.select("div").get(1)
      councilPartnerDivWrapper.attr("class") should include("councilPartnerClasses")

      val councilPartnerLabel = councilFieldset.select("label[for=councilPartnerId]").first()
      councilPartnerLabel should not be(null)
      councilPartnerLabel.attr("for") should be("councilPartnerId")

      val councilPartnerInput = councilPartnerLabel.select("input").first()
      councilPartnerInput should not be(null)
      councilPartnerInput.attr("id") should be("councilPartnerId")
      councilPartnerInput.attr("name") should be("councilPartnerName")
      councilPartnerInput.attr("value") should be("councilPartnerValue")
      councilPartnerInput.attr("foo") should be("foo")
    }
  }
}
