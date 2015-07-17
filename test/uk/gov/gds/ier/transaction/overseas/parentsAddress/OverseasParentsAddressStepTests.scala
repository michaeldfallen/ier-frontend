package uk.gov.gds.ier.transaction.overseas.parentsAddress

import uk.gov.gds.ier.test.ControllerTestSuite
import uk.gov.gds.ier.model._
import uk.gov.gds.ier.config.Config
import uk.gov.gds.ier.DynamicGlobal

class OverseasParentsAddressStepTests extends ControllerTestSuite {

  private def createGlobalConfigWith(availableForScotlandFlag: Boolean) = {
    val mockConfig = new Config {
      override def availableForScotland = availableForScotlandFlag
    }

    Some(new DynamicGlobal {
      override def bindings = { binder =>
        binder bind classOf[uk.gov.gds.ier.config.Config] toInstance mockConfig
      }
    })
  }

  behavior of "ParentsAddressStep.get"
  it should "display the page" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(GET, "/register-to-vote/overseas/parents-address").withIerSession()
      )

      status(result) should be(OK)
      contentType(result) should be(Some("text/html"))
      contentAsString(result) should include(
        "What was your parent or guardian&#39;s last UK address?"
      )
      contentAsString(result) should include("action=\"/register-to-vote/overseas/parents-address\"")
    }
  }

  behavior of "ParentsAddressStep.post"
  it should "bind successfully and redirect to the Name step" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(POST, "/register-to-vote/overseas/parents-address/select")
          .withIerSession()
          .withFormUrlEncodedBody(
            "parentsAddress.uprn" -> "123456789",
            "parentsAddress.postcode" -> "SW1A 1AA"
          )
      )

      status(result) should be(SEE_OTHER)
      redirectLocation(result) should be(Some("/register-to-vote/overseas/passport"))
    }
  }

  it should "redirect to the Northern Ireland Exit page if the postcode starts with BT" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(POST, "/register-to-vote/overseas/parents-address")
          .withIerSession()
          .withApplication(incompleteYoungApplication.copy (parentsAddress = None))
          .withFormUrlEncodedBody(
            "parentsAddress.uprn" -> "123456789",
            "parentsAddress.postcode" -> "BT1A 1AA"
          )
      )

      status(result) should be(SEE_OTHER)
      redirectLocation(result) should be(Some("/register-to-vote/exit/northern-ireland"))
    }
  }

  it should behave like appWithScottishAddressWith(availableForScotlandFlag = true, andRedirectsToUrl = "/register-to-vote/overseas/parents-address/select")

  def appWithScottishAddressWith(availableForScotlandFlag: Boolean, andRedirectsToUrl: String) {
    it should s"redirect $andRedirectsToUrl for Scottish postcode with availableForScotlandFlag: $availableForScotlandFlag" in {
      running(FakeApplication(withGlobal = createGlobalConfigWith(availableForScotlandFlag = availableForScotlandFlag))) {
        val Some(result) = route(
          FakeRequest(POST, "/register-to-vote/overseas/parents-address")
            .withIerSession()
            .withFormUrlEncodedBody(
              "parentsAddress.postcode" -> "EH10 4AE"
            )
        )

        status(result) should be(SEE_OTHER)
        redirectLocation(result) should be(Some(andRedirectsToUrl))
      }
    }
  }

  it should "bind successfully and redirect to the Name step with a manual address" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(POST, "/register-to-vote/overseas/parents-address/select")
          .withIerSession()
          .withFormUrlEncodedBody(
            "parentsAddress.manualAddress.lineOne" -> "Unit 4, Elgar Business Centre",
            "parentsAddress.manualAddress.lineTwo" -> "Moseley Road",
            "parentsAddress.manualAddress.lineThree" -> "Hallow",
            "parentsAddress.manualAddress.city" -> "Worcester",
            "parentsAddress.postcode" -> "SW1A 1AA"
        )
      )

      status(result) should be(SEE_OTHER)
      redirectLocation(result) should be(Some("/register-to-vote/overseas/passport"))
    }
  }



  it should "bind successfully and redirect to confirmation if all other steps are complete" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(POST, "/register-to-vote/overseas/parents-address/select")
          .withIerSession()
          .withApplication(completeOverseasApplication)
          .withFormUrlEncodedBody(
            "parentsAddress.manualAddress.lineOne" -> "Unit 4, Elgar Business Centre",
            "parentsAddress.manualAddress.lineTwo" -> "Moseley Road",
            "parentsAddress.manualAddress.lineThree" -> "Hallow",
            "parentsAddress.manualAddress.city" -> "Worcester",
            "parentsAddress.postcode" -> "SW1A 1AA"
          )
      )

      status(result) should be(SEE_OTHER)
      redirectLocation(result) should be(Some("/register-to-vote/overseas/confirmation"))
    }
  }

  it should "display any errors on unsuccessful bind" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(POST, "/register-to-vote/overseas/parents-address").withIerSession()
      )

      status(result) should be(OK)
      contentAsString(result) should include(
        "What was your parent or guardian&#39;s last UK address?"
      )
      contentAsString(result) should include("Please enter your postcode")
      contentAsString(result) should include("action=\"/register-to-vote/overseas/parents-address\"")
    }
  }

  behavior of "ParentsAddressStep.editGet"
  it should "display the page" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(GET, "/register-to-vote/overseas/edit/parents-address/select").withIerSession()
      )

      status(result) should be(OK)
      contentType(result) should be(Some("text/html"))
      contentAsString(result) should include(
        "What was your parent or guardian&#39;s last UK address?"
      )
      contentAsString(result) should include("action=\"/register-to-vote/overseas/edit/parents-address/select\"")
    }
  }

  behavior of "ParentsAddressStep.editPost"
  it should "bind successfully and redirect to the Previous Address step" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(POST, "/register-to-vote/overseas/edit/parents-address/select")
          .withIerSession()
          .withFormUrlEncodedBody(
            "parentsAddress.uprn" -> "123456789",
            "parentsAddress.postcode" -> "SW1A 1AA"
          )
      )

      status(result) should be(SEE_OTHER)
      redirectLocation(result) should be(Some("/register-to-vote/overseas/passport"))
    }
  }

  it should behave like editedAppWithScottishAddressWith(availableForScotlandFlag = true, andRedirectsToUrl = "/register-to-vote/overseas/parents-address/select")

  def editedAppWithScottishAddressWith(availableForScotlandFlag: Boolean, andRedirectsToUrl: String) {
    it should s"redirect $andRedirectsToUrl for Scottish postcode with availableForScotlandFlag: $availableForScotlandFlag" in {
      running(FakeApplication(withGlobal = createGlobalConfigWith(availableForScotlandFlag = availableForScotlandFlag))) {
        val Some(result) = route(
          FakeRequest(POST, "/register-to-vote/overseas/edit/parents-address")
            .withIerSession()
            .withFormUrlEncodedBody(
              "parentsAddress.postcode" -> "EH10 4AE"
            )
        )

        status(result) should be(SEE_OTHER)
        redirectLocation(result) should be(Some(andRedirectsToUrl))
      }
    }
  }

  it should "bind successfully and redirect to the Name step with a manual address" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(POST, "/register-to-vote/overseas/edit/parents-address/select")
          .withIerSession()
          .withFormUrlEncodedBody(
            "parentsAddress.manualAddress.lineOne" -> "Unit 4, Elgar Business Centre",
            "parentsAddress.manualAddress.lineTwo" -> "Moseley Road",
            "parentsAddress.manualAddress.lineThree" -> "Hallow",
            "parentsAddress.manualAddress.city" -> "Worcester",
            "parentsAddress.postcode" -> "SW1A 1AA"
        )
      )

      status(result) should be(SEE_OTHER)
      redirectLocation(result) should be(Some("/register-to-vote/overseas/passport"))
    }
  }

  it should "bind successfully and redirect to confirmation if all other steps are complete" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(POST, "/register-to-vote/overseas/edit/parents-address/select")
          .withIerSession()
          .withApplication(completeOverseasApplication)
          .withFormUrlEncodedBody(
            "parentsAddress.manualAddress.lineOne" -> "Unit 4, Elgar Business Centre",
            "parentsAddress.manualAddress.lineTwo" -> "Moseley Road",
            "parentsAddress.manualAddress.lineThree" -> "Hallow",
            "parentsAddress.manualAddress.city" -> "Worcester",
            "parentsAddress.postcode" -> "SW1A 1AA"
          )
      )

      status(result) should be(SEE_OTHER)
      redirectLocation(result) should be(Some("/register-to-vote/overseas/confirmation"))
    }
  }

  it should "display any errors on unsuccessful bind" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(POST, "/register-to-vote/overseas/edit/parents-address/select").withIerSession()
      )

      status(result) should be(OK)
      contentAsString(result) should include(
        "What was your parent or guardian&#39;s last UK address?"
      )
      contentAsString(result) should include("Please answer this question")
      contentAsString(result) should include("action=\"/register-to-vote/overseas/edit/parents-address/select\"")
    }
  }

  behavior of "Completing a prior step when this question is incomplete"
  it should "stop on this page" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(POST, "/register-to-vote/overseas/edit/parent-name")
          .withIerSession()
          .withApplication(completeOverseasApplication.copy(
            parentsAddress = None
          ))
          .withFormUrlEncodedBody(
            "overseasParentName.parentName.firstName" -> "John",
            "overseasParentName.parentName.lastName" -> "Smith",
            "overseasParentName.parentPreviousName.hasPreviousName" -> "true",
            "overseasParentName.parentPreviousName.previousName.firstName" -> "John",
            "overseasParentName.parentPreviousName.previousName.lastName" -> "Smith")
      )

      status(result) should be(SEE_OTHER)
      redirectLocation(result) should be(Some("/register-to-vote/overseas/parents-address"))
    }
  }
}
