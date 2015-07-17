package assets.mustache.crown

import uk.gov.gds.ier.test._
import uk.gov.gds.ier.transaction.crown.address.AddressLookupMustache

class AddressLookupTemplateTest
  extends TemplateTestSuite
  with AddressLookupMustache {

  it should "properly render" in {
    running(FakeApplication()) {
      val data = new LookupModel(
        question = Question(),
        postcode = Field(
          id = "postcodeId",
          name = "postcodeName",
          classes = "postcodeClasses",
          value = "postcodeValue"
        ),
        hasUkAddress = Field(
          id = "hasUkAddressId",
          name = "hasUkAddressName",
          value = "hasUkAddressValue"
        )
      )

      val html = Mustache.render("crown/addressLookup", data)
      val doc = Jsoup.parse(html.toString)

      val fieldset = doc.select("fieldset").first()

      val label = fieldset.select("label").first()
      label.attr("for") should be("postcodeId")

      val divWrapper = fieldset.select("div").first()
      divWrapper.attr("class") should include("postcodeClasses")

      val postcodeInput = divWrapper.select("input#postcodeId").first()
      postcodeInput should not be(null)
      postcodeInput.attr("name") should be("postcodeName")
      postcodeInput.attr("value") should be("postcodeValue")
      postcodeInput.attr("class") should include("postcodeClasses")

      val hasUkAddressInput = doc.select("input[id=hasUkAddressId]").first()
      hasUkAddressInput should not be(null)
      hasUkAddressInput.attr("name") should be("hasUkAddressName")
      hasUkAddressInput.attr("value") should be("hasUkAddressValue")
    }
  }
}
