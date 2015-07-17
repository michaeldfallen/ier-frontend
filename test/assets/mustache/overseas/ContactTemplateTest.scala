package assets.mustache.overseas

import uk.gov.gds.ier.test.TemplateTestSuite

class ContactTemplateTest extends TemplateTestSuite {

  case class Model(
    contactFieldSet: FieldSet,
    contactEmailCheckbox: Field,
    contactPhoneCheckbox: Field,
    contactPostCheckbox: Field,
    contactEmailText: Field,
    contactPhoneText: Field
  )

  it should "properly render all properties from the model" in {
    running(FakeApplication()) {
      val data = Model(
        contactFieldSet = FieldSet (classes = "contactFieldSetClass"),
        contactEmailCheckbox = Field(
          id = "contactEmailCheckboxId",
          name = "contactEmailCheckboxName",
          classes = "contactEmailCheckboxClass",
          attributes = "foo=\"foo\""
        ),
        contactPhoneCheckbox = Field(
          id = "contactPhoneCheckboxId",
          name = "contactPhoneCheckboxName",
          classes = "contactPhoneCheckboxClass",
          attributes = "foo=\"foo\""
        ),
        contactPostCheckbox = Field(
          id = "contactPostCheckboxId",
          name = "contactPostCheckboxName",
          classes = "contactPostCheckboxClass",
          attributes = "foo=\"foo\""
        ),
        contactEmailText = Field(
          id = "contactEmailTextId",
          name = "contactEmailTextName",
          classes = "contactEmailTextClass",
          value ="contactEmailTextValue"
        ),
        contactPhoneText = Field(
          id = "contactPhoneTextId",
          name = "contactPhoneTextName",
          classes = "contactPhoneTextClass",
          value ="contactPhoneTextValue"
        )
      )

      val html = Mustache.render("overseas/contact", data)
      val doc = Jsoup.parse(html.toString)

      val contactEmailCheckboxInput = doc.select("input[id*=contactEmailCheckboxId]").first()
      contactEmailCheckboxInput.attr("id") should be("contactEmailCheckboxId")
      contactEmailCheckboxInput.attr("name") should be("contactEmailCheckboxName")
      contactEmailCheckboxInput.attr("class") should include("contactEmailCheckboxClass")
      contactEmailCheckboxInput.attr("foo") should include("foo")

      val contactEmailTextInput = doc.select("input[id*=contactEmailTextId]").first()
      contactEmailTextInput.attr("id") should be("contactEmailTextId")
      contactEmailTextInput.attr("name") should be("contactEmailTextName")
      contactEmailTextInput.attr("class") should include("contactEmailTextClass")
      contactEmailTextInput.attr("value") should include("contactEmailTextValue")

      val contactPhoneCheckboxInput = doc.select("input[id*=contactPhoneCheckboxId]").first()
      contactPhoneCheckboxInput.attr("id") should be("contactPhoneCheckboxId")
      contactPhoneCheckboxInput.attr("name") should be("contactPhoneCheckboxName")
      contactPhoneCheckboxInput.attr("class") should include("contactPhoneCheckboxClass")
      contactPhoneCheckboxInput.attr("foo") should include("foo")

      val contactPhoneTextInput = doc.select("input[id*=contactPhoneTextId]").first()
      contactPhoneTextInput.attr("id") should be("contactPhoneTextId")
      contactPhoneTextInput.attr("name") should be("contactPhoneTextName")
      contactPhoneTextInput.attr("class") should include("contactPhoneTextClass")
      contactPhoneTextInput.attr("value") should include("contactPhoneTextValue")

      val contactPostCheckboxInput = doc.select("input[id*=contactPostCheckboxId]").first()
      contactPostCheckboxInput.attr("id") should be("contactPostCheckboxId")
      contactPostCheckboxInput.attr("name") should be("contactPostCheckboxName")
      contactPostCheckboxInput.attr("class") should include("contactPostCheckboxClass")
      contactPostCheckboxInput.attr("foo") should include("foo")

    }
  }
}
