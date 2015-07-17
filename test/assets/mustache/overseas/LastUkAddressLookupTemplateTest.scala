package assets.mustache.overseas

import uk.gov.gds.ier.transaction.overseas.lastUkAddress.LastUkAddressLookupMustache
import uk.gov.gds.ier.test._

class LastUkAddressLookupTemplateTest
  extends TemplateTestSuite
  with LastUkAddressLookupMustache {

  it should "properly render" in {
    running(FakeApplication()) {
      val data = new LookupModel(
        question = Question(),
        postcode = Field(
          id = "postcodeId",
          name = "postcodeName",
          classes = "postcodeClasses",
          value = "postcodeValue"
        )
      )

      val html = Mustache.render("overseas/lastUkAddressLookup", data)
      val doc = Jsoup.parse(html.toString)

      val fieldset = doc.select("fieldset").first()

      val label = fieldset.select("label").first()
      label.attr("for") should be("postcodeId")

      val divWrapper = fieldset.select("div").first()
      divWrapper.attr("class") should include("postcodeClasses")

      val input = divWrapper.select("input").first()
      input.attr("id") should be("postcodeId")
      input.attr("name") should be("postcodeName")
      input.attr("value") should be("postcodeValue")
      input.attr("class") should include("postcodeClasses")
    }
  }
}
