package uk.gov.gds.ier.transaction.overseas.contact

import uk.gov.gds.ier.test._
import uk.gov.gds.ier.model.{ContactDetail, Contact}
import uk.gov.gds.ier.transaction.overseas.InprogressOverseas

class ContactMustacheTests
  extends MustacheTestSuite
  with ContactForms
  with ContactMustache {

  it should "empty progress form should produce empty Model" in {
    val emptyApplicationForm = contactForm
    val contactModel = mustache.data(
      emptyApplicationForm,
      Call("POST", "/register-to-vote/overseas/contact"),
      InprogressOverseas()
    ).asInstanceOf[ContactModel]

    contactModel.question.title should be("If we have questions about your application, how should we contact you?")
    contactModel.question.postUrl should be("/register-to-vote/overseas/contact")

    contactModel.contactEmailCheckbox.attributes should be("")
    contactModel.contactEmailText.value should be("")
    contactModel.contactPhoneCheckbox.attributes should be("")
    contactModel.contactPhoneText.value should be("")
    contactModel.contactPostCheckbox.attributes should be("")
  }

  it should "progress form with filled email should produce Mustache Model with email value present" in {
    val partiallyFilledApplication =
      InprogressOverseas(
        contact = Some(
          Contact(
            post = false,
            email = Some(ContactDetail(true, Some("my@email.com"))),
            phone = None
          )
        )
      )
    val partiallyFilledApplicationForm = contactForm.fill(partiallyFilledApplication)

    val contactModel = mustache.data(
      partiallyFilledApplicationForm,
      Call("POST", "/register-to-vote/overseas/contact"),
      InprogressOverseas()
    ).asInstanceOf[ContactModel]

    contactModel.question.title should be("If we have questions about your application, how should we contact you?")
    contactModel.question.postUrl should be("/register-to-vote/overseas/contact")

    contactModel.contactEmailCheckbox.attributes should be("checked=\"checked\"")
    contactModel.contactEmailText.value should be("my@email.com")
    contactModel.contactPhoneCheckbox.attributes should be("")
    contactModel.contactPhoneText.value should be("")
    contactModel.contactPostCheckbox.attributes should be("")
  }

  it should "progress form with filled phone should produce Mustache Model with phone value present" in {
    val partiallyFilledApplication =
      InprogressOverseas(
        contact = Some(
          Contact(
            post = false,
            email = None,
            phone = Some(ContactDetail(true, Some("1234567890")))
          )
        )
      )
    val partiallyFilledApplicationForm = contactForm.fill(partiallyFilledApplication)

    val contactModel = mustache.data(
      partiallyFilledApplicationForm,
      Call("POST", "/register-to-vote/overseas/contact"),
      InprogressOverseas()
    ).asInstanceOf[ContactModel]

    contactModel.question.title should be("If we have questions about your application, how should we contact you?")
    contactModel.question.postUrl should be("/register-to-vote/overseas/contact")

    contactModel.contactEmailCheckbox.attributes should be("")
    contactModel.contactEmailText.value should be("")
    contactModel.contactPhoneCheckbox.attributes should be("checked=\"checked\"")
    contactModel.contactPhoneText.value should be("1234567890")
    contactModel.contactPostCheckbox.attributes should be("")
  }

  it should "progress form with filled phone and post option should produce Mustache Model with phone and post values present" in {
    val partiallyFilledApplication =
      InprogressOverseas(
        contact = Some(
          Contact(
            post = true,
            email = None,
            phone = Some(ContactDetail(true, Some("1234567890")))
          )
        )
      )
    val partiallyFilledApplicationForm = contactForm.fill(partiallyFilledApplication)

    val contactModel = mustache.data(
      partiallyFilledApplicationForm,
      Call("POST", "/register-to-vote/overseas/contact"),
      InprogressOverseas()
    ).asInstanceOf[ContactModel]

    contactModel.question.title should be("If we have questions about your application, how should we contact you?")
    contactModel.question.postUrl should be("/register-to-vote/overseas/contact")

    contactModel.contactEmailCheckbox.attributes should be("")
    contactModel.contactEmailText.value should be("")
    contactModel.contactPhoneCheckbox.attributes should be("checked=\"checked\"")
    contactModel.contactPhoneText.value should be("1234567890")
    contactModel.contactPostCheckbox.attributes should be("checked=\"checked\"")
  }

  it should "progress form with validation errors should produce Model with error list present" in {
    val partiallyFilledApplication =
      InprogressOverseas(
        contact = Some(
          Contact(
            post = false,
            email = None,
            phone = Some(ContactDetail(true, None))
          )
        )
      )
    val partiallyFilledApplicationForm = contactForm.fillAndValidate(partiallyFilledApplication)

    val contactModel = mustache.data(
      partiallyFilledApplicationForm,
      Call("POST", "/register-to-vote/overseas/contact"),
      InprogressOverseas()
    ).asInstanceOf[ContactModel]

    contactModel.question.title should be("If we have questions about your application, how should we contact you?")
    contactModel.question.postUrl should be("/register-to-vote/overseas/contact")

    contactModel.contactEmailCheckbox.attributes should be("")
    contactModel.contactEmailText.value should be("")
    contactModel.contactPhoneCheckbox.attributes should be("checked=\"checked\"")
    contactModel.contactPhoneText.value should be("")
    contactModel.contactPostCheckbox.attributes should be("")

    contactModel.question.errorMessages.mkString(", ") should be("Please enter your phone number")
  }
}
