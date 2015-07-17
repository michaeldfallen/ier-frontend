package uk.gov.gds.ier.transaction.overseas.dateOfBirth

import uk.gov.gds.ier.test.ControllerTestSuite
import org.joda.time.DateTime

class DateOfBirthStepTests extends ControllerTestSuite {

  behavior of "DateOfBirthController.get"
  it should "display the page" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(GET, "/register-to-vote/overseas/date-of-birth").withIerSession()
      )

      status(result) should be(OK)
      contentType(result) should be(Some("text/html"))
      contentAsString(result) should not include("<a class=\"back-to-previous\"")
      contentAsString(result) should include("What is your date of birth?")
      contentAsString(result) should include("/register-to-vote/overseas/date-of-birth")
    }
  }

  behavior of "DateOfBirthController.post"
  it should "bind successfully and redirect to the Previously Registered step" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(POST, "/register-to-vote/overseas/date-of-birth")
          .withIerSession()
          .withFormUrlEncodedBody(
          "dob.day" -> "1",
          "dob.month" -> "1",
          "dob.year" -> "1970")
      )

      status(result) should be(SEE_OTHER)
      redirectLocation(result) should be(Some("/register-to-vote/overseas/last-registered-to-vote"))
    }
  }

  it should "bind successfully and redirect to the confirmation step when all complete" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(POST, "/register-to-vote/overseas/date-of-birth")
          .withIerSession()
          .withApplication(completeOverseasApplication)
          .withFormUrlEncodedBody(
          "dob.day" -> "1",
          "dob.month" -> "1",
          "dob.year" -> "1970")
      )

      status(result) should be(SEE_OTHER)
      redirectLocation(result) should be(Some("/register-to-vote/overseas/confirmation"))
    }
  }

  behavior of "DateOfBirthController.post"
  it should "bind successfully and redirect too young exit page" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(POST, "/register-to-vote/overseas/date-of-birth")
          .withIerSession()
          .withFormUrlEncodedBody(
          "dob.day" -> "1",
          "dob.month" -> "1",
          "dob.year" -> s"${DateTime.now.getYear - 10}")
      )

      status(result) should be(SEE_OTHER)
      redirectLocation(result) should be(Some("/register-to-vote/exit/too-young"))
    }
  }
  it should "display any errors on unsuccessful bind" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(POST, "/register-to-vote/overseas/date-of-birth").withIerSession()
      )

      status(result) should be(OK)
      contentAsString(result) should include("What is your date of birth?")
      contentAsString(result) should include("Please enter your date of birth")
      contentAsString(result) should include("/register-to-vote/overseas/date-of-birth")
    }
  }

  behavior of "DateOfBirthController.editGet"
  it should "display the page" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(GET, "/register-to-vote/overseas/edit/date-of-birth").withIerSession()
      )

      status(result) should be(OK)
      contentType(result) should be(Some("text/html"))
      contentAsString(result) should not include("<a class=\"back-to-previous\"")
      contentAsString(result) should include("What is your date of birth?")
      contentAsString(result) should include("/register-to-vote/overseas/edit/date-of-birth")
    }
  }

  behavior of "DateOfBirthController.editPost"
  it should "bind successfully and redirect to the Previously Registered step" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(POST, "/register-to-vote/overseas/edit/date-of-birth")
          .withIerSession()
          .withFormUrlEncodedBody(
          "dob.day" -> "1",
          "dob.month" -> "1",
          "dob.year" -> "1970")
      )

      status(result) should be(SEE_OTHER)
      redirectLocation(result) should be(Some("/register-to-vote/overseas/last-registered-to-vote"))
    }
  }

  it should "bind successfully and redirect to the confirmation step when all complete" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(POST, "/register-to-vote/overseas/edit/date-of-birth")
          .withIerSession()
          .withApplication(completeOverseasApplication)
          .withFormUrlEncodedBody(
          "dob.day" -> "1",
          "dob.month" -> "1",
          "dob.year" -> "1970")
      )

      status(result) should be(SEE_OTHER)
      redirectLocation(result) should be(Some("/register-to-vote/overseas/confirmation"))
    }
  }

  it should "bind successfully and redirect too young exit page" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(POST, "/register-to-vote/overseas/edit/date-of-birth")
          .withIerSession()
          .withFormUrlEncodedBody(
          "dob.day" -> "1",
          "dob.month" -> "1",
          "dob.year" -> s"${DateTime.now.getYear - 10}")
      )

      status(result) should be(SEE_OTHER)
      redirectLocation(result) should be(Some("/register-to-vote/exit/too-young"))
    }
  }

  it should "display any errors on unsuccessful bind" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(POST, "/register-to-vote/overseas/edit/date-of-birth").withIerSession()
      )

      status(result) should be(OK)
      contentAsString(result) should include("What is your date of birth?")
      contentAsString(result) should include("Please enter your date of birth")
      contentAsString(result) should include("/register-to-vote/overseas/edit/date-of-birth")
    }
  }
}
