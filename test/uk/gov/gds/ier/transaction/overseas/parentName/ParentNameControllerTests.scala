package uk.gov.gds.ier.transaction.overseas.parentName

import uk.gov.gds.ier.test.ControllerTestSuite
import uk.gov.gds.ier.model.{PreviousName, Name}
import akka.util.Timeout
import java.util.concurrent.TimeUnit
import uk.gov.gds.ier.model.DOB
import uk.gov.gds.ier.model.LastRegisteredToVote
import uk.gov.gds.ier.model.LastRegisteredType

class ParentNameControllerTests extends ControllerTestSuite {

  behavior of "ParentNameController.get"
  it should "display the page" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(GET, "/register-to-vote/overseas/parent-name").withIerSession()
      )


      status(result) should be(OK)
      contentType(result) should be(Some("text/html"))
      contentAsString(result) should include("Parent or guardian's registration details")
      contentAsString(result) should include("Have they changed their name since you left the UK?")
      contentAsString(result) should include("<form action=\"/register-to-vote/overseas/parent-name\"")
    }
  }

  behavior of "ParentNameController.post"
  it should "bind successfully and redirect to the next step" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(POST, "/register-to-vote/overseas/parent-name")
          .withIerSession()
          .withFormUrlEncodedBody(
            "overseasParentName.parentName.firstName" -> "John",
            "overseasParentName.parentName.lastName" -> "Smith",
            "overseasParentName.parentPreviousName.hasPreviousName" -> "true",
            "overseasParentName.parentPreviousName.hasPreviousNameOption" -> "true",
            "overseasParentName.parentPreviousName.previousName.firstName" -> "John",
            "overseasParentName.parentPreviousName.previousName.lastName" -> "Smith")
      )

      status(result) should be(SEE_OTHER)
      redirectLocation(result) should be(Some("/register-to-vote/overseas/parents-address"))
    }
  }

  it should "bind successfully with no previous name and redirect to parents address step" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(POST, "/register-to-vote/overseas/parent-name")
          .withIerSession()
          .withFormUrlEncodedBody(
            "overseasParentName.parentName.firstName" -> "John",
            "overseasParentName.parentName.lastName" -> "Smith",
            "overseasParentName.parentPreviousName.hasPreviousName" -> "false",
            "overseasParentName.parentPreviousName.hasPreviousNameOption" -> "false")
      )

      status(result) should be(SEE_OTHER)
      redirectLocation(result) should be(Some("/register-to-vote/overseas/parents-address"))
    }
  }

  it should "bind successfully and redirect to the confirmation step with a complete application" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(POST, "/register-to-vote/overseas/parent-name")
          .withIerSession()
          .withApplication(completeOverseasApplication)
          .withFormUrlEncodedBody(
            "overseasParentName.parentName.firstName" -> "John",
            "overseasParentName.parentName.lastName" -> "Smith",
            "overseasParentName.parentPreviousName.hasPreviousName" -> "true",
            "overseasParentName.parentPreviousName.hasPreviousNameOption" -> "true",
            "overseasParentName.parentPreviousName.previousName.firstName" -> "John",
            "overseasParentName.parentPreviousName.previousName.lastName" -> "Smith")
      )

      status(result) should be(SEE_OTHER)
      redirectLocation(result) should be(Some("/register-to-vote/overseas/confirmation"))
    }
  }

  it should "display any errors on unsuccessful bind" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(POST, "/register-to-vote/overseas/parent-name").withIerSession()
      )

      status(result) should be(OK)
      contentAsString(result) should include("Parent or guardian's registration details")
      contentAsString(result) should include("Please enter their full name")
      contentAsString(result) should include("Have they changed their name since you left the UK?")
      contentAsString(result) should include("Please answer this question")
      contentAsString(result) should include("<form action=\"/register-to-vote/overseas/parent-name\"")
    }
  }

  behavior of "ParentNameController.editGet"
  it should "display the page" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(GET, "/register-to-vote/overseas/edit/parent-name").withIerSession()
      )

      status(result) should be(OK)
      contentType(result) should be(Some("text/html"))
      contentAsString(result) should include("Parent or guardian's registration details")
      contentAsString(result) should include("Have they changed their name since you left the UK?")
      contentAsString(result) should include("<form action=\"/register-to-vote/overseas/edit/parent-name\"")
    }
  }

  behavior of "ParentNameController.editPost"
  it should "bind successfully and redirect to the parents address step" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(POST, "/register-to-vote/overseas/edit/parent-name")
          .withIerSession()
          .withFormUrlEncodedBody(
            "overseasParentName.parentName.firstName" -> "John",
            "overseasParentName.parentName.lastName" -> "Smith",
            "overseasParentName.parentPreviousName.hasPreviousName" -> "true",
            "overseasParentName.parentPreviousName.hasPreviousNameOption" -> "true",
            "overseasParentName.parentPreviousName.previousName.firstName" -> "John",
            "overseasParentName.parentPreviousName.previousName.lastName" -> "Smith")
      )

      status(result) should be(SEE_OTHER)
      redirectLocation(result) should be(Some("/register-to-vote/overseas/parents-address"))
    }
  }

  it should "bind successfully with no previous name and redirect to parents address step" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(POST, "/register-to-vote/overseas/edit/parent-name")
          .withIerSession()
          .withFormUrlEncodedBody(
            "overseasParentName.parentName.firstName" -> "John",
            "overseasParentName.parentName.lastName" -> "Smith",
            "overseasParentName.parentPreviousName.hasPreviousName" -> "false",
            "overseasParentName.parentPreviousName.hasPreviousNameOption" -> "false")
      )

      status(result) should be(SEE_OTHER)
      redirectLocation(result) should be(Some("/register-to-vote/overseas/parents-address"))
    }
  }

  it should "bind successfully and redirect to the confirmation step with a complete application" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(POST, "/register-to-vote/overseas/edit/parent-name")
          .withIerSession()
          .withApplication(completeOverseasApplication)
          .withFormUrlEncodedBody(
            "overseasParentName.parentName.firstName" -> "John",
            "overseasParentName.parentName.lastName" -> "Smith",
            "overseasParentName.parentPreviousName.hasPreviousName" -> "true",
            "overseasParentName.parentPreviousName.hasPreviousNameOption" -> "true",
            "overseasParentName.parentPreviousName.previousName.firstName" -> "John",
            "overseasParentName.parentPreviousName.previousName.lastName" -> "Smith")
      )

      status(result) should be(SEE_OTHER)
      redirectLocation(result) should be(Some("/register-to-vote/overseas/confirmation"))
    }
  }

  it should "display any errors on unsuccessful bind" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(POST, "/register-to-vote/overseas/edit/parent-name").withIerSession()
      )

      status(result) should be(OK)
      contentAsString(result) should include("Parent or guardian's registration details")
      contentAsString(result) should include("Please enter their full name")
      contentAsString(result) should include("Have they changed their name since you left the UK?")
      contentAsString(result) should include("Please answer this question")
      contentAsString(result) should include("<form action=\"/register-to-vote/overseas/edit/parent-name\"")
    }
  }
}
