package uk.gov.gds.ier.transaction.ordinary.contact

import uk.gov.gds.ier.test.FormTestSuite
import uk.gov.gds.ier.model.ContactDetail

class ContactFormTests
  extends FormTestSuite
  with ContactForms {

  it should "bind successfully (all)" in {
    val js = Json.toJson(
      Map(
        "contact.post.contactMe" -> "true",
        "contact.phone.detail" -> "1234567890",
        "contact.phone.contactMe" -> "true",
        "contact.email.detail" -> "fake@fake.com",
        "contact.email.contactMe" -> "true"
      )
    )
    contactForm.bind(js).fold(
      hasErrors => fail(serialiser.toJson(hasErrors.prettyPrint)),
      success => {
        success.contact.isDefined should be(true)
        val contact = success.contact.get
        contact.post should be(true)
        contact.phone should be(Some(ContactDetail(true, Some("1234567890"))))
        contact.email should be(Some(ContactDetail(true, Some("fake@fake.com"))))
      }
    )
  }

  it should "bind successfully (post)" in {
    val js = Json.toJson(
      Map(
        "contact.post.contactMe" -> "true"
      )
    )
    contactForm.bind(js).fold(
      hasErrors => fail(serialiser.toJson(hasErrors.prettyPrint)),
      success => {
        success.contact.isDefined should be(true)
        val contact = success.contact.get
        contact.post should be(true)
        contact.phone should be(None)
        contact.email should be(None)
      }
    )
  }

  it should "bind successfully (phone)" in {
    val js = Json.toJson(
      Map(
        "contact.phone.detail" -> "1234567890",
        "contact.phone.contactMe" -> "true"
      )
    )
    contactForm.bind(js).fold(
      hasErrors => fail(serialiser.toJson(hasErrors.prettyPrint)),
      success => {
        success.contact.isDefined should be(true)
        val contact = success.contact.get
        contact.phone should be(Some(ContactDetail(true,Some("1234567890"))))
        contact.post should be(false)
        contact.email should be(None)
      }
    )
  }

  it should "bind successfully (email)" in {
    val js = Json.toJson(
      Map(
        "contact.email.detail" -> "fake@fake.com",
        "contact.email.contactMe" -> "true"
      )
    )
    contactForm.bind(js).fold(
      hasErrors => fail(serialiser.toJson(hasErrors.prettyPrint)),
      success => {
        success.contact.isDefined should be(true)
        val contact = success.contact.get
        contact.email should be(Some(ContactDetail(true,Some("fake@fake.com"))))
        contact.phone should be(None)
        contact.post should be(false)
      }
    )
  }

  it should "bind successfully (email with special characters)" in {
    val js = Json.toJson(
      Map(
        "contact.email.detail" -> "o'fake._%+'-@fake._%+'-.co.uk",
        "contact.email.contactMe" -> "true"
      )
    )
    contactForm.bind(js).fold(
      hasErrors => fail(serialiser.toJson(hasErrors.prettyPrint)),
      success => {
        success.contact.isDefined should be(true)
        val contact = success.contact.get
        contact.email should be(Some(ContactDetail(true,Some("o'fake._%+'-@fake._%+'-.co.uk"))))
        contact.phone should be(None)
        contact.post should be(false)
      }
    )
  }

  it should "error out on empty json" in {
    val js = JsNull

    contactForm.bind(js).fold(
      hasErrors => {
        hasErrors.errors.size should be(2)
        hasErrors.errorMessages("contact") should be(Seq("ordinary_contact_error_pleaseAnswer"))
        hasErrors.globalErrorMessages should be(Seq("ordinary_contact_error_pleaseAnswer"))
      },
      success => fail("Should have thrown an error")
    )
  }

  it should "error out on empty values" in {
    val js = Json.toJson(
      Map(
        "contact.phone.contactMe" -> "",
        "contact.email.contactMe" -> "",
        "contact.post.detail" -> "",
        "contact.phone.detail" -> "",
        "contact.email.detail" -> ""
      )
    )
    contactForm.bind(js).fold(
      hasErrors => {
        hasErrors.errors.size should be(2)
        hasErrors.errorMessages("contact") should be(Seq("ordinary_contact_error_pleaseAnswer"))
        hasErrors.globalErrorMessages should be(Seq("ordinary_contact_error_pleaseAnswer"))
      },
      success => fail("Should have thrown an error")
    )
  }

  it should "error out with contactType and no detail provided (phone)" in {
    val js = Json.toJson(
      Map(
        "contact.phone.contactMe" -> "true"
      )
    )
    contactForm.bind(js).fold(
      hasErrors => {
        hasErrors.errors.size should be(2)
        hasErrors.keyedErrorsAsMap should matchMap(Map(
          "contact.phone.detail" -> Seq("ordinary_contact_error_enterYourPhoneNo")
        ))
        hasErrors.globalErrorMessages should be(Seq("ordinary_contact_error_enterYourPhoneNo"))
      },
      success => fail("Should have thrown an error")
    )
  }

  it should "error out with contactType and no detail provided (email)" in {
    val js = Json.toJson(
      Map(
        "contact.email.contactMe" -> "true"
      )
    )
    contactForm.bind(js).fold(
      hasErrors => {
        hasErrors.errors.size should be(2)
        hasErrors.keyedErrorsAsMap should matchMap(Map(
          "contact.email.detail" -> Seq("ordinary_contact_error_enterYourEmail")
        ))
        hasErrors.globalErrorMessages should be(Seq("ordinary_contact_error_enterYourEmail"))
      },
      success => fail("Should have thrown an error")
    )
  }

  it should "error out with contactType and invalid email provided" in {
    val js = Json.toJson(
      Map(
        "contact.email.contactMe" -> "true",
        "contact.email.detail" -> "test@mail"
      )
    )
    contactForm.bind(js).fold(
      hasErrors => {
        hasErrors.errors.size should be(2)
        hasErrors.keyedErrorsAsMap should matchMap(Map(
          "contact.email.detail" -> Seq("ordinary_contact_error_pleaseEnterValidEmail")
        ))
        hasErrors.globalErrorMessages should be(Seq("ordinary_contact_error_pleaseEnterValidEmail"))
      },
      success => fail("Should have thrown an error")
    )
  }

  it should "pass on invalid email if email.contactMe is not true" in {
    val js = Json.toJson(
      Map(
        "contact.post.contactMe" -> "true",
        "contact.email.detail" -> "test@mail"
      )
    )
    contactForm.bind(js).fold(
      hasErrors => fail(hasErrors.prettyPrint.mkString(", ")),
      success => {
        success.contact.isDefined should be(true)
        val contact = success.contact.get
        contact.post should be(true)
        contact.phone should be(None)
        contact.email.isDefined should be(true)
      }
    )
  }
}
