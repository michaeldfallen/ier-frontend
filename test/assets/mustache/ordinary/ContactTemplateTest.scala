package assets.mustache.ordinary

import uk.gov.gds.ier.transaction.ordinary.contact.ContactMustache
import uk.gov.gds.ier.test._

class ContactTemplateTest
  extends TemplateTestSuite
  with ContactMustache {

  it should "properly render all properties from the model" in {

    running(FakeApplication()) {
      val data = ContactModel(
        question = Question(postUrl = "/whatever-url",
        number = "1",
        title = "contact title"
        ),
        contactFieldSet = FieldSet(
          classes = ""
        ),
        contactEmailCheckbox = Field(
          id = "contactEmailCheckboxId",
          name = "contactEmailCheckboxName",
          value = "true"
        ),
        contactPhoneCheckbox = Field(
          id = "contactPhoneCheckboxId",
          name = "contactPhoneCheckboxName",
          value = "true"
        ),
        contactPostCheckbox = Field(
          id = "contactPostCheckboxId",
          name = "contactPostCheckboxName",
          value = "true"
        ),
        contactEmailText = Field(
          id = "contactEmailTextId",
          name = "contactEmailTextName",
          value = "test@test.com"
        ),
        contactPhoneText = Field(
          id = "contactPhoneTextId",
          name = "contactPhoneTextName",
          value = "123456"
        )
      )

      val html = Mustache.render("ordinary/contact", data)
      val doc = Jsoup.parse(html.toString)

      // page
      val f = doc.select("form").first()
      f should not be(null)
      f.attr("action") should be ("/whatever-url")

      val h = doc.select("header").first()
      h should not be(null)
      h.text should include ("1")
      h.text should include ("contact title")

      val emailCheckBox = doc.select("input#contactEmailCheckboxId").first()
      emailCheckBox should not be (null)
      emailCheckBox.attr("id") should be("contactEmailCheckboxId")
      emailCheckBox.attr("name") should be("contactEmailCheckboxName")
      emailCheckBox.attr("value") should be("true")

      val phoneCheckBox = doc.select("input#contactPhoneCheckboxId").first()
      phoneCheckBox should not be (null)
      phoneCheckBox.attr("id") should be("contactPhoneCheckboxId")
      phoneCheckBox.attr("name") should be("contactPhoneCheckboxName")
      phoneCheckBox.attr("value") should be("true")

      val postCheckBox = doc.select("input#contactPostCheckboxId").first()
      postCheckBox should not be (null)
      postCheckBox.attr("id") should be("contactPostCheckboxId")
      postCheckBox.attr("name") should be("contactPostCheckboxName")
      postCheckBox.attr("value") should be("true")

      val emailField = doc.select("input#contactEmailTextId").first()
      emailField should not be (null)
      emailField.attr("id") should be("contactEmailTextId")
      emailField.attr("name") should be("contactEmailTextName")
      emailField.attr("value") should be("test@test.com")

      val phoneField = doc.select("input#contactPhoneTextId").first()
      phoneField should not be (null)
      phoneField.attr("id") should be("contactPhoneTextId")
      phoneField.attr("name") should be("contactPhoneTextName")
      phoneField.attr("value") should be("123456")
    }
  }
}
