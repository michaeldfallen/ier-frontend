package uk.gov.gds.ier.transaction.forces.nationality

import uk.gov.gds.ier.test.ControllerTestSuite

class NationalityControllerTests extends ControllerTestSuite {

  behavior of "NationalityController.get"
  it should "display the page" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(GET, "/register-to-vote/forces/nationality").withIerSession()
      )

      status(result) should be(OK)
      contentType(result) should be(Some("text/html"))

      contentAsString(result) should include("What is your nationality?")
      contentAsString(result) should include("/register-to-vote/forces/nationality")
    }
  }

  behavior of "NationalityController.post"
  it should "bind successfully and redirect to the next step" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(POST, "/register-to-vote/forces/nationality")
          .withIerSession()
          .withFormUrlEncodedBody(
            "nationality.british" -> "true",
            "nationality.hasOtherCountry" -> "true",
            "nationality.otherCountries[0]" -> "France")
      )

      status(result) should be(SEE_OTHER)
      redirectLocation(result) should be(Some("/register-to-vote/forces/date-of-birth"))
    }
  }

  it should "bind successfully and redirect to the confirmation step with complete application" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(POST, "/register-to-vote/forces/nationality")
          .withIerSession()
          .withApplication(completeForcesApplication)
          .withFormUrlEncodedBody(
            "nationality.british" -> "true",
            "nationality.hasOtherCountry" -> "true",
            "nationality.otherCountries[0]" -> "France")
      )

      status(result) should be(SEE_OTHER)
      redirectLocation(result) should be(Some("/register-to-vote/forces/confirmation"))
    }
  }

  it should "redirect to no-franchise page with a country with no right to vote in UK" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(POST, "/register-to-vote/forces/nationality")
          .withIerSession()
          .withFormUrlEncodedBody(
            "nationality.hasOtherCountry" -> "true",
            "nationality.otherCountries[0]" -> "Japan")
      )

      status(result) should be(SEE_OTHER)
      redirectLocation(result) should be(Some("/register-to-vote/exit/no-franchise"))
    }
  }

  it should "display any errors on unsuccessful bind (no content)" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(POST, "/register-to-vote/forces/nationality").withIerSession()
      )

      status(result) should be(OK)
      contentAsString(result) should include("What is your nationality?")
      contentAsString(result) should include("Please answer this question")
      contentAsString(result) should include("/register-to-vote/forces/nationality")
    }
  }

  it should "display any errors on unsuccessful bind (bad other country)" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(POST, "/register-to-vote/forces/nationality")
          .withIerSession()
          .withFormUrlEncodedBody(
            "nationality.british" -> "true",
            "nationality.hasOtherCountry" -> "true",
            "nationality.otherCountries[0]" -> "BLARGHHUH")
      )

      status(result) should be(OK)
      contentAsString(result) should include("What is your nationality?")
      contentAsString(result) should include("This is not a valid entry")
      contentAsString(result) should include("/register-to-vote/forces/nationality")
    }
  }

  behavior of "Completing a prior step when this question is incomplete"
  it should "stop on this page" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(POST, "/register-to-vote/forces/previous-address/select")
          .withIerSession()
          .withApplication(completeForcesApplication.copy(nationality = None))
          .withFormUrlEncodedBody(
          "previousAddress.uprn" -> "123456789",
          "previousAddress.postcode" -> "SW1A 1AA"
        )
      )

      status(result) should be(SEE_OTHER)
      redirectLocation(result) should be(Some("/register-to-vote/forces/nationality"))
    }
  }

  behavior of "NationalityController.editGet"
  it should "display the page" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(GET, "/register-to-vote/forces/edit/nationality").withIerSession()
      )

      status(result) should be(OK)
      contentType(result) should be(Some("text/html"))

      contentAsString(result) should include("What is your nationality?")
      contentAsString(result) should include("/register-to-vote/forces/edit/nationality")
    }
  }

  behavior of "NationalityController.editPost"
  it should "bind successfully and redirect to the next step" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(POST, "/register-to-vote/forces/edit/nationality")
          .withIerSession()
          .withFormUrlEncodedBody(
            "nationality.british" -> "true",
            "nationality.hasOtherCountry" -> "true",
            "nationality.otherCountries[0]" -> "France")
      )

      status(result) should be(SEE_OTHER)
      redirectLocation(result) should be(Some("/register-to-vote/forces/date-of-birth"))
    }
  }

  it should "bind successfully and redirect to the confirmation step with complete application" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(POST, "/register-to-vote/forces/edit/nationality")
          .withIerSession()
          .withApplication(completeForcesApplication)
          .withFormUrlEncodedBody(
            "nationality.british" -> "true",
            "nationality.hasOtherCountry" -> "true",
            "nationality.otherCountries[0]" -> "France")
      )

      status(result) should be(SEE_OTHER)
      redirectLocation(result) should be(Some("/register-to-vote/forces/confirmation"))
    }
  }

  it should "redirect to no-franchise page with a country with no right to vote in UK" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(POST, "/register-to-vote/forces/edit/nationality")
          .withIerSession()
          .withFormUrlEncodedBody(
            "nationality.hasOtherCountry" -> "true",
            "nationality.otherCountries[0]" -> "Japan")
      )

      status(result) should be(SEE_OTHER)
      redirectLocation(result) should be(Some("/register-to-vote/exit/no-franchise"))
    }
  }

  it should "display any errors on unsuccessful bind (no content)" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(POST, "/register-to-vote/forces/edit/nationality").withIerSession()
      )

      status(result) should be(OK)
      contentAsString(result) should include("What is your nationality?")
      contentAsString(result) should include("Please answer this question")
      contentAsString(result) should include("/register-to-vote/forces/edit/nationality")
    }
  }

  it should "display any errors on unsuccessful bind (bad other country)" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(POST, "/register-to-vote/forces/edit/nationality")
          .withIerSession()
          .withFormUrlEncodedBody(
            "nationality.british" -> "true",
            "nationality.hasOtherCountry" -> "true",
            "nationality.otherCountries[0]" -> "BLARGHHUH")
      )

      status(result) should be(OK)
      contentAsString(result) should include("What is your nationality?")
      contentAsString(result) should include("This is not a valid entry")
      contentAsString(result) should include("/register-to-vote/forces/edit/nationality")
    }
  }
}
