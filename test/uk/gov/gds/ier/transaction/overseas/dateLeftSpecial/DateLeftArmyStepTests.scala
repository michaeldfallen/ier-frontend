package uk.gov.gds.ier.transaction.overseas.dateLeftSpecial

import uk.gov.gds.ier.test.ControllerTestSuite
import uk.gov.gds.ier.model.{LastRegisteredToVote, LastRegisteredType, DOB, DateOfBirth}
import uk.gov.gds.ier.model.LastRegisteredType._
import uk.gov.gds.ier.transaction.overseas.InprogressOverseas

class DateLeftArmyStepTests extends ControllerTestSuite {

  behavior of "DateLeftArmyStep.get"
  it should "display the page" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(GET, "/register-to-vote/overseas/date-left-army").withIerSession()
      )

      status(result) should be(OK)
      contentType(result) should be(Some("text/html"))
      contentAsString(result) should include("When did you cease to be a member of the armed forces?")
      contentAsString(result) should include("/register-to-vote/overseas/date-left-army")
    }
  }

  behavior of "DateLeftArmyStep.post"
  it should "bind successfully and redirect to the LastUKAddress step" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(POST, "/register-to-vote/overseas/date-left-army")
          .withIerSession()
          .withFormUrlEncodedBody(
          "dateLeftSpecial.month" -> "10",
          "dateLeftSpecial.year" -> "2000"
        )
      )

      status(result) should be(SEE_OTHER)
      redirectLocation(result) should be(Some("/register-to-vote/overseas/last-uk-address"))
    }
  }

  it should "bind successfully and exit if it's been over 15 years when the user left the army" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(POST, "/register-to-vote/overseas/date-left-army")
          .withIerSession()
          .withFormUrlEncodedBody(
          "dateLeftSpecial.month" -> "10",
          "dateLeftSpecial.year" -> "1998"
        )
      )

      status(result) should be(SEE_OTHER)
      redirectLocation(result) should be(
        Some("/register-to-vote/exit/overseas/left-service-over-15-years")
      )
    }
  }


  it should "display any errors on unsuccessful bind" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(POST, "/register-to-vote/overseas/date-left-army").withIerSession()
      )

      status(result) should be(OK)
      contentAsString(result) should include("When did you cease to be a member of the armed forces?")
      contentAsString(result) should include("Please answer this question")
      contentAsString(result) should include("/register-to-vote/overseas/date-left-army")
    }
  }

  behavior of "DateLeftArmyStep.editGet"
  it should "display the edit page" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(GET, "/register-to-vote/overseas/edit/date-left-army").withIerSession()
      )

      status(result) should be(OK)
      contentType(result) should be(Some("text/html"))
      contentAsString(result) should include("When did you cease to be a member of the armed forces?")
      contentAsString(result) should include("/register-to-vote/overseas/edit/date-left-army")
    }
  }

  behavior of "DateLeftArmyStep.editPost"
  it should "bind successfully and redirect to the lastUkAddress" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(POST, "/register-to-vote/overseas/edit/date-left-army")
          .withIerSession()
          .withFormUrlEncodedBody(
              "dateLeftSpecial.month" -> "10",
              "dateLeftSpecial.year" -> "2000")
      )

      status(result) should be(SEE_OTHER)
      redirectLocation(result) should be(Some("/register-to-vote/overseas/last-uk-address"))
    }
  }

  it should "bind successfully and exit if it's been over 15 years when the user left the army" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(POST, "/register-to-vote/overseas/edit/date-left-army")
          .withIerSession()
          .withFormUrlEncodedBody(
          "dateLeftSpecial.month" -> "10",
          "dateLeftSpecial.year" -> "1998"
        )
      )

      status(result) should be(SEE_OTHER)
      redirectLocation(result) should be(
        Some("/register-to-vote/exit/overseas/left-service-over-15-years")
      )
    }
  }

  it should "display any errors on unsuccessful bind" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(POST, "/register-to-vote/overseas/edit/date-left-army").withIerSession()
      )

      status(result) should be(OK)
      contentAsString(result) should include("When did you cease to be a member of the armed forces?")
      contentAsString(result) should include("Please answer this question")
      contentAsString(result) should include("/register-to-vote/overseas/edit/date-left-army")
    }
  }

  behavior of "DateLeftArmyStep.post when complete application"
  it should "bind successfully and redirect to the confirmation step if all steps complete" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(POST, "/register-to-vote/overseas/date-left-army")
          .withIerSession()
          .withApplication(completeOverseasApplication.copy(lastRegisteredToVote = Some(LastRegisteredToVote(LastRegisteredType.Forces))))
          .withFormUrlEncodedBody(
            "dateLeftSpecial.month" -> "10",
            "dateLeftSpecial.year" -> "2000")
      )

      status(result) should be(SEE_OTHER)
      redirectLocation(result) should be(Some("/register-to-vote/overseas/confirmation"))
    }
  }


  behavior of "DateLeftArmyStep.editPost when complete application"
  it should "bind successfully and redirect to the confirmation step if all steps complete" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(POST, "/register-to-vote/overseas/edit/date-left-army")
          .withIerSession()
          .withApplication(completeOverseasApplication.copy(lastRegisteredToVote = Some(LastRegisteredToVote(LastRegisteredType.Forces))))
          .withFormUrlEncodedBody(
            "dateLeftSpecial.month" -> "10",
            "dateLeftSpecial.year" -> "2000")
      )

      status(result) should be(SEE_OTHER)
      redirectLocation(result) should be(Some("/register-to-vote/overseas/confirmation"))
    }
  }

}
