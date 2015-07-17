package uk.gov.gds.ier.transaction.forces.nino

import uk.gov.gds.ier.test.ControllerTestSuite

class NinoControllerTests extends ControllerTestSuite {

  behavior of "NinoController.get"
  it should "display the page" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(GET, "/register-to-vote/forces/nino").withIerSession()
      )

      status(result) should be(OK)
      contentType(result) should be(Some("text/html"))
      contentAsString(result) should include("What is your National Insurance number?")
      contentAsString(result) should include("/register-to-vote/forces/nino")
    }
  }

  behavior of "NinoController.post"
  it should "bind successfully and redirect to the next step" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(POST, "/register-to-vote/forces/nino")
          .withIerSession()
          .withFormUrlEncodedBody("NINO.NINO" -> "AB 12 34 56 D")
      )

      status(result) should be(SEE_OTHER)
      redirectLocation(result) should be(Some("/register-to-vote/forces/service"))
    }
  }

  it should "bind successfully and redirect to the confirmation step with complete application" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(POST, "/register-to-vote/forces/nino")
          .withIerSession()
          .withApplication(completeForcesApplication)
          .withFormUrlEncodedBody("NINO.NINO" -> "AB 12 34 56 D")
      )

      status(result) should be(SEE_OTHER)
      redirectLocation(result) should be(Some("/register-to-vote/forces/confirmation"))
    }
  }

  it should "display any errors on unsuccessful bind" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(POST, "/register-to-vote/forces/nino").withIerSession()
      )

      status(result) should be(OK)
      contentAsString(result) should include("What is your National Insurance number?")
      contentAsString(result) should include("Please enter your National Insurance number")
      contentAsString(result) should include("/register-to-vote/forces/nino")
    }
  }

  behavior of "NinoController.editGet"
  it should "display the page" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(GET, "/register-to-vote/forces/edit/nino").withIerSession()
      )

      status(result) should be(OK)
      contentType(result) should be(Some("text/html"))
      contentAsString(result) should include("What is your National Insurance number?")
      contentAsString(result) should include("/register-to-vote/forces/edit/nino")
    }
  }

  behavior of "NinoController.editPost"
  it should "bind successfully and redirect to the next step" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(POST, "/register-to-vote/forces/edit/nino")
          .withIerSession()
          .withFormUrlEncodedBody("NINO.NINO" -> "AB 12 34 56 D")
      )

      status(result) should be(SEE_OTHER)
      redirectLocation(result) should be(Some("/register-to-vote/forces/service"))
    }
  }

  it should "bind successfully and redirect to the confirmation step with complete application" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(POST, "/register-to-vote/forces/edit/nino")
          .withIerSession()
          .withApplication(completeForcesApplication)
          .withFormUrlEncodedBody("NINO.NINO" -> "AB 12 34 56 D")
      )

      status(result) should be(SEE_OTHER)
      redirectLocation(result) should be(Some("/register-to-vote/forces/confirmation"))
    }
  }

  it should "display any errors on unsuccessful bind" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(POST, "/register-to-vote/forces/edit/nino").withIerSession()
      )

      status(result) should be(OK)
      contentAsString(result) should include("What is your National Insurance number?")
      contentAsString(result) should include("Please enter your National Insurance number")
      contentAsString(result) should include("/register-to-vote/forces/edit/nino")
    }
  }
}
