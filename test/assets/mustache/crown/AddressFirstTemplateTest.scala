package assets.mustache.crown

import uk.gov.gds.ier.transaction.crown.address.AddressFirstMustache
import uk.gov.gds.ier.test._

class AddressFirstTemplateTest
  extends TemplateTestSuite
  with AddressFirstMustache {

  it should "properly render all properties from the model" in {
    running(FakeApplication()) {
      val data = new AddressFirstModel(
        question = Question(
          postUrl = "http://some.server/post_url",
          number = "123",
          title = "Page title ABC"
        ),
        hasAddressYesAndLivingThere = Field(
          id = "hasAddressYesAndLivingThereId",
          name = "hasAddressYesAndLivingThereName",
          classes = "hasAddressYesAndLivingThereClass",
          value = "hasAddressYesAndLivingThereValue",
          attributes = "foo=\"foo\""
        ),
        hasAddressYesAndNotLivingThere = Field(
          id = "hasAddressYesAndNotLivingThereId",
          name = "hasAddressYesAndNotLivingThereName",
          classes = "hasAddressYesAndNotLivingThereClass",
          value = "hasAddressYesAndNotLivingThereValue",
          attributes = "foo=\"foo\""
        ),
        hasAddressNo = Field(
          id = "hasAddressNoId",
          name = "hasAddressNoName",
          classes = "hasAddressNoClass",
          value = "hasAddressNoValue",
          attributes = "foo=\"foo\""
        )
      )

      val html = Mustache.render("crown/addressFirst", data)
      val doc = Jsoup.parse(html.toString)

      { // YES and living there option
        doc.select("label[for=hasAddressYesAndLivingThereId]").size() should be(1)
        val r = doc.select("input#hasAddressYesAndLivingThereId").first()
        r should not be(null)
        r.attr("name") should be("hasAddressYesAndLivingThereName")
        r.attr("value") should be("hasAddressYesAndLivingThereValue")
        r.attr("foo") should be("foo")
      }

      { // YES and not living there option
        doc.select("label[for=hasAddressYesAndNotLivingThereId]").size() should be(1)
        val r = doc.select("input#hasAddressYesAndNotLivingThereId").first()
        r should not be(null)
        r.attr("name") should be("hasAddressYesAndNotLivingThereName")
        r.attr("value") should be("hasAddressYesAndNotLivingThereValue")
        r.attr("foo") should be("foo")
      }

      { // NO option
        doc.select("label[for=hasAddressNoId]").size() should be(1)
        val r = doc.select("input#hasAddressNoId").first()
        r should not be(null)
        r.attr("id") should be("hasAddressNoId")
        r.attr("name") should be("hasAddressNoName")
        r.attr("value") should be("hasAddressNoValue")
        r.attr("foo") should be("foo")
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
