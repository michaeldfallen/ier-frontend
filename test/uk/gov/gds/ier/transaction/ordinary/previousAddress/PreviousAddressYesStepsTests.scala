package uk.gov.gds.ier.transaction.ordinary.previousAddress

import uk.gov.gds.ier.test.ControllerTestSuite
import uk.gov.gds.ier.model.{PartialPreviousAddress, MovedHouseOption}

class PreviousAddressYesStepsTests extends ControllerTestSuite {

  behavior of "PreviousAddressPostcodeController"
  it should "display the page on GET" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(GET, "/register-to-vote/previous-address/postcode").withIerSession()
      )

      status(result) should be(OK)
      contentType(result) should be(Some("text/html"))
      contentAsString(result) should include(
        "What was your previous address?"
      )
      contentAsString(result) should include("Question 8 of 11")
      contentAsString(result) should include("<form action=\"/register-to-vote/previous-address/postcode\"")
    }
  }

  it should "react to your answer on the PreviousAddressFirst step" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(GET, "/register-to-vote/previous-address/postcode")
          .withIerSession()
          .withApplication(completeOrdinaryApplication.copy(
            previousAddress = Some(
              PartialPreviousAddress(Some(MovedHouseOption.MovedFromAbroadRegistered), None)
            )
          ))
      )

      status(result) should be(OK)
      contentAsString(result) should include(
        "What was your last UK address before moving abroad?"
      )
    }
  }

  it should "redirect to next step on POST with all required data" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(POST, "/register-to-vote/previous-address/postcode")
          .withIerSession()
          .withFormUrlEncodedBody(
            "previousAddress.previousAddress.postcode" -> "SW1A 1AA"
          )
      )

      status(result) should be(SEE_OTHER)
      redirectLocation(result) should be(Some("/register-to-vote/previous-address/select"))
    }
  }

  it should "stay on same postcode page and display errors on POST with missing required data" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(POST, "/register-to-vote/previous-address/postcode").withIerSession()
      )

      status(result) should be(OK)
      contentAsString(result) should include(
        "What was your previous address?"
      )
      contentAsString(result) should include("Please enter your postcode")
      contentAsString(result) should include("/register-to-vote/previous-address/postcode")
    }
  }

  behavior of "PreviousAddressSelectController"

  it should "bind successfully and redirect to the next step" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(POST, "/register-to-vote/previous-address/select")
          .withIerSession()
          .withFormUrlEncodedBody(
            "previousAddress.previousAddress.uprn" -> "123456789",
            "previousAddress.previousAddress.postcode" -> "SW1A 1AA"
          )
      )

      status(result) should be(SEE_OTHER)
      redirectLocation(result) should be(Some("/register-to-vote/open-register"))
    }
  }

  it should "bind successfully and redirect to the next step with a manual address" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(POST, "/register-to-vote/previous-address/manual")
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
      redirectLocation(result) should be(Some("/register-to-vote/open-register"))
    }
  }

  it should "bind successfully and redirect to confirmation if all other steps are complete" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(POST, "/register-to-vote/previous-address/manual")
          .withIerSession()
          .withApplication(completeOrdinaryApplication)
          .withFormUrlEncodedBody(
            "previousAddress.previousAddress.manualAddress.lineOne" -> "Unit 4, Elgar Business Centre",
            "previousAddress.previousAddress.manualAddress.lineTwo" -> "Moseley Road",
            "previousAddress.previousAddress.manualAddress.lineThree" -> "Hallow",
            "previousAddress.previousAddress.manualAddress.city" -> "Worcester",
            "previousAddress.previousAddress.postcode" -> "SW1A 1AA"
          )
      )

      status(result) should be(SEE_OTHER)
      redirectLocation(result) should be(Some("/register-to-vote/confirmation"))
    }
  }

  behavior of "PreviousAddressStep.editGet"
  it should "display the page" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(GET, "/register-to-vote/edit/previous-address/postcode").withIerSession()
      )

      status(result) should be(OK)
      contentType(result) should be(Some("text/html"))
      contentAsString(result) should include(
        "What was your previous address?"
      )
      contentAsString(result) should include("Question 8 of 11")
      contentAsString(result) should include("<form action=\"/register-to-vote/edit/previous-address/postcode\"")
    }
  }

  behavior of "PreviousAddressStep.editPost"
  it should "bind successfully and redirect to the next step" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(POST, "/register-to-vote/edit/previous-address/select")
          .withIerSession()
          .withFormUrlEncodedBody(
            "previousAddress.previousAddress.uprn" -> "123456789",
            "previousAddress.previousAddress.postcode" -> "SW1A 1AA"
          )
      )

      status(result) should be(SEE_OTHER)
      redirectLocation(result) should be(Some("/register-to-vote/open-register"))
    }
  }

  it should "bind successfully manual address and redirect to the next step" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(POST, "/register-to-vote/edit/previous-address/manual")
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
      redirectLocation(result) should be(Some("/register-to-vote/open-register"))
    }
  }

  it should "bind successfully and redirect to confirmation if all other steps are complete" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(POST, "/register-to-vote/edit/previous-address/manual")
          .withIerSession()
          .withApplication(completeOrdinaryApplication)
          .withFormUrlEncodedBody(
            "previousAddress.previousAddress.manualAddress.lineOne" -> "Unit 4, Elgar Business Centre",
            "previousAddress.previousAddress.manualAddress.lineTwo" -> "Moseley Road",
            "previousAddress.previousAddress.manualAddress.lineThree" -> "Hallow",
            "previousAddress.previousAddress.manualAddress.city" -> "Worcester",
            "previousAddress.previousAddress.postcode" -> "SW1A 1AA"
          )
      )

      status(result) should be(SEE_OTHER)
      redirectLocation(result) should be(Some("/register-to-vote/confirmation"))
    }
  }

  it should "display any errors on unsuccessful bind" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(POST, "/register-to-vote/edit/previous-address/postcode").withIerSession()
      )

      status(result) should be(OK)
      contentAsString(result) should include(
        "What was your previous address?"
      )
      contentAsString(result) should include("Please enter your postcode")
      contentAsString(result) should include("/register-to-vote/edit/previous-address/postcode")
    }
  }

  behavior of "Completing a prior step when this question is incomplete"
  it should "stop on this page" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(POST, "/register-to-vote/previous-address")
          .withIerSession()
          .withApplication(completeOrdinaryApplication.copy(previousAddress = None))
          .withFormUrlEncodedBody(
            "previousAddress.movedRecently.movedRecently" -> "from-uk"
          )
      )

      status(result) should be(SEE_OTHER)
      redirectLocation(result) should be(Some("/register-to-vote/previous-address/postcode"))
    }
  }
}
