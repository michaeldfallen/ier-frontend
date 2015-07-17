package uk.gov.gds.ier.transaction.forces.service

import uk.gov.gds.ier.test.ControllerTestSuite

class ServiceControllerTests extends ControllerTestSuite {

  behavior of "ServiceController.get"
  it should "display the page" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(GET, "/register-to-vote/forces/service").withIerSession()
      )

      status(result) should be(OK)
      contentType(result) should be(Some("text/html"))
      contentAsString(result) should include("Which of the services are you in?")
      contentAsString(result) should include("/register-to-vote/forces/service")
    }
  }

  behavior of "ServiceController.post"
  it should "bind successfully and redirect to the next step" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(POST, "/register-to-vote/forces/service")
          .withIerSession()
          .withFormUrlEncodedBody(
            "service.serviceName" -> "British Army",
            "service.regiment" -> "my regiment"
        )
      )

      status(result) should be(SEE_OTHER)
      redirectLocation(result) should be(Some("/register-to-vote/forces/rank"))
    }
  }

  it should "bind successfully and redirect to the confirmation step with complete application" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(POST, "/register-to-vote/forces/service")
          .withIerSession()
          .withApplication(completeForcesApplication)
          .withFormUrlEncodedBody(
            "service.serviceName" -> "British Army",
            "service.regiment" -> "my regiment"
        )
      )

      status(result) should be(SEE_OTHER)
      redirectLocation(result) should be(Some("/register-to-vote/forces/confirmation"))
    }
  }

  it should "display any errors on unsuccessful bind" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(POST, "/register-to-vote/forces/service").withIerSession()
      )

      status(result) should be(OK)
      contentAsString(result) should include("Which of the services are you in?")
      contentAsString(result) should include("Please answer this question")
      contentAsString(result) should include("/register-to-vote/forces/service")
    }
  }

}
