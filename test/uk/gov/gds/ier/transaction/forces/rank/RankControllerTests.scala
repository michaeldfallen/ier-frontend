package uk.gov.gds.ier.transaction.forces.rank

import uk.gov.gds.ier.test.ControllerTestSuite

class RankControllerTests extends ControllerTestSuite {

  behavior of "RankController.get"
  it should "display the page" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(GET, "/register-to-vote/forces/rank").withIerSession()
      )

      status(result) should be(OK)
      contentType(result) should be(Some("text/html"))
      contentAsString(result) should include("What is your service number?")
      contentAsString(result) should include("<form action=\"/register-to-vote/forces/rank\"")
    }
  }

  behavior of "RankController.post"
  it should "bind successfully and redirect to the next step" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(POST, "/register-to-vote/forces/rank")
          .withIerSession()
          .withFormUrlEncodedBody(
            "rank.serviceNumber" -> "123456",
            "rank.rank" -> "Captain")
      )

      status(result) should be(SEE_OTHER)
      redirectLocation(result) should be(Some("/register-to-vote/forces/contact-address"))
    }
  }



  it should "bind successfully and redirect to the confirmation step with a complete application" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(POST, "/register-to-vote/forces/rank")
          .withIerSession()
          .withApplication(completeForcesApplication)
          .withFormUrlEncodedBody(
            "rank.serviceNumber" -> "123456",
            "rank.rank" -> "Captain")
      )

      status(result) should be(SEE_OTHER)
      redirectLocation(result) should be(Some("/register-to-vote/forces/confirmation"))
    }
  }

  it should "display any errors on unsuccessful bind" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(POST, "/register-to-vote/forces/rank").withIerSession()
      )

      status(result) should be(OK)
      contentAsString(result) should include("What is your service number?")
      contentAsString(result) should include("Please answer this question")
      contentAsString(result) should include("<form action=\"/register-to-vote/forces/rank\"")
    }
  }

  behavior of "RankController.editGet"
  it should "display the page" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(GET, "/register-to-vote/forces/edit/rank").withIerSession()
      )

      status(result) should be(OK)
      contentType(result) should be(Some("text/html"))
      contentAsString(result) should include("What is your service number?")
      contentAsString(result) should include("<form action=\"/register-to-vote/forces/edit/rank\"")
    }
  }

  behavior of "RankController.editPost"
  it should "bind successfully and redirect to the next step" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(POST, "/register-to-vote/forces/edit/rank")
          .withIerSession()
          .withFormUrlEncodedBody(
            "rank.serviceNumber" -> "123456",
            "rank.rank" -> "Captain")
      )

      status(result) should be(SEE_OTHER)
      redirectLocation(result) should be(Some("/register-to-vote/forces/contact-address"))
    }
  }

  it should "bind successfully and redirect to the confirmation step with a complete application" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(POST, "/register-to-vote/forces/edit/rank")
          .withIerSession()
          .withApplication(completeForcesApplication)
          .withFormUrlEncodedBody(
          "rank.serviceNumber" -> "123456",
          "rank.rank" -> "Captain")
      )

      status(result) should be(SEE_OTHER)
      redirectLocation(result) should be(Some("/register-to-vote/forces/confirmation"))
    }
  }

  it should "display any errors on unsuccessful bind" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(POST, "/register-to-vote/forces/edit/rank").withIerSession()
      )

      status(result) should be(OK)
      contentAsString(result) should include("What is your service number?")
      contentAsString(result) should include("Please answer this question")
      contentAsString(result) should include("<form action=\"/register-to-vote/forces/edit/rank\"")
    }
  }
}
