package uk.gov.gds.ier.transaction.crown.statement

import uk.gov.gds.ier.test.ControllerTestSuite

class StatementStepTests extends ControllerTestSuite {

  behavior of "StatementStep.get"

  it should "display the page" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(GET, "/register-to-vote/crown/statement")
          .withIerSession()
      )

      status(result) should be(OK)
      contentType(result) should be(Some("text/html"))
      contentAsString(result) should include(
        "Which of these statements applies to you?"
      )
    }
  }

  behavior of "StatementStep.post"
  it should "bind and redirect to the address step (crownServant)" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(POST, "/register-to-vote/crown/statement")
          .withIerSession()
          .withFormUrlEncodedBody("statement.crownServant" -> "true")
      )

      status(result) should be(SEE_OTHER)
      redirectLocation(result) should be(Some("/register-to-vote/crown/address/first"))
    }
  }

  it should "bind and redirect to the address step (crownPartner)" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(POST, "/register-to-vote/crown/statement")
          .withIerSession()
          .withFormUrlEncodedBody("statement.crownPartner" -> "true")
      )

      status(result) should be(SEE_OTHER)
      redirectLocation(result) should be(Some("/register-to-vote/crown/address/first"))
    }
  }

  it should "bind and redirect to the address step (councilEmployee)" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(POST, "/register-to-vote/crown/statement")
          .withIerSession()
          .withFormUrlEncodedBody("statement.councilEmployee" -> "true")
      )

      status(result) should be(SEE_OTHER)
      redirectLocation(result) should be(Some("/register-to-vote/crown/address/first"))
    }
  }

  it should "bind and redirect to the address step (councilPartner)" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(POST, "/register-to-vote/crown/statement")
          .withIerSession()
          .withFormUrlEncodedBody("statement.councilPartner" -> "true")
      )

      status(result) should be(SEE_OTHER)
      redirectLocation(result) should be(Some("/register-to-vote/crown/address/first"))
    }
  }

  it should "error because no answer given" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(POST, "/register-to-vote/crown/statement")
          .withIerSession()
      )

      status(result) should be(OK)
      contentAsString(result) should include("Please answer this question")
    }
  }

  it should "error on two me answers (councilEmployee and crownServant)" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(POST, "/register-to-vote/crown/statement")
          .withIerSession()
          .withFormUrlEncodedBody(
            "statement.councilEmployee" -> "true",
            "statement.crownServant" -> "true"
          )
      )

      status(result) should be(OK)
      contentAsString(result) should include(
        "Please select only one of these answers"
      )
    }
  }
  it should "error on two partner answers (councilPartner and crownPartner)" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(POST, "/register-to-vote/crown/statement")
          .withIerSession()
          .withFormUrlEncodedBody(
            "statement.councilPartner" -> "true",
            "statement.crownPartner" -> "true"
          )
      )

      status(result) should be(OK)
      contentAsString(result) should include(
        "Please select only one of these answers"
      )
    }
  }

  it should "bind successfully and redirect to the confirmation step with a complete application" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(POST, "/register-to-vote/crown/statement")
          .withIerSession()
          .withApplication(completeCrownApplication)
          .withFormUrlEncodedBody(
            "statement.crownServant" -> "true"
          )
      )

      status(result) should be(SEE_OTHER)
      redirectLocation(result) should be(Some("/register-to-vote/crown/confirmation"))
    }
  }
}
