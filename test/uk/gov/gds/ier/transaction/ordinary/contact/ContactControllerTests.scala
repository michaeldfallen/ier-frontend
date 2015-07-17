package uk.gov.gds.ier.transaction.ordinary.contact

import uk.gov.gds.ier.test.ControllerTestSuite

class ContactControllerTests extends ControllerTestSuite {

  behavior of "ContactController.get"
  it should "display the page" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(GET, "/register-to-vote/contact").withIerSession()
      )

      status(result) should be(OK)
      contentType(result) should be(Some("text/html"))
      contentAsString(result) should include("Question 11")
      contentAsString(result) should include("If we have questions about your application, how should we contact you?")
      contentAsString(result) should include("/register-to-vote/contact")
    }
  }

  behavior of "ContactController.post"
  it should "bind successfully and redirect to the Confirmation step" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(POST, "/register-to-vote/contact")
          .withIerSession()
          .withFormUrlEncodedBody(
            "contact.phone.contactMe" -> "true",
            "contact.phone.detail" -> "01234 123 456")
      )

      status(result) should be(SEE_OTHER)
      redirectLocation(result) should be(Some("/register-to-vote/confirmation"))
    }
  }

  it should "display any errors on unsuccessful bind" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(POST, "/register-to-vote/contact").withIerSession()
      )

      status(result) should be(OK)
      contentAsString(result) should include("Please answer this question")
      contentAsString(result) should include("/register-to-vote/contact")
    }
  }

  behavior of "Completing a prior step when this question is incomplete"
  it should "stop on this page" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(POST, "/register-to-vote/country-of-residence")
          .withIerSession()
          .withApplication(completeOrdinaryApplication.copy(contact = None))
          .withFormUrlEncodedBody(
          "country.residence" -> "England"
        )
      )

      status(result) should be(SEE_OTHER)
      redirectLocation(result) should be(Some("/register-to-vote/contact"))
    }
  }

  behavior of "ContactController.editGet"
  it should "display the page" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(GET, "/register-to-vote/edit/contact").withIerSession()
      )

      status(result) should be(OK)
      contentType(result) should be(Some("text/html"))
      contentAsString(result) should include("Question 11")
      contentAsString(result) should include("If we have questions about your application, how should we contact you?")
      contentAsString(result) should include("/register-to-vote/edit/contact")
    }
  }

  behavior of "ContactController.editPost"
  it should "bind successfully and redirect to the Confirmation step" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(POST, "/register-to-vote/edit/contact")
          .withIerSession()
          .withFormUrlEncodedBody(
          "contact.phone.contactMe" -> "true",
          "contact.phone.detail" -> "01234 123 456")
      )

      status(result) should be(SEE_OTHER)
      redirectLocation(result) should be(Some("/register-to-vote/confirmation"))
    }
  }

  it should "display any errors on unsuccessful bind" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(POST, "/register-to-vote/edit/contact").withIerSession()
      )

      status(result) should be(OK)
      contentAsString(result) should include("Please answer this question")
      contentAsString(result) should include("/register-to-vote/edit/contact")
    }
  }

}
