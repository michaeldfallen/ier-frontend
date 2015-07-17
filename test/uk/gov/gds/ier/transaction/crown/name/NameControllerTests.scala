package uk.gov.gds.ier.transaction.crown.name

import uk.gov.gds.ier.test.ControllerTestSuite

class NameControllerTests extends ControllerTestSuite {

  behavior of "NameController.get"
  it should "display the page" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(GET, "/register-to-vote/crown/name").withIerSession()
      )

      status(result) should be(OK)
      contentType(result) should be(Some("text/html"))
      contentAsString(result) should include("What is your full name?")
      contentAsString(result) should include("Have you ever changed your name?")
      contentAsString(result) should include("<form action=\"/register-to-vote/crown/name\"")
    }
  }

  behavior of "NameController.post"
  it should "bind successfully and redirect to the next step" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(POST, "/register-to-vote/crown/name")
          .withIerSession()
          .withFormUrlEncodedBody(
          "name.firstName" -> "John",
          "name.lastName" -> "Smith",
          "previousName.hasPreviousName" -> "true",
          "previousName.hasPreviousNameOption" -> "true",
          "previousName.previousName.firstName" -> "John",
          "previousName.previousName.lastName" -> "Smith")
      )

      status(result) should be(SEE_OTHER)
      redirectLocation(result) should be(Some("/register-to-vote/crown/job-title"))
    }
  }

  it should "bind successfully with no previous name and redirect to next step" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(POST, "/register-to-vote/crown/name")
          .withIerSession()
          .withFormUrlEncodedBody(
          "name.firstName" -> "John",
          "name.lastName" -> "Smith",
          "previousName.hasPreviousName" -> "false",
          "previousName.hasPreviousNameOption" -> "false")
      )

      status(result) should be(SEE_OTHER)
      redirectLocation(result) should be(Some("/register-to-vote/crown/job-title"))
    }
  }

  it should "bind successfully and redirect to the confirmation step with a complete application" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(POST, "/register-to-vote/crown/name")
          .withIerSession()
          .withApplication(completeCrownApplication)
          .withFormUrlEncodedBody(
          "name.firstName" -> "John",
          "name.lastName" -> "Smith",
          "previousName.hasPreviousName" -> "true",
          "previousName.hasPreviousNameOption" -> "true",
          "previousName.previousName.firstName" -> "John",
          "previousName.previousName.lastName" -> "Smith")
      )

      status(result) should be(SEE_OTHER)
      redirectLocation(result) should be(Some("/register-to-vote/crown/confirmation"))
    }
  }

  it should "display any errors on unsuccessful bind" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(POST, "/register-to-vote/crown/name").withIerSession()
      )

      status(result) should be(OK)
      contentAsString(result) should include("What is your full name?")
      contentAsString(result) should include("Please enter your full name")
      contentAsString(result) should include("Have you ever changed your name?")
      contentAsString(result) should include("Please answer this question")
      contentAsString(result) should include("<form action=\"/register-to-vote/crown/name\"")
    }
  }

//  behavior of "Completing a prior step when this question is incomplete"
//  it should "stop on this page" in {
//    running(FakeApplication()) {
//      val Some(result) = route(
//        FakeRequest(POST, "/register-to-vote/crown/date-of-birth")
//          .withIerSession()
//          .withApplication(completeCrownApplication.copy(name = None))
//          .withFormUrlEncodedBody(
//          "dob.dob.day" -> "1",
//          "dob.dob.month" -> "1",
//          "dob.dob.year" -> "1970")
//      )
//
//      status(result) should be(SEE_OTHER)
//      redirectLocation(result) should be(Some("/register-to-vote/crown/name"))
//    }
//  }

  behavior of "NameController.editGet"
  it should "display the page" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(GET, "/register-to-vote/crown/edit/name").withIerSession()
      )

      status(result) should be(OK)
      contentType(result) should be(Some("text/html"))
      contentAsString(result) should include("What is your full name?")
      contentAsString(result) should include("Have you ever changed your name?")
      contentAsString(result) should include("<form action=\"/register-to-vote/crown/edit/name\"")
    }
  }

  behavior of "NameController.editPost"
  it should "bind successfully and redirect to the next step" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(POST, "/register-to-vote/crown/edit/name")
          .withIerSession()
          .withFormUrlEncodedBody(
          "name.firstName" -> "John",
          "name.lastName" -> "Smith",
          "previousName.hasPreviousName" -> "true",
          "previousName.hasPreviousNameOption" -> "true",
          "previousName.previousName.firstName" -> "John",
          "previousName.previousName.lastName" -> "Smith")
      )

      status(result) should be(SEE_OTHER)
      redirectLocation(result) should be(Some("/register-to-vote/crown/job-title"))
    }
  }

  it should "bind successfully with no previous name and redirect to the next step" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(POST, "/register-to-vote/crown/edit/name")
          .withIerSession()
          .withFormUrlEncodedBody(
          "name.firstName" -> "John",
          "name.lastName" -> "Smith",
          "previousName.hasPreviousName" -> "false",
          "previousName.hasPreviousNameOption" -> "false")
      )

      status(result) should be(SEE_OTHER)
      redirectLocation(result) should be(Some("/register-to-vote/crown/job-title"))
    }
  }

  it should "bind successfully and redirect to the confirmation step with a complete application" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(POST, "/register-to-vote/crown/edit/name")
          .withIerSession()
          .withApplication(completeCrownApplication)
          .withFormUrlEncodedBody(
          "name.firstName" -> "John",
          "name.lastName" -> "Smith",
          "previousName.hasPreviousName" -> "true",
          "previousName.hasPreviousNameOption" -> "true",
          "previousName.previousName.firstName" -> "John",
          "previousName.previousName.lastName" -> "Smith")
      )

      status(result) should be(SEE_OTHER)
      redirectLocation(result) should be(Some("/register-to-vote/crown/confirmation"))
    }
  }

  it should "display any errors on unsuccessful bind" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(POST, "/register-to-vote/edit/name").withIerSession()
      )

      status(result) should be(OK)
      contentAsString(result) should include("What is your full name?")
      contentAsString(result) should include("Please enter your full name")
      contentAsString(result) should include("Have you ever changed your name?")
      contentAsString(result) should include("Please answer this question")
      contentAsString(result) should include("<form action=\"/register-to-vote/edit/name\"")
    }
  }
}
