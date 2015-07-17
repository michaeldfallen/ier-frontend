package uk.gov.gds.ier.transaction.forces.address

import uk.gov.gds.ier.test.ControllerTestSuite
import uk.gov.gds.ier.model.HasAddressOption

class AddressFirstStepTests extends ControllerTestSuite {

  behavior of "AddressFirstStep.get"
  it should "display the page" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(GET, "/register-to-vote/forces/address/first").withIerSession()
      )

      status(result) should be(OK)
      contentType(result) should be(Some("text/html"))
      contentAsString(result) should include(
        "Do you have a UK address?"
      )
      contentAsString(result) should include("<form action=\"/register-to-vote/forces/address/first\"")
    }
  }

  behavior of "AddressFirstStep.post"
  it should "bind successfully and redirect to the next step (yes living there)" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(POST, "/register-to-vote/forces/address/first")
          .withIerSession()
          .withFormUrlEncodedBody(
          "address.hasAddress" -> "yes-living-there"
        )
      )

      status(result) should be(SEE_OTHER)
      redirectLocation(result) should be(Some("/register-to-vote/forces/address"))
    }
  }
  it should "bind successfully and redirect to the next step (yes not living there)" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(POST, "/register-to-vote/forces/address/first")
          .withIerSession()
          .withFormUrlEncodedBody(
          "address.hasAddress" -> "yes-not-living-there"
        )
      )

      status(result) should be(SEE_OTHER)
      redirectLocation(result) should be(Some("/register-to-vote/forces/address"))
    }
  }

  it should "bind successfully and redirect to address selectstep if all other steps are complete" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(POST, "/register-to-vote/forces/address/first")
          .withIerSession()
          .withApplication(completeForcesApplication)
          .withFormUrlEncodedBody(
            "address.hasAddress" -> "yes-living-there"
          )
      )

      status(result) should be(SEE_OTHER)
      redirectLocation(result) should be(Some("/register-to-vote/forces/address/select"))
    }
  }

  it should "display any errors on unsuccessful bind" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(POST, "/register-to-vote/forces/address/first").withIerSession()
      )

      status(result) should be(OK)
      contentAsString(result) should include(
        "Do you have a UK address?"
      )
      contentAsString(result) should include("Please answer this question")
      contentAsString(result) should include("<form action=\"/register-to-vote/forces/address/first\"")

    }
  }

behavior of "AddressFirstStep.editGet"
  it should "display the page" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(GET, "/register-to-vote/forces/edit/address/first").withIerSession()
      )

      status(result) should be(OK)
      contentType(result) should be(Some("text/html"))
      contentAsString(result) should include(
        "Do you have a UK address?"
      )
      contentAsString(result) should include("<form action=\"/register-to-vote/forces/edit/address/first\"")

    }
  }

  behavior of "AddressFirstStep.editPost"
  it should "bind successfully and redirect to the next step" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(POST, "/register-to-vote/forces/edit/address/first")
          .withIerSession()
          .withFormUrlEncodedBody(
            "address.hasAddress" -> "yes-living-there"
          )
      )

      status(result) should be(SEE_OTHER)
      redirectLocation(result) should be(Some("/register-to-vote/forces/address"))
    }
  }

  behavior of "Completing a prior step when this question is incomplete"
  it should "stop on this page" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(POST, "/register-to-vote/forces/statement")
          .withIerSession()
          .withApplication(completeForcesApplication.copy(address = None))
          .withFormUrlEncodedBody(
            "statement.forcesMember" -> "true"
          )
      )

      status(result) should be(SEE_OTHER)
      redirectLocation(result) should be(Some("/register-to-vote/forces/address/first"))
    }
  }
}
