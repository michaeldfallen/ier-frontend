package uk.gov.gds.ier.transaction.overseas.contact

import uk.gov.gds.ier.test.ControllerTestSuite

class ContactControllerTests extends ControllerTestSuite {

  behavior of "ContactController.get"
  it should "display the page" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(GET, "/register-to-vote/overseas/contact").withIerSession()
      )

      status(result) should be(OK)
      contentType(result) should be(Some("text/html"))
      contentAsString(result) should include(
        "If we have questions about your application, how should we contact you?")
      contentAsString(result) should include("/register-to-vote/overseas/contact")
    }
  }

  behavior of "ContactController.post"
  it should "bind successfully and redirect to the Confirmation step" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(POST, "/register-to-vote/overseas/contact")
          .withIerSession()
          .withFormUrlEncodedBody(
            "contact.phone.contactMe" -> "true",
            "contact.phone.detail" -> "01234 123 456")
      )

      status(result) should be(SEE_OTHER)
      redirectLocation(result) should be(Some("/register-to-vote/overseas/confirmation"))
    }
  }

  it should "display any errors on unsuccessful bind" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(POST, "/register-to-vote/overseas/contact").withIerSession()
      )

      status(result) should be(OK)
      contentAsString(result) should include(
        "If we have questions about your application, how should we contact you?")
      contentAsString(result) should include("Please answer this question")
      contentAsString(result) should include("/register-to-vote/overseas/contact")
    }
  }

  behavior of "ContactController.editGet"
  it should "display the page" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(GET, "/register-to-vote/overseas/edit/contact").withIerSession()
      )

      status(result) should be(OK)
      contentType(result) should be(Some("text/html"))
      contentAsString(result) should include(
        "If we have questions about your application, how should we contact you?")
      contentAsString(result) should include("/register-to-vote/overseas/edit/contact")
    }
  }

  behavior of "ContactController.editPost"
  it should "bind successfully and redirect to the Confirmation step" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(POST, "/register-to-vote/overseas/edit/contact")
          .withIerSession()
          .withFormUrlEncodedBody(
            "contact.phone.contactMe" -> "true",
            "contact.phone.detail" -> "01234 123 456")
      )

      status(result) should be(SEE_OTHER)
      redirectLocation(result) should be(Some("/register-to-vote/overseas/confirmation"))
    }
  }

  it should "display any errors on unsuccessful bind" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(POST, "/register-to-vote/overseas/edit/contact").withIerSession()
      )

      status(result) should be(OK)
      contentAsString(result) should include(
        "If we have questions about your application, how should we contact you?")
      contentAsString(result) should include("Please answer this question")
      contentAsString(result) should include("/register-to-vote/overseas/edit/contact")
    }
  }

}
