package uk.gov.gds.ier.transaction.ordinary.previousAddress

import uk.gov.gds.ier.test.ControllerTestSuite
import uk.gov.gds.ier.model.{PartialManualAddress, PartialAddress, MovedHouseOption, PartialPreviousAddress}

class PreviousAddressFirstControllerTests extends ControllerTestSuite {

  behavior of "PreviousAddressController.get"
  it should "display the page" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(GET, "/register-to-vote/previous-address").withIerSession()
      )

      status(result) should be(OK)
      contentType(result) should be(Some("text/html"))
      contentAsString(result) should include("Question 8")
      contentAsString(result) should include(
        "<form action=\"/register-to-vote/previous-address\"")
      contentAsString(result) should include("" +
        "Have you moved out of another address in the last 12 months?")
    }
  }

  behavior of "PreviousAddressController.post"
  it should "bind successfully and redirect to the Select step for uk" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(POST, "/register-to-vote/previous-address")
          .withIerSession()
          .withFormUrlEncodedBody(
          "previousAddress.movedRecently.movedRecently" -> "from-uk"
        )
      )

      status(result) should be(SEE_OTHER)
      redirectLocation(result) should be(Some("/register-to-vote/previous-address/postcode"))
    }
  }

  it should "bind successfully and redirect to the Select step when registered abroad" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(POST, "/register-to-vote/previous-address")
          .withIerSession()
          .withFormUrlEncodedBody(
          "previousAddress.movedRecently.movedRecently" -> "from-abroad",
          "previousAddress.movedRecently.wasRegisteredWhenAbroad" -> "true"
        )
      )

      status(result) should be(SEE_OTHER)
      redirectLocation(result) should be(Some("/register-to-vote/previous-address/postcode"))
    }
  }

  it should "bind successfully and redirect to the Open register step when not registered abroad" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(POST, "/register-to-vote/previous-address")
          .withIerSession()
          .withFormUrlEncodedBody(
          "previousAddress.movedRecently.movedRecently" -> "from-abroad",
          "previousAddress.movedRecently.wasRegisteredWhenAbroad" -> "false"
        )
      )

      status(result) should be(SEE_OTHER)
      redirectLocation(result) should be(Some("/register-to-vote/open-register"))
    }
  }

  it should "bind successfully and redirect to the Open register step" in {
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

  it should "bind successfully and redirect to the confirmation step when complete Application" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(POST, "/register-to-vote/previous-address/select")
          .withIerSession()
          .withApplication(completeOrdinaryApplication)
          .withFormUrlEncodedBody(
            "previousAddress.previousAddress.uprn" -> "123456789",
            "previousAddress.previousAddress.postcode" -> "SW1A 1AA"
          )
      )

      status(result) should be(SEE_OTHER)
      redirectLocation(result) should be(Some("/register-to-vote/confirmation"))
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

  it should "display any errors on unsuccessful bind with no data" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(POST, "/register-to-vote/previous-address").withIerSession()
      )

      status(result) should be(OK)
      contentAsString(result) should include("Have you moved out of another address in the last 12 months?")
      contentAsString(result) should include("Please answer this question")
      contentAsString(result) should include("/register-to-vote/previous-address")
    }
  }

  it should "display any errors on unsuccessful bind with incomplete data" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(POST, "/register-to-vote/previous-address")
          .withIerSession()
          .withFormUrlEncodedBody(
            "previousAddress.movedRecently.movedRecently" -> "from-abroad"
        )
      )

      status(result) should be(OK)
      contentAsString(result) should include("Have you moved out of another address in the last 12 months?")
      contentAsString(result) should include("Please answer this question")
      contentAsString(result) should include("/register-to-vote/previous-address")
    }
  }

  behavior of "Completing a prior step when this question is incomplete"
  it should "stop on this page" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(POST, "/register-to-vote/country-of-residence")
          .withIerSession()
          .withApplication(completeOrdinaryApplication.copy(previousAddress = None))
          .withFormUrlEncodedBody(
          "country.residence" -> "England"
        )
      )

      status(result) should be(SEE_OTHER)
      redirectLocation(result) should be(Some("/register-to-vote/previous-address"))
    }
  }

  behavior of "PreviousAddressController.editGet"
  it should "display the page" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(GET, "/register-to-vote/edit/previous-address").withIerSession()
      )

      status(result) should be(OK)
      contentType(result) should be(Some("text/html"))
      contentAsString(result) should include("Question 8")
      contentAsString(result) should include("" +
        "Have you moved out of another address in the last 12 months?")
      contentAsString(result) should include("" +
        "<form action=\"/register-to-vote/edit/previous-address\"")
    }
  }

  behavior of "PreviousAddressController.editPost"

  it should "bind successfully and redirect to the Select address step (skipping postcode) when the address was selected before" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(POST, "/register-to-vote/edit/previous-address")
          .withIerSession()
          .withApplication(completeOrdinaryApplication.copy(
            previousAddress =
              Some(PartialPreviousAddress(
                movedRecently = Some(MovedHouseOption.MovedFromAbroadRegistered),
                previousAddress = Some(PartialAddress(
                  addressLine = Some("123 Fake Street, Fakerton"),
                  postcode = "SW1A 1AA",
                  uprn = Some("123456789"),
                  manualAddress = None))))))
          .withFormUrlEncodedBody("previousAddress.previousAddress.movedRecently.movedRecently" -> "from-uk")
      )

      status(result) should be(SEE_OTHER)
      redirectLocation(result) should be(Some("/register-to-vote/previous-address/select"))
    }
  }

  it should "bind successfully and redirect to the Select address step (skipping postcode) when the address was provided manually" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(POST, "/register-to-vote/edit/previous-address")
          .withIerSession()
          .withApplication(completeOrdinaryApplication.copy(
            previousAddress =
              Some(PartialPreviousAddress(
                movedRecently = Some(MovedHouseOption.MovedFromAbroadRegistered),
                previousAddress = Some(PartialAddress(
                  addressLine = None,
                  postcode = "SW1A 1AA",
                  uprn = None,
                  manualAddress = Some(PartialManualAddress(
                    lineOne = Some("123 Fake Street"),
                    lineTwo = Some("Nonexistent building"),
                    lineThree = Some(""),
                    city = Some("Fakerton")
                  ))
                ))
              ))
           ))
          .withFormUrlEncodedBody("previousAddress.movedRecently.movedRecently" -> "from-uk")
      )

      status(result) should be(SEE_OTHER)
      redirectLocation(result) should be(Some("/register-to-vote/previous-address/manual"))
    }
  }

  it should "bind successfully and redirect to the confirmation step when switched to not have a previous address" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(POST, "/register-to-vote/edit/previous-address")
          .withIerSession()
          .withApplication(completeOrdinaryApplication.copy(
          previousAddress =
            Some(PartialPreviousAddress(
              movedRecently = Some(MovedHouseOption.MovedFromAbroadRegistered),
              previousAddress = Some(PartialAddress(
                addressLine = None,
                postcode = "SW1A 1AA",
                uprn = None,
                manualAddress = Some(PartialManualAddress(
                  lineOne = Some("123 Fake Street"),
                  lineTwo = Some("Nonexistent building"),
                  lineThree = Some(""),
                  city = Some("Fakerton")
                ))
              ))
            ))
        ))
        .withFormUrlEncodedBody("previousAddress.movedRecently.movedRecently" -> "no")
      )

      status(result) should be(SEE_OTHER)
      redirectLocation(result) should be(Some("/register-to-vote/confirmation"))
    }
  }

  it should "bind successfully and redirect to the confirmation step when switched to previous address but not registered " in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(POST, "/register-to-vote/edit/previous-address")
          .withIerSession()
          .withApplication(completeOrdinaryApplication.copy(
          previousAddress =
            Some(PartialPreviousAddress(
              movedRecently = Some(MovedHouseOption.MovedFromUk),
              previousAddress = Some(PartialAddress(
                addressLine = None,
                postcode = "SW1A 1AA",
                uprn = None,
                manualAddress = Some(PartialManualAddress(
                  lineOne = Some("123 Fake Street"),
                  lineTwo = Some("Nonexistent building"),
                  lineThree = Some(""),
                  city = Some("Fakerton")
                ))
              ))
            ))
        ))
        .withFormUrlEncodedBody(
          "previousAddress.movedRecently.movedRecently" -> "from-abroad",
          "previousAddress.movedRecently.wasRegisteredWhenAbroad" -> "false")
      )

      status(result) should be(SEE_OTHER)
      redirectLocation(result) should be(Some("/register-to-vote/confirmation"))
    }
  }

  it should "bind successfully and redirect to the Other Address step" in {
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

  it should "bind successfully and redirect to the confirmation step when complete Application" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(POST, "/register-to-vote/edit/previous-address/select")
          .withIerSession()
          .withApplication(completeOrdinaryApplication)
          .withFormUrlEncodedBody(
            "previousAddress.previousAddress.uprn" -> "123456789",
            "previousAddress.previousAddress.postcode" -> "SW1A 1AA"
          )
      )

      status(result) should be(SEE_OTHER)
      redirectLocation(result) should be(Some("/register-to-vote/confirmation"))
    }
  }

  it should "bind successfully and redirect to the Other Address step with a manual address" in {
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

  it should "display any errors on missing required fields for address select" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(POST, "/register-to-vote/edit/previous-address/select").withIerSession()
      )

      status(result) should be(OK)
      contentAsString(result) should include("" +
        "What was your previous address?")
      contentAsString(result) should include("" +
        "Please select your address")
      contentAsString(result) should include("" +
        "<form action=\"/register-to-vote/edit/previous-address/select\"")
    }
  }

  it should "display any errors on missing required fields for address manual" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(POST, "/register-to-vote/edit/previous-address/manual").withIerSession()
      )

      status(result) should be(OK)
      contentAsString(result) should include("" +
        "What was your previous address?")
      contentAsString(result) should include("" +
        "Please answer this question")
      contentAsString(result) should include("" +
        "<form action=\"/register-to-vote/edit/previous-address/manual\"")
    }
  }

}
