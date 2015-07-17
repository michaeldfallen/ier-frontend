package uk.gov.gds.ier.transaction.crown.applicationFormVote

import uk.gov.gds.ier.test.ControllerTestSuite

class CrownProxyVoteStepTests extends ControllerTestSuite {

  behavior of "ProxyVoteStep.get"
  it should "display the page" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(GET, "/register-to-vote/crown/proxy-vote").withIerSession()
      )

      status(result) should be(OK)
      contentType(result) should be(Some("text/html"))
      contentAsString(result) should include(
        "Do you want us to send you a proxy vote application form?")
      contentAsString(result) should include("/register-to-vote/crown/proxy-vote")
    }
  }

  behavior of "ProxyVoteStep.post"
  it should "bind successfully and redirect to the next step" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(POST, "/register-to-vote/crown/proxy-vote")
          .withIerSession()
          .withFormUrlEncodedBody(
            "postalOrProxyVote.optIn" -> "true",
            "postalOrProxyVote.deliveryMethod.methodName" -> "email",
            "postalOrProxyVote.deliveryMethod.emailAddress" -> "mail@test.co.uk",
            "postalOrProxyVote.voteType" -> "by-proxy"
          )
      )

      status(result) should be(SEE_OTHER)
      redirectLocation(result) should be(Some("/register-to-vote/crown/contact"))
    }
  }

  it should "display any errors on unsuccessful bind" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(POST, "/register-to-vote/crown/proxy-vote").withIerSession()
      )

      status(result) should be(OK)
      contentAsString(result) should include(
        "Do you want us to send you a proxy vote application form?")
      contentAsString(result) should include("Please answer this question")
      contentAsString(result) should include("/register-to-vote/crown/proxy-vote")
    }
  }

  behavior of "ProxyVoteStep.editGet"
  it should "display the page" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(GET, "/register-to-vote/crown/edit/proxy-vote").withIerSession()
      )

      status(result) should be(OK)
      contentType(result) should be(Some("text/html"))
      contentAsString(result) should include(
        "Do you want us to send you a proxy vote application form?")
      contentAsString(result) should include("/register-to-vote/crown/edit/proxy-vote")
    }
  }

  behavior of "ProxyVoteStep.editPost"
  it should "bind successfully and redirect to the next step" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(POST, "/register-to-vote/crown/edit/proxy-vote")
          .withIerSession()
          .withFormUrlEncodedBody(
            "postalOrProxyVote.optIn" -> "true",
            "postalOrProxyVote.deliveryMethod.methodName" -> "email",
            "postalOrProxyVote.deliveryMethod.emailAddress" -> "mail@test.co.uk",
            "postalOrProxyVote.voteType" -> "by-proxy"
        )
      )

      status(result) should be(SEE_OTHER)
      redirectLocation(result) should be(Some("/register-to-vote/crown/contact"))
    }
  }

  it should "display any errors on unsuccessful bind" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(POST, "/register-to-vote/crown/edit/proxy-vote").withIerSession()
      )

      status(result) should be(OK)
      contentAsString(result) should include(
        "Do you want us to send you a proxy vote application form?")
      contentAsString(result) should include("Please answer this question")
      contentAsString(result) should include("/register-to-vote/crown/edit/proxy-vote")
    }
  }
}
