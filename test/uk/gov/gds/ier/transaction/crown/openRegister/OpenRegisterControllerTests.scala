package uk.gov.gds.ier.transaction.crown.openRegister

import uk.gov.gds.ier.test.ControllerTestSuite

class OpenRegisterControllerTests extends ControllerTestSuite {

  behavior of "OpenRegisterController.get"
  it should "display the page" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(GET, "/register-to-vote/crown/open-register").withIerSession()
      )

      status(result) should be(OK)
      contentType(result) should be(Some("text/html"))
      contentAsString(result) should include("Do you want to include your name and address on the open register?")
      contentAsString(result) should include("/register-to-vote/crown/open-register")
    }
  }

  behavior of "OpenRegisterController.post"
  it should "bind successfully and redirect to the Ways to Vote step" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(POST, "/register-to-vote/crown/open-register")
          .withIerSession()
          .withFormUrlEncodedBody("openRegister.optIn" -> "true")
      )

      status(result) should be(SEE_OTHER)
      redirectLocation(result) should be(Some("/register-to-vote/crown/ways-to-vote"))
    }
  }

  it should "bind successfully and redirect to the confirmation step with complete Application" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(POST, "/register-to-vote/crown/open-register")
          .withIerSession()
          .withApplication(completeCrownApplication)
          .withFormUrlEncodedBody("openRegister.optIn" -> "true")
      )

      status(result) should be(SEE_OTHER)
      redirectLocation(result) should be(Some("/register-to-vote/crown/confirmation"))
    }
  }

  it should "not display any errors because we are evil dark patterny" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(POST, "/register-to-vote/crown/open-register").withIerSession()
      )

      status(result) should be(SEE_OTHER)
      redirectLocation(result) should be(Some("/register-to-vote/crown/ways-to-vote"))
    }
  }

//  behavior of "Completing a prior step when this question is incomplete"
//  it should "stop on this page" in {
//    running(FakeApplication()) {
//      val Some(result) = route(
//        FakeRequest(POST, "/register-to-vote/crown/contact-address")
//          .withIerSession()
//          .withApplication(completeCrownApplication.copy(openRegisterOptin = None))
//          .withFormUrlEncodedBody(
//          "contactAddress.contactAddressType" -> "uk"
//        )
//      )
//
//      status(result) should be(SEE_OTHER)
//      redirectLocation(result) should be(Some("/register-to-vote/crown/open-register"))
//    }
//  }

  behavior of "OpenRegisterController.editGet"
  it should "display the page" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(GET, "/register-to-vote/crown/edit/open-register").withIerSession()
      )

      status(result) should be(OK)
      contentType(result) should be(Some("text/html"))
      contentAsString(result) should include("Do you want to include your name and address on the open register?")
      contentAsString(result) should include("/register-to-vote/crown/edit/open-register")
    }
  }

  behavior of "OpenRegisterController.editPost"
  it should "bind successfully and redirect to the Previous Name step" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(POST, "/register-to-vote/crown/edit/open-register")
          .withIerSession()
          .withFormUrlEncodedBody("openRegister.optIn" -> "true")
      )

      status(result) should be(SEE_OTHER)
      redirectLocation(result) should be(Some("/register-to-vote/crown/ways-to-vote"))
    }
  }

  it should "bind successfully and redirect to the confirmation step with complete Application" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(POST, "/register-to-vote/crown/edit/open-register")
          .withIerSession()
          .withApplication(completeCrownApplication)
          .withFormUrlEncodedBody("openRegister.optIn" -> "true")
      )

      status(result) should be(SEE_OTHER)
      redirectLocation(result) should be(Some("/register-to-vote/crown/confirmation"))
    }
  }

  it should "not display any errors because we are evil dark patterny" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(POST, "/register-to-vote/crown/edit/open-register").withIerSession()
      )

      status(result) should be(SEE_OTHER)
      redirectLocation(result) should be(Some("/register-to-vote/crown/ways-to-vote"))
    }
  }
}
