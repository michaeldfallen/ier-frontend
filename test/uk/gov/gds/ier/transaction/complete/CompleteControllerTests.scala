package uk.gov.gds.ier.transaction.complete

import uk.gov.gds.ier.test.ControllerTestSuite
import uk.gov.gds.ier.controller.routes._
import play.api.test.FakeApplication
import uk.gov.gds.ier.service.apiservice.EroAuthorityDetails

class CompleteControllerTests extends ControllerTestSuite {

  behavior of "CompleteController.get"
  it should "display the page with link back to start when user indicated that has other address" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(GET, "/register-to-vote/complete")
          .withCompleteCookie(CompleteCookie(
            refNum = "123457689013",
            authority = Some(EroAuthorityDetails(
              name = "Hornsey Council",
              urls = List(),
              email = None,
              phone = None,
              addressLine1 = None,
              addressLine2 = None,
              addressLine3 = None,
              addressLine4 = None,
              postcode = None
            )),
            hasOtherAddress = true,
            backToStartUrl = "/register-to-vote/start",
            showEmailConfirmation = true,
            showBirthdayBunting = false
          ))
          .withIerSession()
      )

      status(result) should be(OK)
      contentType(result) should be(Some("text/html"))
      val renderedOutput = contentAsString(result)

      renderedOutput should include("123457689013")
      renderedOutput should include("/register-to-vote/start")
      renderedOutput should not include("Happy Birthday")
    }
  }

  it should "display the page without link back to start when user did not indicated other address" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(GET, "/register-to-vote/complete")
          .withCompleteCookie(CompleteCookie(
            refNum = "123457689013",
            authority = Some(EroAuthorityDetails(
              name = "Hornsey Council",
              urls = List(),
              email = None,
              phone = None,
              addressLine1 = None,
              addressLine2 = None,
              addressLine3 = None,
              addressLine4 = None,
              postcode = None
            )),
            hasOtherAddress = false,
            backToStartUrl = "/register-to-vote/start",
            showEmailConfirmation = true,
            showBirthdayBunting = false
          ))
          .withIerSession()
      )

      status(result) should be(OK)
      contentType(result) should be(Some("text/html"))
      val renderedOutput = contentAsString(result)

      renderedOutput should include("123457689013")
      renderedOutput should not include("/register-to-vote/start")
      renderedOutput should not include("Happy Birthday")
    }
  }

  it should "display the page with email confirmation info" in runningApp {
    val Some(result) = route(
      FakeRequest(GET, "/register-to-vote/complete")
        .withCompleteCookie(CompleteCookie(
          refNum = "123457689013",
          authority = Some(EroAuthorityDetails(
            name = "Hornsey Council",
            urls = List(),
            email = None,
            phone = None,
            addressLine1 = None,
            addressLine2 = None,
            addressLine3 = None,
            addressLine4 = None,
            postcode = None
          )),
          hasOtherAddress = true,
          backToStartUrl = "/register-to-vote/start",
          showEmailConfirmation = true,
          showBirthdayBunting = false
        ))
        .withIerSession()
    )

    status(result) should be(OK)
    contentType(result) should be(Some("text/html"))
    val renderedOutput = contentAsString(result)

    renderedOutput should include("We have sent you an acknowledgement email.")
    renderedOutput should not include("Happy Birthday")
  }

  it should "display the page without email confirmation info" in runningApp {
    val Some(result) = route(
      FakeRequest(GET, "/register-to-vote/complete")
        .withCompleteCookie(CompleteCookie(
          refNum = "123457689013",
          authority = Some(EroAuthorityDetails(
            name = "Hornsey Council",
            urls = List(),
            email = None,
            phone = None,
            addressLine1 = None,
            addressLine2 = None,
            addressLine3 = None,
            addressLine4 = None,
            postcode = None
          )),
          hasOtherAddress = true,
          backToStartUrl = "/register-to-vote/start",
          showEmailConfirmation = false,
          showBirthdayBunting = false
        ))
        .withIerSession()
    )

    status(result) should be(OK)
    contentType(result) should be(Some("text/html"))
    val renderedOutput = contentAsString(result)

    renderedOutput should not include("We have sent you a confirmation email.")
    renderedOutput should not include("Happy Birthday")
  }

  it should "display happy birthday bunting" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(GET, "/register-to-vote/complete")
          .withCompleteCookie(CompleteCookie(
            refNum = "123457689013",
            authority = Some(EroAuthorityDetails(
              name = "Hornsey Council",
              urls = List(),
              email = None,
              phone = None,
              addressLine1 = None,
              addressLine2 = None,
              addressLine3 = None,
              addressLine4 = None,
              postcode = None
            )),
            hasOtherAddress = true,
            backToStartUrl = "/register-to-vote/start",
            showEmailConfirmation = false,
            showBirthdayBunting = true
          ))
          .withIerSession()
      )

      status(result) should be(OK)
      contentType(result) should be(Some("text/html"))
      val renderedOutput = contentAsString(result)

      renderedOutput should include("Happy Birthday")
      renderedOutput should include("/register-to-vote/start")
    }
  }
}
