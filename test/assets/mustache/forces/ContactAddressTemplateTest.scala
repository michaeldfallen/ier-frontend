package assets.mustache.forces

import uk.gov.gds.ier.transaction.forces.contactAddress.ContactAddressMustache
import uk.gov.gds.ier.test._

class ContactAddressTemplateTest
  extends TemplateTestSuite
  with ContactAddressMustache {

  it should "properly render all properties from the model" in {
    running(FakeApplication()) {

      val ukContactAddressModel = new UKContactAddressModel(
        ukAddressOption = Field(
          id = "ukAddressOptionId",
          name = "ukAddressOptionName",
          attributes = "foo=\"foo\""
        ),
        ukAddressLineText = Field(
          value = "ukAddressLineTextValue"
        )
      )

      val otherContactAddressModel = new OtherContactAddressModel(
         otherAddressOption = Field(
           id = "otherAddressOptionId",
           name = "otherAddressOptionName",
           attributes = "foo=\"foo\""
         ),
         otherAddressLine1 = Field(
           id = "otherAddressLine1Id",
           name = "otherAddressLine1Name",
           classes = "otherAddressLine1Class",
           value = "otherAddressLine1Value"
         ),
         otherAddressLine2 = Field(
           id = "otherAddressLine2Id",
           name = "otherAddressLine2Name",
           classes = "otherAddressLine2Class",
           value = "otherAddressLine2Value"
         ),
         otherAddressLine3 = Field(
           id = "otherAddressLine3Id",
           name = "otherAddressLine3Name",
           classes = "otherAddressLine3Class",
           value = "otherAddressLine3Value"
         ),
         otherAddressLine4 = Field(
           id = "otherAddressLine4Id",
           name = "otherAddressLine4Name",
           classes = "otherAddressLine4Class",
           value = "otherAddressLine4Value"
         ),
         otherAddressLine5 = Field(
           id = "otherAddressLine5Id",
           name = "otherAddressLine5Name",
           classes = "otherAddressLine5Class",
           value = "otherAddressLine5Value"
         ),
         otherAddressPostcode = Field(
           id = "otherAddressPostcodeId",
           name = "otherAddressPostcodeName",
           classes = "otherAddressPostcodeClass",
           value = "otherAddressPostcodeValue"
         ),
         otherAddressCountry = Field(
           id = "otherAddressCountryId",
           name = "otherAddressCountryName",
           classes = "otherAddressCountryClass",
           value = "otherAddressCountryValue"
         )
      )

      val bfpoContactAddressModel = new BFPOContactAddressModel(
        BFPOAddressOption = Field(
          id = "BFPOAddressOptionId",
          name = "BFPOAddressOptionName",
          attributes = "foo=\"foo\""
        ),
        BFPOAddressLine1 = Field(
          id = "BFPOAddressLine1Id",
          name = "BFPOAddressLine1Name",
          classes = "BFPOAddressLine1Class",
          value = "BFPOAddressLine1Value"
        ),
        BFPOAddressLine2 = Field(
          id = "BFPOAddressLine2Id",
          name = "BFPOAddressLine2Name",
          classes = "BFPOAddressLine2Class",
          value = "BFPOAddressLine2Value"
        ),
        BFPOAddressLine3 = Field(
          id = "BFPOAddressLine3Id",
          name = "BFPOAddressLine3Name",
          classes = "BFPOAddressLine3Class",
          value = "BFPOAddressLine3Value"
        ),
        BFPOAddressLine4 = Field(
          id = "BFPOAddressLine4Id",
          name = "BFPOAddressLine4Name",
          classes = "BFPOAddressLine4Class",
          value = "BFPOAddressLine4Value"
        ),
        BFPOAddressLine5 = Field(
          id = "BFPOAddressLine5Id",
          name = "BFPOAddressLine5Name",
          classes = "BFPOAddressLine5Class",
          value = "BFPOAddressLine5Value"
        ),
        BFPOAddressPostcode = Field(
          id = "BFPOAddressPostcodeId",
          name = "BFPOAddressPostcodeName",
          classes = "BFPOAddressPostcodeClass",
          value = "BFPOAddressPostcodeValue"
        )
      )


      val data = new ContactAddressModel(
        question = Question(
          postUrl = "http://some.server/post_url",
          number = "123",
          title = "Page title ABC"
        ),
        contactAddressFieldSet = FieldSet(),
        ukAddress = ukContactAddressModel,
        bfpoAddress = bfpoContactAddressModel,
        otherAddress = otherContactAddressModel
      )

      val html = Mustache.render("forces/contactAddress", data)
      val doc = Jsoup.parse(html.toString)

      {
        doc.select("input#ukAddressOptionId").size() should be(1)
        val r = doc.select("input#ukAddressOptionId").first()
        r.attr("id") should be("ukAddressOptionId")
        r.attr("name") should be("ukAddressOptionName")
        r.attr("foo") should be("foo")
      }

      {
        doc.select("input#BFPOAddressOptionId").size() should be(1)
        val r = doc.select("input#BFPOAddressOptionId").first()
        r.attr("id") should be("BFPOAddressOptionId")
        r.attr("name") should be("BFPOAddressOptionName")
        r.attr("foo") should be("foo")
      }

      {
        doc.select("input#BFPOAddressLine1Id").size() should be(1)
        val r = doc.select("input#BFPOAddressLine1Id").first()
        r.attr("id") should be("BFPOAddressLine1Id")
        r.attr("name") should be("BFPOAddressLine1Name")
        r.attr("class") should include ("BFPOAddressLine1Class")
        r.attr("value") should be("BFPOAddressLine1Value")
      }

      {
        doc.select("input#BFPOAddressLine2Id").size() should be(1)
        val r = doc.select("input#BFPOAddressLine2Id").first()
        r.attr("id") should be("BFPOAddressLine2Id")
        r.attr("name") should be("BFPOAddressLine2Name")
        r.attr("value") should be("BFPOAddressLine2Value")
      }

      {
        doc.select("input#BFPOAddressLine3Id").size() should be(1)
        val r = doc.select("input#BFPOAddressLine3Id").first()
        r.attr("id") should be("BFPOAddressLine3Id")
        r.attr("name") should be("BFPOAddressLine3Name")
        r.attr("value") should be("BFPOAddressLine3Value")
      }

      {
        doc.select("input#BFPOAddressLine4Id").size() should be(1)
        val r = doc.select("input#BFPOAddressLine4Id").first()
        r.attr("id") should be("BFPOAddressLine4Id")
        r.attr("name") should be("BFPOAddressLine4Name")
        r.attr("value") should be("BFPOAddressLine4Value")
      }

      {
        doc.select("input#BFPOAddressLine5Id").size() should be(1)
        val r = doc.select("input#BFPOAddressLine5Id").first()
        r.attr("id") should be("BFPOAddressLine5Id")
        r.attr("name") should be("BFPOAddressLine5Name")
        r.attr("value") should be("BFPOAddressLine5Value")
      }

      {
        doc.select("input#BFPOAddressPostcodeId").size() should be(1)
        val r = doc.select("input#BFPOAddressPostcodeId").first()
        r.attr("id") should be("BFPOAddressPostcodeId")
        r.attr("name") should be("BFPOAddressPostcodeName")
        r.attr("class") should include ("BFPOAddressPostcodeClass")
        r.attr("value") should be("BFPOAddressPostcodeValue")
      }

      {
        doc.select("input#otherAddressOptionId").size() should be(1)
        val r = doc.select("input#otherAddressOptionId").first()
        r.attr("id") should be("otherAddressOptionId")
        r.attr("name") should be("otherAddressOptionName")
        r.attr("foo") should be("foo")
      }

      {
        doc.select("input#otherAddressLine1Id").size() should be(1)
        val r = doc.select("input#otherAddressLine1Id").first()
        r.attr("id") should be("otherAddressLine1Id")
        r.attr("name") should be("otherAddressLine1Name")
        r.attr("class") should include ("otherAddressLine1Class")
        r.attr("value") should be("otherAddressLine1Value")
      }

      {
        doc.select("input#otherAddressLine2Id").size() should be(1)
        val r = doc.select("input#otherAddressLine2Id").first()
        r.attr("id") should be("otherAddressLine2Id")
        r.attr("name") should be("otherAddressLine2Name")
        r.attr("value") should be("otherAddressLine2Value")
      }

      {
        doc.select("input#otherAddressLine3Id").size() should be(1)
        val r = doc.select("input#otherAddressLine3Id").first()
        r.attr("id") should be("otherAddressLine3Id")
        r.attr("name") should be("otherAddressLine3Name")
        r.attr("value") should be("otherAddressLine3Value")
      }

      {
        doc.select("input#otherAddressLine4Id").size() should be(1)
        val r = doc.select("input#otherAddressLine4Id").first()
        r.attr("id") should be("otherAddressLine4Id")
        r.attr("name") should be("otherAddressLine4Name")
        r.attr("value") should be("otherAddressLine4Value")
      }

      {
        doc.select("input#otherAddressLine5Id").size() should be(1)
        val r = doc.select("input#otherAddressLine5Id").first()
        r.attr("id") should be("otherAddressLine5Id")
        r.attr("name") should be("otherAddressLine5Name")
        r.attr("value") should be("otherAddressLine5Value")
      }

      {
        doc.select("input#otherAddressPostcodeId").size() should be(1)
        val r = doc.select("input#otherAddressPostcodeId").first()
        r.attr("id") should be("otherAddressPostcodeId")
        r.attr("name") should be("otherAddressPostcodeName")
        r.attr("class") should include ("otherAddressPostcodeClass")
        r.attr("value") should be("otherAddressPostcodeValue")
      }

      {
        doc.select("input#otherAddressCountryId").size() should be(1)
        val r = doc.select("input#otherAddressCountryId").first()
        r.attr("id") should be("otherAddressCountryId")
        r.attr("name") should be("otherAddressCountryName")
        r.attr("class") should include ("otherAddressCountryClass")
        r.attr("value") should be("otherAddressCountryValue")
      }

      {
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
