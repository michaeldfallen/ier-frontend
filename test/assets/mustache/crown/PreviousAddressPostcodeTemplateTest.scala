package assets.mustache.crown

import uk.gov.gds.ier.test._
import uk.gov.gds.ier.transaction.crown.previousAddress.PreviousAddressPostcodeMustache

class PreviousAddressPostcodeTemplateTest
  extends TemplateTestSuite
  with PreviousAddressPostcodeMustache {

  it should "properly render all properties from the model" in {
    running(FakeApplication()) {
      val data = new PostcodeModel(
        question = Question(
          postUrl = "http://some.server/previousAddress/select",
          number = "123",
          title = "Page title ABC"
        ),
        postcode = Field(
          id = "postcodeId",
          name = "postcodeName",
          classes = "postcodeClass1 postcodeClass2",
          value = "WR26NJ"
        )
      )

      val html = Mustache.render("crown/previousAddressPostcode", data)
      val doc = Jsoup.parse(html.toString)

      val fieldset = doc.select("fieldset").first()
      fieldset should not be (null)

      val label = fieldset.select("label").first()
      label should not be (null)
      label.attr("for") should be("postcodeId")

      val divWrapper = fieldset.select("div").first()
      divWrapper should not be (null)
      divWrapper.attr("class") should include("postcodeClass1")
      divWrapper.attr("class") should include("postcodeClass2")

      val input = divWrapper.select("input#postcodeId").first()
      input should not be (null)
      input.attr("name") should be("postcodeName")
      input.attr("value") should be("WR26NJ")
      input.attr("class") should include("postcodeClass1")
      input.attr("class") should include("postcodeClass2")
    }
  }
}
