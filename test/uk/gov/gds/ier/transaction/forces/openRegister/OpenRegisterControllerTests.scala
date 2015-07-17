package uk.gov.gds.ier.transaction.forces.openRegister

import uk.gov.gds.ier.test.ControllerTestSuite

class OpenRegisterControllerTests extends ControllerTestSuite {

  behavior of "OpenRegisterController.get"
  it should "display the page" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(GET, "/register-to-vote/forces/open-register").withIerSession()
      )

      status(result) should be(OK)
      contentType(result) should be(Some("text/html"))
      contentAsString(result) should include("Do you want to include your name and address on the open register?")
      contentAsString(result) should include("/register-to-vote/forces/open-register")
    }
  }

  behavior of "OpenRegisterController.post"
  it should "bind successfully and redirect to the Ways to Vote step" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(POST, "/register-to-vote/forces/open-register")
          .withIerSession()
          .withFormUrlEncodedBody("openRegister.optIn" -> "true")
      )

      status(result) should be(SEE_OTHER)
      redirectLocation(result) should be(Some("/register-to-vote/forces/ways-to-vote"))
    }
  }

  it should "bind successfully and redirect to the confirmation step with complete Application" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(POST, "/register-to-vote/forces/open-register")
          .withIerSession()
          .withApplication(completeForcesApplication)
          .withFormUrlEncodedBody("openRegister.optIn" -> "true")
      )

      status(result) should be(SEE_OTHER)
      redirectLocation(result) should be(Some("/register-to-vote/forces/confirmation"))
    }
  }

  it should "not display any errors because we are evil dark patterny" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(POST, "/register-to-vote/forces/open-register").withIerSession()
      )

      status(result) should be(SEE_OTHER)
      redirectLocation(result) should be(Some("/register-to-vote/forces/ways-to-vote"))
    }
  }

  behavior of "Completing a prior step when this question is incomplete"
  it should "stop on this page" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(POST, "/register-to-vote/forces/contact-address")
          .withIerSession()
          .withApplication(completeForcesApplication.copy(openRegisterOptin = None))
          .withFormUrlEncodedBody(
          "contactAddress.contactAddressType" -> "uk"
        )
      )

      status(result) should be(SEE_OTHER)
      redirectLocation(result) should be(Some("/register-to-vote/forces/open-register"))
    }
  }

  behavior of "OpenRegisterController.editGet"
  it should "display the page" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(GET, "/register-to-vote/forces/edit/open-register").withIerSession()
      )

      status(result) should be(OK)
      contentType(result) should be(Some("text/html"))
      contentAsString(result) should include("Do you want to include your name and address on the open register?")
      contentAsString(result) should include("/register-to-vote/forces/edit/open-register")
    }
  }

  behavior of "OpenRegisterController.editPost"
  it should "bind successfully and redirect to the Previous Name step" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(POST, "/register-to-vote/forces/edit/open-register")
          .withIerSession()
          .withFormUrlEncodedBody("openRegister.optIn" -> "true")
      )

      status(result) should be(SEE_OTHER)
      redirectLocation(result) should be(Some("/register-to-vote/forces/ways-to-vote"))
    }
  }

  it should "bind successfully and redirect to the confirmation step with complete Application" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(POST, "/register-to-vote/forces/edit/open-register")
          .withIerSession()
          .withApplication(completeForcesApplication)
          .withFormUrlEncodedBody("openRegister.optIn" -> "true")
      )

      status(result) should be(SEE_OTHER)
      redirectLocation(result) should be(Some("/register-to-vote/forces/confirmation"))
    }
  }

  it should "not display any errors because we are evil dark patterny" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(POST, "/register-to-vote/forces/edit/open-register").withIerSession()
      )

      status(result) should be(SEE_OTHER)
      redirectLocation(result) should be(Some("/register-to-vote/forces/ways-to-vote"))
    }
  }
}
