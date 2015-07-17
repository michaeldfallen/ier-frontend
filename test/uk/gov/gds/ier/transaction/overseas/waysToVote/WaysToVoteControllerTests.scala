package uk.gov.gds.ier.transaction.overseas.waysToVote

import uk.gov.gds.ier.test.ControllerTestSuite

class WaysToVoteControllerTests extends ControllerTestSuite {

  behavior of "WaysToVoteController.get"
  it should "display the page" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(GET, "/register-to-vote/overseas/ways-to-vote").withIerSession()
      )
      status(result) should be(OK)
      contentType(result) should be(Some("text/html"))
      contentAsString(result) should include("How do you want to vote?")
    }
  }

  behavior of "WaysToVoteController.post"
  it should "redirect to postal vote step when submitted data indicate by-post way" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(POST, "/register-to-vote/overseas/ways-to-vote")
          .withIerSession()
          .withFormUrlEncodedBody(
            "waysToVote.wayType" -> "by-post")
      )
      status(result) should be(SEE_OTHER)
      redirectLocation(result) should be(Some("/register-to-vote/overseas/postal-vote"))
    }
  }

  behavior of "WaysToVoteController.post"
  it should "redirect to postal vote step when submitted data indicate by-proxy way" in {
    // proxy voting is handled by the same controller as postal vote, hence same next step URL
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(POST, "/register-to-vote/overseas/ways-to-vote")
          .withIerSession()
          .withFormUrlEncodedBody(
            "waysToVote.wayType" -> "by-proxy")
      )
      status(result) should be(SEE_OTHER)
      redirectLocation(result) should be(Some("/register-to-vote/overseas/proxy-vote"))
    }
  }

  behavior of "WaysToVoteController.post"
  it should "redirect to Contact Step when submitted data indicate in-person way" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(POST, "/register-to-vote/overseas/ways-to-vote")
          .withIerSession()
          .withFormUrlEncodedBody(
            "waysToVote.wayType" -> "in-person")
      )
      status(result) should be(SEE_OTHER)
      redirectLocation(result) should be(Some("/register-to-vote/overseas/contact"))
    }
  }

  behavior of "WaysToVoteController.editGet"
  it should "display the page" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(GET, "/register-to-vote/overseas/edit/ways-to-vote").withIerSession()
      )
      status(result) should be(OK)
      contentType(result) should be(Some("text/html"))
      contentAsString(result) should include("How do you want to vote?")
    }
  }

  behavior of "WaysToVoteController.editPost"
  it should "redirect to postal vote step when submitted data indicate by-post way" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(POST, "/register-to-vote/overseas/edit/ways-to-vote")
          .withIerSession()
          .withFormUrlEncodedBody(
            "waysToVote.wayType" -> "by-post")
      )
      status(result) should be(SEE_OTHER)
      redirectLocation(result) should be(Some("/register-to-vote/overseas/postal-vote"))
    }
  }

  it should "bind successfully and redirect to the postal vote step with a complete application " +
    "no matter the user changes in the ways to vote step or not" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(POST, "/register-to-vote/overseas/edit/ways-to-vote")
          .withIerSession()
          .withApplication(completeOverseasApplication)
          .withFormUrlEncodedBody(
            "waysToVote.wayType" -> "by-post")
      )

      status(result) should be(SEE_OTHER)
      redirectLocation(result) should be(Some("/register-to-vote/overseas/postal-vote"))
    }
  }

  behavior of "WaysToVoteController.get"
  it should "display any errors on unsuccessful bind" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(POST, "/register-to-vote/overseas/ways-to-vote").withIerSession()
      )
      status(result) should be(OK)
      contentType(result) should be(Some("text/html"))
      contentAsString(result) should include("How do you want to vote?")

      contentAsString(result) should include("Please answer this question")
    }
  }

  behavior of "WaysToVoteController.editGet"
  it should "display any errors on unsuccessful bind" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(POST, "/register-to-vote/overseas/edit/ways-to-vote").withIerSession()
      )
      status(result) should be(OK)
      contentType(result) should be(Some("text/html"))
      contentAsString(result) should include("How do you want to vote?")

      contentAsString(result) should include("Please answer this question")
    }
  }
  
  behavior of "OpenRegisterController.post"
  it should "bypass the waysToVote and postalOrProxy step and redirect to Contact Step when " +
    "completing the open register step" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(POST, "/register-to-vote/overseas/open-register")
          .withIerSession()
          .withApplication(completeCrownApplication.copy(contact = None))
          .withFormUrlEncodedBody("openRegister.optIn" -> "true")
      )
      status(result) should be(SEE_OTHER)
      redirectLocation(result) should be(Some("/register-to-vote/overseas/contact"))
    }
  }

  it should "bypass Contact step if application complete and WaysToVote = InPerson" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(POST, "/register-to-vote/overseas/ways-to-vote")
          .withIerSession()
          .withApplication(completeOverseasApplication)
          .withFormUrlEncodedBody("waysToVote.wayType" -> "in-person")
      )
      status(result) should be(SEE_OTHER)
      redirectLocation(result) should be(Some("/register-to-vote/overseas/confirmation"))
    }
  }
}
