package uk.gov.gds.ier.transaction.country

import uk.gov.gds.ier.test.ControllerTestSuite
import uk.gov.gds.ier.DynamicGlobal
import uk.gov.gds.ier.config.Config

class CountryControllerTests extends ControllerTestSuite {

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

  behavior of "CountryController.get"
  it should "display the page" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(GET, "/register-to-vote/country-of-residence").withIerSession()
      )

      status(result) should be(OK)
      contentType(result) should be(Some("text/html"))
      contentAsString(result) should include("Question 1")
      contentAsString(result) should not include("<a class=\"back-to-previous\"")
      contentAsString(result) should include("Where do you live?")
      contentAsString(result) should include("/register-to-vote/country-of-residence")
    }
  }

  behavior of "CountryController.post"
  it should "bind successfully and redirect to the Nationality step" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(POST, "/register-to-vote/country-of-residence")
          .withIerSession()
          .withFormUrlEncodedBody(
          "country.residence" -> "England"
        )
      )

      status(result) should be(SEE_OTHER)
      redirectLocation(result) should be(Some("/register-to-vote/nationality"))
    }
  }

  it should "bind successfully on Northern Ireland and redirect to the exit page" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(POST, "/register-to-vote/country-of-residence")
          .withIerSession()
          .withFormUrlEncodedBody(
          "country.residence" -> "Northern Ireland"
        )
      )

      status(result) should be(SEE_OTHER)
      redirectLocation(result) should be(Some("/register-to-vote/exit/northern-ireland"))
    }
  }

  it should "bind successfully on British Islands and redirect to the exit page" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(POST, "/register-to-vote/country-of-residence")
          .withIerSession()
          .withFormUrlEncodedBody(
          "country.residence" -> "British Islands"
        )
      )

      status(result) should be(SEE_OTHER)
      redirectLocation(result) should be(Some("/register-to-vote/exit/british-islands"))
    }
  }

  it should behave like appWhichBindsToScotlandWith(availableForScotlandFlag = false, andRedirectsToUrl = "/register-to-vote/exit/scotland")
  it should behave like appWhichBindsToScotlandWith(availableForScotlandFlag = true, andRedirectsToUrl = "/register-to-vote/nationality")
  it should behave like overseasAppWhichBindsToScotlandWith(availableForScotlandFlag = false, andRedirectsToUrl = "/register-to-vote/exit/scotland")
  it should behave like overseasAppWhichBindsToScotlandWith(availableForScotlandFlag = true, andRedirectsToUrl = "/register-to-vote/overseas/start")

  def appWhichBindsToScotlandWith(availableForScotlandFlag: Boolean, andRedirectsToUrl:String) {
    it should s"bind successfully on Scotland and redirect to $andRedirectsToUrl with availableForScotland flag: $availableForScotlandFlag" in {
      running(FakeApplication(withGlobal = createGlobalConfigWith(availableForScotlandFlag = availableForScotlandFlag))) {
        val Some(result) = route(
          FakeRequest(POST, "/register-to-vote/country-of-residence")
            .withIerSession()
            .withFormUrlEncodedBody(
              "country.residence" -> "Scotland"
            )
        )

        status(result) should be(SEE_OTHER)
        redirectLocation(result) should be(Some(andRedirectsToUrl))
      }
    }
  }

  def overseasAppWhichBindsToScotlandWith(availableForScotlandFlag: Boolean, andRedirectsToUrl:String) {
    it should s"bind successfully on Abroad + Scotland and redirect to $andRedirectsToUrl with availableForScotland flag: $availableForScotlandFlag" in {
      running(FakeApplication(withGlobal = createGlobalConfigWith(availableForScotlandFlag = availableForScotlandFlag))) {
        val Some(result) = route(
          FakeRequest(POST, "/register-to-vote/country-of-residence")
            .withIerSession()
            .withFormUrlEncodedBody(
              "country.residence" -> "Abroad",
              "country.origin" -> "Scotland"
            )
        )

        status(result) should be(SEE_OTHER)
        redirectLocation(result) should be(Some(andRedirectsToUrl))
      }
    }
  }


  it should "bind successfully on Abroad + Wales and redirect to the overseas" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(POST, "/register-to-vote/country-of-residence")
          .withIerSession()
          .withFormUrlEncodedBody(
            "country.residence" -> "Abroad",
            "country.origin" -> "Wales"
          )
      )

      status(result) should be(SEE_OTHER)
      redirectLocation(result) should be(Some("/register-to-vote/overseas/start"))
    }
  }

  it should "bind successfully on Abroad + England and redirect to the overseas" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(POST, "/register-to-vote/country-of-residence")
          .withIerSession()
          .withFormUrlEncodedBody(
            "country.residence" -> "Abroad",
            "country.origin" -> "England"
          )
      )

      status(result) should be(SEE_OTHER)
      redirectLocation(result) should be(Some("/register-to-vote/overseas/start"))
    }
  }

  it should "bind successfully on Abroad + NIreland and redirect to the exit page" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(POST, "/register-to-vote/country-of-residence")
          .withIerSession()
          .withFormUrlEncodedBody(
            "country.residence" -> "Abroad",
            "country.origin" -> "Northern Ireland"
          )
      )

      status(result) should be(SEE_OTHER)
      redirectLocation(result) should be(Some("/register-to-vote/exit/northern-ireland"))
    }
  }

  it should "bind successfully on Abroad + British Islands and redirect to the exit page" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(POST, "/register-to-vote/country-of-residence")
          .withIerSession()
          .withFormUrlEncodedBody(
            "country.residence" -> "Abroad",
            "country.origin" -> "British Islands"
          )
      )

      status(result) should be(SEE_OTHER)
      redirectLocation(result) should be(Some("/register-to-vote/exit/british-islands"))
    }
  }


  it should "require the origin question answered when abroad" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(POST, "/register-to-vote/country-of-residence")
          .withIerSession()
          .withFormUrlEncodedBody(
            "country.residence" -> "Abroad"
          )
      )

      status(result) should be(OK)
      contentAsString(result) should include("Please answer this question")
    }
  }

  it should "display any errors on unsuccessful bind" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(POST, "/register-to-vote/country-of-residence").withIerSession()
      )

      status(result) should be(OK)
      contentAsString(result) should include("Where do you live?")
      contentAsString(result) should include("Please answer this question")
      contentAsString(result) should include("/register-to-vote/country-of-residence")
    }
  }

  behavior of "CountryController.editGet"
  it should "display the page" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(GET, "/register-to-vote/edit/country-of-residence").withIerSession()
      )

      status(result) should be(OK)
      contentType(result) should be(Some("text/html"))
      contentAsString(result) should include("Question 1")
      contentAsString(result) should not include("<a class=\"back-to-previous\"")
      contentAsString(result) should include("Where do you live?")
      contentAsString(result) should include("/register-to-vote/edit/country-of-residence")
    }
  }

  behavior of "CountryController.editPost"
  it should "bind successfully and redirect to the Nationality step" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(POST, "/register-to-vote/edit/country-of-residence")
          .withIerSession()
          .withFormUrlEncodedBody(
          "country.residence" -> "England"
        )
      )

      status(result) should be(SEE_OTHER)
      redirectLocation(result) should be(Some("/register-to-vote/nationality"))
    }
  }

  it should "bind successfully on Northern Ireland and redirect to the exit page" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(POST, "/register-to-vote/edit/country-of-residence")
          .withIerSession()
          .withFormUrlEncodedBody(
          "country.residence" -> "Northern Ireland"
        )
      )

      status(result) should be(SEE_OTHER)
      redirectLocation(result) should be(Some("/register-to-vote/exit/northern-ireland"))
    }
  }

  it should "bind successfully on British Islands and redirect to the exit page" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(POST, "/register-to-vote/edit/country-of-residence")
          .withIerSession()
          .withFormUrlEncodedBody(
          "country.residence" -> "British Islands"
        )
      )

      status(result) should be(SEE_OTHER)
      redirectLocation(result) should be(Some("/register-to-vote/exit/british-islands"))
    }
  }

  it should "bind successfully on Abroad + Wales and redirect to the overseas" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(POST, "/register-to-vote/edit/country-of-residence")
          .withIerSession()
          .withFormUrlEncodedBody(
            "country.residence" -> "Abroad",
            "country.origin" -> "Wales"
          )
      )

      status(result) should be(SEE_OTHER)
      redirectLocation(result) should be(Some("/register-to-vote/overseas/start"))
    }
  }

  it should "bind successfully on Abroad + England and redirect to the overseas" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(POST, "/register-to-vote/edit/country-of-residence")
          .withIerSession()
          .withFormUrlEncodedBody(
            "country.residence" -> "Abroad",
            "country.origin" -> "England"
          )
      )

      status(result) should be(SEE_OTHER)
      redirectLocation(result) should be(Some("/register-to-vote/overseas/start"))
    }
  }

  it should "bind successfully on Abroad + NIreland and redirect to the exit page" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(POST, "/register-to-vote/edit/country-of-residence")
          .withIerSession()
          .withFormUrlEncodedBody(
            "country.residence" -> "Abroad",
            "country.origin" -> "Northern Ireland"
          )
      )

      status(result) should be(SEE_OTHER)
      redirectLocation(result) should be(Some("/register-to-vote/exit/northern-ireland"))
    }
  }

  it should "bind successfully on Abroad + British Islands and redirect to the exit page" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(POST, "/register-to-vote/edit/country-of-residence")
          .withIerSession()
          .withFormUrlEncodedBody(
            "country.residence" -> "Abroad",
            "country.origin" -> "British Islands"
          )
      )

      status(result) should be(SEE_OTHER)
      redirectLocation(result) should be(Some("/register-to-vote/exit/british-islands"))
    }
  }

  it should "require the origin question answered when abroad" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(POST, "/register-to-vote/edit/country-of-residence")
          .withIerSession()
          .withFormUrlEncodedBody(
            "country.residence" -> "Abroad"
          )
      )

      status(result) should be(OK)
      contentAsString(result) should include("Please answer this question")
    }
  }

  it should behave like editedAppWhichBindsToScotlandWith(availableForScotlandFlag = false, andRedirectsToUrl = "/register-to-vote/exit/scotland" )
  it should behave like editedAppWhichBindsToScotlandWith(availableForScotlandFlag = true, andRedirectsToUrl = "/register-to-vote/nationality" )
  it should behave like editedOverseasAppWhichBindsToScotlandWith(availableForScotlandFlag = false, andRedirectsToUrl = "/register-to-vote/exit/scotland" )
  it should behave like editedOverseasAppWhichBindsToScotlandWith(availableForScotlandFlag = true, andRedirectsToUrl = "/register-to-vote/overseas/start" )

  def editedAppWhichBindsToScotlandWith(availableForScotlandFlag: Boolean, andRedirectsToUrl:String) {
    it should s"bind successfully on Scotland and redirect to $andRedirectsToUrl with availableForScotland flag: $availableForScotlandFlag" in {
      running(FakeApplication(withGlobal = createGlobalConfigWith(availableForScotlandFlag = availableForScotlandFlag))) {
        val Some(result) = route(
          FakeRequest(POST, "/register-to-vote/edit/country-of-residence")
            .withIerSession()
            .withFormUrlEncodedBody(
              "country.residence" -> "Scotland"
            )
        )

        status(result) should be(SEE_OTHER)
        redirectLocation(result) should be(Some(andRedirectsToUrl))
      }
    }
  }

  def editedOverseasAppWhichBindsToScotlandWith(availableForScotlandFlag: Boolean, andRedirectsToUrl:String) {
    it should s"bind successfully on Abroad + Scotland and redirect to $andRedirectsToUrl with availableForScotland flag: $availableForScotlandFlag" in {
      running(FakeApplication(withGlobal = createGlobalConfigWith(availableForScotlandFlag = availableForScotlandFlag))) {
        val Some(result) = route(
          FakeRequest(POST, "/register-to-vote/edit/country-of-residence")
            .withIerSession()
            .withFormUrlEncodedBody(
              "country.residence" -> "Abroad",
              "country.origin" -> "Scotland"
            )
        )

        status(result) should be(SEE_OTHER)
        redirectLocation(result) should be(Some(andRedirectsToUrl))
      }
    }
  }

}
