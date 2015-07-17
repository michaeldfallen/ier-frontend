package assets.mustache.forces

import uk.gov.gds.ier.transaction.forces.service.ServiceMustache
import uk.gov.gds.ier.test._

class ServiceTemplateTest
  extends TemplateTestSuite
  with ServiceMustache {

  it should "properly render all properties from the model" in {

    running(FakeApplication()) {
      val data = ServiceModel(
        question = Question(postUrl = "/whatever-url",
        number = "7",
        title = "service title"
        ),
        serviceFieldSet = FieldSet(
          classes = "serviceFieldSetClasses"
        ),
        royalNavy = Field(
          id = "royalNavyId",
          name = "royalNavyName",
          attributes = "foo=\"foo\""
        ),
        britishArmy = Field(
          id = "britishArmyId",
          name = "britishArmyName",
          attributes = "foo=\"foo\""
        ),
        royalAirForce = Field(
          id = "royalAirForceId",
          name = "royalAirForceName",
          attributes = "foo=\"foo\""
        ),
        regiment = Field(
          id = "regimentId",
          name = "regimentName",
          value = "regimentValue"
        ),
        regimentShowFlag = Text(
          value = "regimentShowFlag"
        )
      )

      val html = Mustache.render("forces/service", data)
      val doc = Jsoup.parse(html.toString)

      val serviceFieldset = doc.select("fieldSet[data-validation-name=service]").first()
      serviceFieldset.attr("class") should include ("serviceFieldSetClasses")

      val royalNavyInput = doc.select("input[id=royalNavyId]").first()
      royalNavyInput.attr("id") should be("royalNavyId")
      royalNavyInput.attr("name") should be("royalNavyName")
      royalNavyInput.attr("foo") should be("foo")

      val britishArmyInput = doc.select("input[id=britishArmyId]").first()
      britishArmyInput.attr("id") should be("britishArmyId")
      britishArmyInput.attr("name") should be("britishArmyName")
      britishArmyInput.attr("foo") should be("foo")

      val royalAirForceInput = doc.select("input[id=royalAirForceId]").first()
      royalAirForceInput.attr("id") should be("royalAirForceId")
      royalAirForceInput.attr("name") should be("royalAirForceName")
      royalAirForceInput.attr("foo") should be("foo")

      val regimentInput = doc.select("input[id=regimentId]").first()
      regimentInput.attr("id") should be("regimentId")
      regimentInput.attr("name") should be("regimentName")
      regimentInput.attr("value") should be("regimentValue")

      val regimentShowFlagDiv = doc.select("div[data-condition=britishArmyId]").first()
      regimentShowFlagDiv.attr("class") should include ("optional-sectionregimentShowFlag")

    }
  }
}
