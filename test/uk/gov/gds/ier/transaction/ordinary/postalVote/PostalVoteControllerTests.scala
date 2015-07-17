package uk.gov.gds.ier.transaction.ordinary.postalVote

import uk.gov.gds.ier.test.ControllerTestSuite

class PostalVoteControllerTests extends ControllerTestSuite {

  behavior of "PostalVoteController.get"
  it should "display the page" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(GET, "/register-to-vote/postal-vote").withIerSession()
      )

      status(result) should be(OK)
      contentType(result) should be(Some("text/html"))
      contentAsString(result) should include("Question 10")
      contentAsString(result) should include("Do you want to apply for a postal vote?")
      contentAsString(result) should include("/register-to-vote/postal-vote")
    }
  }

  behavior of "PostalVoteController.post"
  it should behave like appWithPostalVote("yes")
  it should behave like appWithPostalVote("no-vote-in-person")
  it should behave like appWithPostalVote("no-already-have")

  def appWithPostalVote(postalVoteOption: String) {
    it should s"bind successfully and redirect to the Contact step for vote option: $postalVoteOption" in {
      running(FakeApplication()) {
        val Some(result) = route(
          FakeRequest(POST, "/register-to-vote/postal-vote")
            .withIerSession()
            .withFormUrlEncodedBody(
              "postalVote.optIn" -> postalVoteOption,
              "postalVote.deliveryMethod.methodName" -> "post"
            )
        )

        status(result) should be(SEE_OTHER)
        redirectLocation(result) should be(Some("/register-to-vote/contact"))
      }
    }
  }

  it should "bind successfully and redirect to the confirmation step when complete application" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(POST, "/register-to-vote/postal-vote")
          .withIerSession()
          .withApplication(completeOrdinaryApplication)
          .withFormUrlEncodedBody(
            "postalVote.optIn" -> "yes",
            "postalVote.deliveryMethod.methodName" -> "post"
          )
      )

      status(result) should be(SEE_OTHER)
      redirectLocation(result) should be(Some("/register-to-vote/confirmation"))
    }
  }

  it should "display any errors on unsuccessful bind" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(POST, "/register-to-vote/postal-vote").withIerSession()
      )

      status(result) should be(OK)
      contentAsString(result) should include("Do you want to apply for a postal vote?")
      contentAsString(result) should include("Please answer this question")
      contentAsString(result) should include("/register-to-vote/postal-vote")
    }
  }

  behavior of "Completing a prior step when this question is incomplete"
  it should "stop on this page" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(POST, "/register-to-vote/country-of-residence")
          .withIerSession()
          .withApplication(completeOrdinaryApplication.copy(postalVote = None))
          .withFormUrlEncodedBody(
          "country.residence" -> "England"
        )
      )

      status(result) should be(SEE_OTHER)
      redirectLocation(result) should be(Some("/register-to-vote/postal-vote"))
    }
  }

  behavior of "PostalVoteController.editGet"
  it should "display the page" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(GET, "/register-to-vote/edit/postal-vote").withIerSession()
      )

      status(result) should be(OK)
      contentType(result) should be(Some("text/html"))
      contentAsString(result) should include("Question 10")
      contentAsString(result) should include("Do you want to apply for a postal vote?")
      contentAsString(result) should include("/register-to-vote/edit/postal-vote")
    }
  }

  behavior of "PostalVoteController.editPost"
  it should behave like editedAppWithPostalVote("yes")
  it should behave like editedAppWithPostalVote("no-vote-in-person")
  it should behave like editedAppWithPostalVote("no-already-have")

  def editedAppWithPostalVote(postalVoteOption: String) {
    it should s"bind successfully and redirect to the incomplete Contact step for vote option: $postalVoteOption" in {
      running(FakeApplication()) {
        val Some(result) = route(
          FakeRequest(POST, "/register-to-vote/edit/postal-vote")
            .withIerSession()
            .withFormUrlEncodedBody(
              "postalVote.optIn" -> postalVoteOption,
              "postalVote.deliveryMethod.methodName" -> "post"
            )
        )

        status(result) should be(SEE_OTHER)
        redirectLocation(result) should be(Some("/register-to-vote/contact"))
      }
    }

    it should s"bind successfully and redirect to the Confirmation step when complete application for vote option: $postalVoteOption" in {
      running(FakeApplication()) {
        val Some(result) = route(
          FakeRequest(POST, "/register-to-vote/edit/postal-vote")
            .withIerSession()
            .withApplication(completeOrdinaryApplication)
            .withFormUrlEncodedBody(
              "postalVote.optIn" -> postalVoteOption,
              "postalVote.deliveryMethod.methodName" -> "post"
            )
        )

        status(result) should be(SEE_OTHER)
        redirectLocation(result) should be(Some("/register-to-vote/confirmation"))
      }
    }
  }
}
