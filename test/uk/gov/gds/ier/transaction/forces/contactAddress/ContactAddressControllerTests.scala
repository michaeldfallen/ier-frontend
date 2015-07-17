package uk.gov.gds.ier.transaction.forces.contactAddress

import uk.gov.gds.ier.test.ControllerTestSuite

class ContactAddressControllerTests extends ControllerTestSuite {

  behavior of "ContactAddressController.get"
  it should "display the page" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(GET, "/register-to-vote/forces/contact-address").withIerSession()
      )

      status(result) should be(OK)
      contentType(result) should be(Some("text/html"))
      contentAsString(result) should include("Where should we write to you about your registration?")
      contentAsString(result) should include("/register-to-vote/forces/contact-address")
    }
  }

  behavior of "ContactAddressController.post"
  it should "bind successfully and redirect to the next step" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(POST, "/register-to-vote/forces/contact-address")
          .withIerSession()
          .withFormUrlEncodedBody(
            "contactAddress.contactAddressType" -> "uk"
          )
      )

      status(result) should be(SEE_OTHER)
      redirectLocation(result) should be(Some("/register-to-vote/forces/open-register"))
    }
  }

  it should "bind successfully and redirect to the confirmation step when all complete" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(POST, "/register-to-vote/forces/contact-address")
          .withIerSession()
          .withApplication(completeForcesApplication)
          .withFormUrlEncodedBody(
          "contactAddress.contactAddressType" -> "uk"
        )
      )

      status(result) should be(SEE_OTHER)
      redirectLocation(result) should be(Some("/register-to-vote/forces/confirmation"))
    }
  }

  it should "display any errors on unsuccessful bind" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(POST, "/register-to-vote/forces/contact-address").withIerSession()
      )

      status(result) should be(OK)
      contentAsString(result) should include("Where should we write to you about your registration?")
      contentAsString(result) should include("Please answer this question")
      contentAsString(result) should include("/register-to-vote/forces/contact-address")
    }
  }

  behavior of "Completing a prior step when this question is incomplete"
  it should "stop on this page" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(POST, "/register-to-vote/forces/rank")
          .withIerSession()
          .withApplication(completeForcesApplication.copy(contactAddress = None))
          .withFormUrlEncodedBody(
          "rank.serviceNumber" -> "12345",
          "rank.rank" -> "Captain"
        )
      )

      status(result) should be(SEE_OTHER)
      redirectLocation(result) should be(Some("/register-to-vote/forces/contact-address"))
    }
  }

  behavior of "ContactAddressController.editGet"
  it should "display the page" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(GET, "/register-to-vote/forces/edit/contact-address").withIerSession()
      )

      status(result) should be(OK)
      contentType(result) should be(Some("text/html"))
      contentAsString(result) should include("Where should we write to you about your registration?")
      contentAsString(result) should include("/register-to-vote/forces/edit/contact-address")    }
  }

  behavior of "ContactAddressController.editPost"
  it should "bind successfully and redirect to the next step" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(POST, "/register-to-vote/forces/edit/contact-address")
          .withIerSession()
          .withFormUrlEncodedBody(
          "contactAddress.contactAddressType" -> "uk"
          )
        )

      status(result) should be(SEE_OTHER)
      redirectLocation(result) should be(Some("/register-to-vote/forces/open-register"))
    }
  }

  it should "bind successfully and redirect to the confirmation step when all complete" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(POST, "/register-to-vote/forces/edit/contact-address")
          .withIerSession()
          .withApplication(completeForcesApplication)
          .withFormUrlEncodedBody(
          "contactAddress.contactAddressType" -> "uk"
        )
      )

      status(result) should be(SEE_OTHER)
      redirectLocation(result) should be(Some("/register-to-vote/forces/confirmation"))
    }
  }

  it should "display any errors on unsuccessful bind" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(POST, "/register-to-vote/forces/edit/contact-address").withIerSession()
      )

      status(result) should be(OK)
      contentAsString(result) should include("Where should we write to you about your registration?")
      contentAsString(result) should include("Please answer this question")
      contentAsString(result) should include("/register-to-vote/forces/edit/contact-address")
    }
  }
}
