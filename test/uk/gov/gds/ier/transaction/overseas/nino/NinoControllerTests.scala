package uk.gov.gds.ier.transaction.overseas.nino

import uk.gov.gds.ier.test.ControllerTestSuite

class NinoControllerTests extends ControllerTestSuite {

  behavior of "NinoController.get"
  it should "display the page" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(GET, "/register-to-vote/overseas/nino").withIerSession()
      )

      status(result) should be(OK)
      contentType(result) should be(Some("text/html"))
      contentAsString(result) should include("What is your National Insurance number?")
      contentAsString(result) should include("/register-to-vote/overseas/nino")
    }
  }

  behavior of "NinoController.post"
  it should "bind successfully and redirect to the Previous Name step" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(POST, "/register-to-vote/overseas/nino")
          .withIerSession()
          .withFormUrlEncodedBody("NINO.NINO" -> "AB 12 34 56 D")
      )

      status(result) should be(SEE_OTHER)
      redirectLocation(result) should be(Some("/register-to-vote/overseas/address"))
    }
  }

  it should "bind successfully and redirect to the confirmation step with complete application" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(POST, "/register-to-vote/overseas/nino")
          .withIerSession()
          .withApplication(completeOverseasApplication)
          .withFormUrlEncodedBody("NINO.NINO" -> "AB 12 34 56 D")
      )

      status(result) should be(SEE_OTHER)
      redirectLocation(result) should be(Some("/register-to-vote/overseas/confirmation"))
    }
  }

  it should "display any errors on unsuccessful bind" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(POST, "/register-to-vote/overseas/nino").withIerSession()
      )

      status(result) should be(OK)
      contentAsString(result) should include("What is your National Insurance number?")
      contentAsString(result) should include("Please enter your National Insurance number")
      contentAsString(result) should include("/register-to-vote/overseas/nino")
    }
  }

  behavior of "NinoController.editGet"
  it should "display the page" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(GET, "/register-to-vote/overseas/edit/nino").withIerSession()
      )

      status(result) should be(OK)
      contentType(result) should be(Some("text/html"))
      contentAsString(result) should include("What is your National Insurance number?")
      contentAsString(result) should include("/register-to-vote/overseas/edit/nino")
    }
  }

  behavior of "NinoController.editPost"
  it should "bind successfully and redirect to the previous step" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(POST, "/register-to-vote/overseas/edit/nino")
          .withIerSession()
          .withFormUrlEncodedBody("NINO.NINO" -> "AB 12 34 56 D")
      )

      status(result) should be(SEE_OTHER)
      redirectLocation(result) should be(Some("/register-to-vote/overseas/address"))
    }
  }

  it should "bind successfully and redirect to the confirmation step with complete application" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(POST, "/register-to-vote/overseas/edit/nino")
          .withIerSession()
          .withApplication(completeOverseasApplication)
          .withFormUrlEncodedBody("NINO.NINO" -> "AB 12 34 56 D")
      )

      status(result) should be(SEE_OTHER)
      redirectLocation(result) should be(Some("/register-to-vote/overseas/confirmation"))
    }
  }

  it should "display any errors on unsuccessful bind" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(POST, "/register-to-vote/overseas/edit/nino").withIerSession()
      )

      status(result) should be(OK)
      contentAsString(result) should include("What is your National Insurance number?")
      contentAsString(result) should include("Please enter your National Insurance number")
      contentAsString(result) should include("/register-to-vote/overseas/edit/nino")
    }
  }
}
