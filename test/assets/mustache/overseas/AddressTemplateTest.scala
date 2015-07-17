package assets.mustache.overseas

import uk.gov.gds.ier.transaction.overseas.address.AddressMustache
import uk.gov.gds.ier.test._

class AddressTemplateTest
  extends TemplateTestSuite
  with AddressMustache {

  it should "properly render all properties from the model" in {

    running(FakeApplication()) {
      val data = AddressModel(
        question = Question(postUrl = "/register-to-vote/overseas/address",
        number = "11",
        title = "Where do you live?"
        ),
        countrySelect = Field(
          id = "overseasAddressCountryId",
          name = "overseasAddressCountryName",
          classes = "overseasAddressCountryClass",
          value = "United Kingdom",
          optionList = List(SelectOption(value = "United Kingdom", text = "United Kingdom",
              selected = "selected=\"selected\""),
              SelectOption(value = "France", text = "France"))
        ),
        addressLine1 = Field(
          id = "addressLine1Id",
          name = "addressLine1Name",
          value = "some address line 1"
        ),
        addressLine2 = Field(
          id = "addressLine2Id",
          name = "addressLine2Name",
          value = "some address line 2"
        ),
        addressLine3 = Field(
          id = "addressLine3Id",
          name = "addressLine3Name",
          value = "some address line 3"
        ),
        addressLine4 = Field(
          id = "addressLine4Id",
          name = "addressLine4Name",
          value = "some address line 4"
        ),
        addressLine5 = Field(
          id = "addressLine5Id",
          name = "addressLine5Name",
          value = "some address line 5"
        )
      )

      val html = Mustache.render("overseas/address", data)
      val doc = Jsoup.parse(html.toString)

      { //country select label
        doc.select("label[for=overseasAddressCountryId]").size() should be (1)
      }

      { // country selector wrapper
        doc.select("div[class*=overseasAddressCountryClass]").size() should be(1)
      }

      { // country selector
        val e = doc.select("div[class*=overseasAddressCountryClass] select").first()
        e should not be(null)
        e.attr("id") should be("overseasAddressCountryId")
        e.attr("id") should be("overseasAddressCountryId")
        e.attr("name") should be("overseasAddressCountryName")
        e.attr("class") should include("overseasAddressCountryClass")
      }

      {
       val e = doc.select("div[class*=overseasAddressCountryClass] select option[selected]").first()
       e should not be(null)
       e.text should be("United Kingdom")
       e.attr("value") should be("United Kingdom")
      }

      { //address details line 1 wrapper
        val e = doc.select("input[id=addressLine1Id]").first()
        e should not be(null)
        e.attr("id") should be("addressLine1Id")
        e.attr("name") should be("addressLine1Name")
        e.attr("value") should be("some address line 1")
      }

      { //address details line 2 wrapper
      val e = doc.select("input[id=addressLine2Id]").first()
        e should not be(null)
        e.attr("id") should be("addressLine2Id")
        e.attr("name") should be("addressLine2Name")
        e.attr("value") should be("some address line 2")
      }

      { //address details line 3 wrapper
      val e = doc.select("input[id=addressLine3Id]").first()
        e should not be(null)
        e.attr("id") should be("addressLine3Id")
        e.attr("name") should be("addressLine3Name")
        e.attr("value") should be("some address line 3")
      }

      { //address details line 4 wrapper
      val e = doc.select("input[id=addressLine4Id]").first()
        e should not be(null)
        e.attr("id") should be("addressLine4Id")
        e.attr("name") should be("addressLine4Name")
        e.attr("value") should be("some address line 4")
      }

      { //address details line 5 wrapper
      val e = doc.select("input[id=addressLine5Id]").first()
        e should not be(null)
        e.attr("id") should be("addressLine5Id")
        e.attr("name") should be("addressLine5Name")
        e.attr("value") should be("some address line 5")
      }
    }
  }
}
