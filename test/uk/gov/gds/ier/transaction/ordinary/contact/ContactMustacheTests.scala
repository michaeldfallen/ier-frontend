package uk.gov.gds.ier.transaction.ordinary.contact

import uk.gov.gds.ier.test._
import uk.gov.gds.ier.model._
import uk.gov.gds.ier.transaction.ordinary.contact.routes._
import uk.gov.gds.ier.transaction.ordinary.InprogressOrdinary

class ContactMustacheTests
  extends MustacheTestSuite
  with ContactForms
  with ContactMustache {

  it should "empty progress form should produce empty Model" in runningApp {
    val emptyApplicationForm = contactForm
    val contactModel = mustache.data(
      emptyApplicationForm,
      ContactStep.post,
      InprogressOrdinary()
    ).asInstanceOf[ContactModel]

    contactModel.question.title should be ("If we have questions about your application, how should we contact you?")
    contactModel.question.postUrl should be("/register-to-vote/contact")

    contactModel.contactEmailCheckbox.attributes should be("")
    contactModel.contactEmailText.value should be("")
    contactModel.contactPhoneCheckbox.attributes should be("")
    contactModel.contactPhoneText.value should be("")
    contactModel.contactPostCheckbox.attributes should be("")
  }

  it should "progress form with filled email should produce Mustache Model with email value " in runningApp {
    val partiallyFilledApplicationForm = contactForm.fill(
      InprogressOrdinary(
        contact = Some(
          Contact(
            post = false,
            email = Some(ContactDetail(true, Some("my@email.com"))),
            phone = None
          )
        )
      )
    )

    val contactModel = mustache.data(
      partiallyFilledApplicationForm,
      ContactStep.post,
      InprogressOrdinary()
    ).asInstanceOf[ContactModel]

    contactModel.question.title should be("If we have questions about your application, how should we contact you?")
    contactModel.question.postUrl should be("/register-to-vote/contact")

    contactModel.contactEmailCheckbox.attributes should be("checked=\"checked\"")
    contactModel.contactEmailText.value should be("my@email.com")
    contactModel.contactPhoneCheckbox.attributes should be("")
    contactModel.contactPhoneText.value should be("")
    contactModel.contactPostCheckbox.attributes should be("")
  }

  it should "progress form with filled phone should produce Mustache Model with phone value" in runningApp {
    val partiallyFilledApplicationForm = contactForm.fill(
      InprogressOrdinary(
        contact = Some(
          Contact(
            post = false,
            email = None,
            phone = Some(ContactDetail(true, Some("1234567890")))
          )
        )
      )
    )

    val contactModel = mustache.data(
      partiallyFilledApplicationForm,
      ContactStep.post,
      InprogressOrdinary()
    ).asInstanceOf[ContactModel]

    contactModel.question.title should be("If we have questions about your application, how should we contact you?")
    contactModel.question.postUrl should be("/register-to-vote/contact")

    contactModel.contactEmailCheckbox.attributes should be("")
    contactModel.contactEmailText.value should be("")
    contactModel.contactPhoneCheckbox.attributes should be("checked=\"checked\"")
    contactModel.contactPhoneText.value should be("1234567890")
    contactModel.contactPostCheckbox.attributes should be("")
  }

  it should "progress form with filled phone and post should produce Model with phone and post" in runningApp {
    val partiallyFilledApplicationForm = contactForm.fill(
      InprogressOrdinary(
        contact = Some(
          Contact(
            post = true,
            email = None,
            phone = Some(ContactDetail(true, Some("1234567890")))
          )
        )
      )
    )

    val contactModel = mustache.data(
      partiallyFilledApplicationForm,
      ContactStep.post,
      InprogressOrdinary()
    ).asInstanceOf[ContactModel]

    contactModel.question.title should be("If we have questions about your application, how should we contact you?")
    contactModel.question.postUrl should be("/register-to-vote/contact")

    contactModel.contactEmailCheckbox.attributes should be("")
    contactModel.contactEmailText.value should be("")
    contactModel.contactPhoneCheckbox.attributes should be("checked=\"checked\"")
    contactModel.contactPhoneText.value should be("1234567890")
    contactModel.contactPostCheckbox.attributes should be("checked=\"checked\"")
  }

  it should "progress form with validation errors should produce Model with error list present" in runningApp {
    val partiallyFilledApplicationForm = contactForm.fillAndValidate(
      InprogressOrdinary(
        contact = Some(
          Contact(
            post = false,
            email = None,
            phone = Some(ContactDetail(true, None))
          )
        )
      )
    )

    val contactModel = mustache.data(
      partiallyFilledApplicationForm,
      ContactStep.post,
      InprogressOrdinary()
    ).asInstanceOf[ContactModel]

    contactModel.question.title should be("If we have questions about your application, how should we contact you?")
    contactModel.question.postUrl should be("/register-to-vote/contact")

    contactModel.contactEmailCheckbox.attributes should be("")
    contactModel.contactEmailText.value should be("")
    contactModel.contactPhoneCheckbox.attributes should be("checked=\"checked\"")
    contactModel.contactPhoneText.value should be("")
    contactModel.contactPostCheckbox.attributes should be("")

    contactModel.question.errorMessages.mkString(", ") should be("Please enter your phone number")
  }

  it should "pre-fill the email address from the postal vote question" in runningApp {
    val partiallyFilledApplicationForm = contactForm.fillAndValidate(
      InprogressOrdinary(
        contact = None,
        postalVote = Some(PostalVote(
          postalVoteOption = Some(PostalVoteOption.Yes),
          deliveryMethod = Some(PostalVoteDeliveryMethod(
            deliveryMethod = Some("email"),
            emailAddress = Some("my@email.com")
          ))
        ))
      )
    )

    val contactModel = mustache.data(
      partiallyFilledApplicationForm,
      ContactStep.post,
      InprogressOrdinary()
    ).asInstanceOf[ContactModel]

    contactModel.question.title should be("If we have questions about your application, how should we contact you?")
    contactModel.question.postUrl should be("/register-to-vote/contact")

    contactModel.contactEmailCheckbox.attributes should be("")
    contactModel.contactEmailText.value should be("my@email.com")
    contactModel.contactPhoneCheckbox.attributes should be("")
    contactModel.contactPhoneText.value should be("")
    contactModel.contactPostCheckbox.attributes should be("")
  }
}
