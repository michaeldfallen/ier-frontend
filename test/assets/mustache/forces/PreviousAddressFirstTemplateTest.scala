package assets.mustache.forces

import uk.gov.gds.ier.transaction.forces.previousAddress.PreviousAddressFirstMustache
import uk.gov.gds.ier.test._

/**
 * Test rendering of Mustache template from given model
 */
class PreviousAddressFirstTemplateTest
  extends TemplateTestSuite
  with PreviousAddressFirstMustache {

  it should "properly render all properties from the model" in {
    running(FakeApplication()) {
      val data = new PreviousAddressFirstModel(
        question = Question(
          postUrl = "http://some.server/post_url",
          number = "123",
          title = "Page title ABC"
        ),
        previousYes = Field(
          id = "previousYesId",
          name = "previousYesName",
          classes = "previousYesClass previousYesClass2",
          value = "true",
          attributes = "checked=\"checked1\""
        ),
        previousNo = Field(
          id = "previousNoId",
          name = "previousNoName",
          classes = "previousNoClass previousNoClass2",
          value = "false",
          attributes = "checked=\"checked2\""
        )
      )

      val html = Mustache.render("forces/previousAddressFirst", data)
      val doc = Jsoup.parse(html.toString)

      { // YES option
        doc.select("label[for=previousYesId]").size() should be(1)
        val r = doc.select("input#previousYesId").first()
        r should not be(null)
        r.attr("name") should be("previousYesName")
        r.attr("value") should be("true")
        r.attr("checked") should be("checked1")
      }

      { // NO option
        doc.select("label[for=previousNoId]").size() should be(1)
        val r = doc.select("input#previousNoId").first()
        r should not be(null)
        r.attr("id") should be("previousNoId")
        r.attr("name") should be("previousNoName")
        r.attr("value") should be("false")
        r.attr("checked") should be("checked2")
      }

      { // page
        val f = doc.select("form").first() // there should be only one form in the template
        f should not be(null)
        f.attr("action") should be ("http://some.server/post_url")

        val h = doc.select("header").first() // there should be only one header in the template
        h should not be(null)
        h.text should include ("123")
        h.text should include ("Page title ABC")
      }
    }
  }
}
