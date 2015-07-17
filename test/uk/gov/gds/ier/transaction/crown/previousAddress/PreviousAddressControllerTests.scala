package uk.gov.gds.ier.transaction.crown.previousAddress

import uk.gov.gds.ier.test.ControllerTestSuite

class PreviousAddressControllerTests extends ControllerTestSuite {

  behavior of "PreviousAddressController.get"
  it should "display the page" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(GET, "/register-to-vote/crown/previous-address").withIerSession()
      )

      status(result) should be(OK)
      contentType(result) should be(Some("text/html"))
      contentAsString(result) should include(
        "<form action=\"/register-to-vote/crown/previous-address\"")
      contentAsString(result) should include("" +
        "Have you changed your UK address in the last 12 months?")
    }
  }

  behavior of "PreviousAddressController.post"
  it should "bind successfully and redirect to the next step" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(POST, "/register-to-vote/crown/previous-address/select")
          .withIerSession()
          .withFormUrlEncodedBody(
            "previousAddress.movedRecently" -> "yes",
            "previousAddress.previousAddress.uprn" -> "123456789",
            "previousAddress.previousAddress.postcode" -> "SW1A 1AA"
          )
      )

      status(result) should be(SEE_OTHER)
      redirectLocation(result) should be(Some("/register-to-vote/crown/nationality"))
    }
  }

  it should "bind successfully and redirect to the confirmation step when complete Application" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(POST, "/register-to-vote/crown/previous-address/select")
          .withIerSession()
          .withApplication(completeCrownApplication)
          .withFormUrlEncodedBody(
            "previousAddress.previousAddress.uprn" -> "123456789",
            "previousAddress.previousAddress.postcode" -> "SW1A 1AA"
          )
      )

      status(result) should be(SEE_OTHER)
      redirectLocation(result) should be(Some("/register-to-vote/crown/confirmation"))
    }
  }

  it should "bind successfully and redirect to the next step with a manual address" in {
      running(FakeApplication()) {
        val Some(result) = route(
          FakeRequest(POST, "/register-to-vote/crown/previous-address/manual")
            .withIerSession()
            .withFormUrlEncodedBody(
              "previousAddress.previousAddress.manualAddress.lineOne" -> "Unit 4, Elgar Business Centre",
              "previousAddress.previousAddress.manualAddress.lineTwo" -> "Moseley Road",
              "previousAddress.previousAddress.manualAddress.lineThree" -> "Hallow",
              "previousAddress.previousAddress.manualAddress.city" -> "Worcester",
              "previousAddress.previousAddress.postcode" -> "SW1A 1AA"
          )
        )

        status(result) should be(SEE_OTHER)
        redirectLocation(result) should be(Some("/register-to-vote/crown/nationality"))
      }
    }

  it should "display any errors on unsuccessful bind" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(POST, "/register-to-vote/crown/previous-address").withIerSession()
      )

      status(result) should be(OK)
      contentAsString(result) should include("" +
        "Have you changed your UK address in the last 12 months?")
      contentAsString(result) should include("" +
        "Please answer this question")
      contentAsString(result) should include("" +
        "/register-to-vote/crown/previous-address")
    }
  }

  behavior of "Completing a prior step when this question is incomplete"
  it should "stop on this page" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(POST, "/register-to-vote/crown/address/select")
          .withIerSession()
          .withApplication(completeCrownApplication.copy(previousAddress = None))
          .withFormUrlEncodedBody(
          "address.uprn" -> "123456789",
          "address.postcode" -> "SW1A 1AA"
        )
      )

      status(result) should be(SEE_OTHER)
      redirectLocation(result) should be(Some("/register-to-vote/crown/previous-address"))
    }
  }

  behavior of "PreviousAddressController.editGet"
  it should "display the page" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(GET, "/register-to-vote/crown/edit/previous-address").withIerSession()
      )

      status(result) should be(OK)
      contentType(result) should be(Some("text/html"))
      contentAsString(result) should include("" +
        "Have you changed your UK address in the last 12 months?")
      contentAsString(result) should include("" +
        "<form action=\"/register-to-vote/crown/edit/previous-address\"")
    }
  }

  behavior of "PreviousAddressController.editPost"
  it should "bind successfully and redirect to the next step" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(POST, "/register-to-vote/crown/edit/previous-address/select")
          .withIerSession()
          .withFormUrlEncodedBody(
            "previousAddress.movedRecently" -> "yes",
            "previousAddress.previousAddress.uprn" -> "123456789",
            "previousAddress.previousAddress.postcode" -> "SW1A 1AA"
          )
      )

      status(result) should be(SEE_OTHER)
      redirectLocation(result) should be(Some("/register-to-vote/crown/nationality"))
    }
  }

  it should "bind successfully and redirect to the confirmation step when complete Application" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(POST, "/register-to-vote/crown/edit/previous-address/select")
          .withIerSession()
          .withApplication(completeCrownApplication)
          .withFormUrlEncodedBody(
            "previousAddress.movedRecently" -> "yes",
            "previousAddress.previousAddress.uprn" -> "123456789",
            "previousAddress.previousAddress.postcode" -> "SW1A 1AA"
          )
      )

      status(result) should be(SEE_OTHER)
      redirectLocation(result) should be(Some("/register-to-vote/crown/confirmation"))
    }
  }

  it should "bind successfully and redirect to the next step with a manual address" in {
      running(FakeApplication()) {
        val Some(result) = route(
          FakeRequest(POST, "/register-to-vote/crown/edit/previous-address/manual")
            .withIerSession()
            .withFormUrlEncodedBody(
            "previousAddress.movedRecently" -> "yes",
            "previousAddress.previousAddress.manualAddress.lineOne" -> "Unit 4, Elgar Business Centre",
            "previousAddress.previousAddress.manualAddress.lineTwo" -> "Moseley Road",
            "previousAddress.previousAddress.manualAddress.lineThree" -> "Hallow",
            "previousAddress.previousAddress.manualAddress.city" -> "Worcester",
            "previousAddress.previousAddress.postcode" -> "SW1A 1AA"
          )
        )

        status(result) should be(SEE_OTHER)
        redirectLocation(result) should be(Some("/register-to-vote/crown/nationality"))
      }
    }

  it should "display any errors on missing required fields for address select" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(POST, "/register-to-vote/crown/edit/previous-address/select").withIerSession()
      )

      status(result) should be(OK)
      contentAsString(result) should include("" +
        "What was your previous UK address?")
      contentAsString(result) should include("" +
        "Please answer this question")
      contentAsString(result) should include("" +
        "<form action=\"/register-to-vote/crown/edit/previous-address/select\"")
    }
  }

  it should "display any errors on missing required fields for address manual" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(POST, "/register-to-vote/crown/edit/previous-address/manual").withIerSession()
      )

      status(result) should be(OK)
      contentAsString(result) should include("" +
        "What was your previous UK address?")
      contentAsString(result) should include("" +
        "Please answer this question")
      contentAsString(result) should include("" +
        "<form action=\"/register-to-vote/crown/edit/previous-address/manual\"")
    }
  }

}
