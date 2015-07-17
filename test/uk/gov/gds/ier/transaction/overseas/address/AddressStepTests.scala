package uk.gov.gds.ier.transaction.overseas.address

import uk.gov.gds.ier.test.ControllerTestSuite
import org.joda.time.DateTime

class AddressStepTests extends ControllerTestSuite {

  behavior of "AddressStep.get"
  it should "display the page" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(GET, "/register-to-vote/overseas/address").withIerSession()
      )

      status(result) should be(OK)
      contentType(result) should be(Some("text/html"))
      contentAsString(result) should include("What is your overseas address?")
      contentAsString(result) should include("/register-to-vote/overseas/address")
    }
  }

  behavior of "AddressStep.post"
  it should "bind successfully and redirect to the Open Register step if all the fields are completed in this page" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(POST, "/register-to-vote/overseas/address")
          .withIerSession()
          .withFormUrlEncodedBody(
          "overseasAddress.country" -> "United Kingdom",
          "overseasAddress.addressLine1" -> "some street, some borough, postcode")
      )

      status(result) should be(SEE_OTHER)
      redirectLocation(result) should be(Some("/register-to-vote/overseas/open-register"))
    }
  }

  it should "display any errors on unsuccessful bind" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(POST, "/register-to-vote/overseas/address").withIerSession()
      )

      status(result) should be(OK)
      contentAsString(result) should include("Please enter your country")
      contentAsString(result) should include("Please enter your address")
      contentAsString(result) should include("/register-to-vote/overseas/address")
    }
  }

  behavior of "AddressStep.editGet"
  it should "display the edit page" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(GET, "/register-to-vote/overseas/edit/address").withIerSession()
      )

      status(result) should be(OK)
      contentType(result) should be(Some("text/html"))
      contentAsString(result) should include("What is your overseas address?")
      contentAsString(result) should include("/register-to-vote/overseas/edit/address")
    }
  }

  behavior of "AddressStep.editPost"
  it should "bind successfully and redirect to the Open Register step if all the fiedls are entered" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(POST, "/register-to-vote/overseas/edit/address")
          .withIerSession()
          .withFormUrlEncodedBody(
          "overseasAddress.country" -> "United Kingdom",
          "overseasAddress.addressLine1" -> "some street, some borough, postcode")
      )

      status(result) should be(SEE_OTHER)
      redirectLocation(result) should be(Some("/register-to-vote/overseas/open-register"))
    }
  }

  it should "display any errors on unsuccessful bind" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(POST, "/register-to-vote/overseas/edit/address").withIerSession()
      )

      status(result) should be(OK)
      contentAsString(result) should include("Please enter your country")
      contentAsString(result) should include("Please enter your address")
      contentAsString(result) should include("/register-to-vote/overseas/edit/address")
    }
  }

  behavior of "AddressStep.post when complete application"
  it should "bind successfully and redirect to the confirmation step if all the fields are entered" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(POST, "/register-to-vote/overseas/address")
          .withIerSession()
          .withApplication(completeOverseasApplication)
          .withFormUrlEncodedBody(
          "overseasAddress.country" -> "United Kingdom",
          "overseasAddress.addressLine1" -> "some street, some borough, postcode")
      )

      status(result) should be(SEE_OTHER)
      redirectLocation(result) should be(Some("/register-to-vote/overseas/confirmation"))
    }
  }

  behavior of "AddressStep.editPost when complete application"
  it should "bind successfully and redirect to the confirmation step if all the fields are entered" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(POST, "/register-to-vote/overseas/edit/address")
          .withIerSession()
          .withApplication(completeOverseasApplication)
          .withFormUrlEncodedBody(
          "overseasAddress.country" -> "United Kingdom",
          "overseasAddress.addressLine1" -> "some street, some borough, postcode")
      )

      status(result) should be(SEE_OTHER)
      redirectLocation(result) should be(Some("/register-to-vote/overseas/confirmation"))
    }
  }
}
