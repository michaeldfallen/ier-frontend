package uk.gov.gds.ier.transaction.crown.job

import uk.gov.gds.ier.test.ControllerTestSuite

class JobControllerTests extends ControllerTestSuite {

  behavior of "JobController.get"
  it should "display the page" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(GET, "/register-to-vote/crown/job-title").withIerSession()
      )

      status(result) should be(OK)
      contentType(result) should be(Some("text/html"))
      contentAsString(result) should include("What is your role?")
      contentAsString(result) should include("<form action=\"/register-to-vote/crown/job-title\"")
    }
  }

  behavior of "JobController.post"
  it should "bind successfully and redirect to the next step" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(POST, "/register-to-vote/crown/job-title")
          .withIerSession()
          .withFormUrlEncodedBody(
            "job.jobTitle" -> "Doctor",
            "job.payrollNumber" -> "123456",
            "job.govDepartment" -> "Fake Department")
      )

      status(result) should be(SEE_OTHER)
      redirectLocation(result) should be(Some("/register-to-vote/crown/nino"))
    }
  }



  it should "bind successfully and redirect to the nino step" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(POST, "/register-to-vote/crown/job-title")
          .withIerSession()
          .withApplication(completeCrownApplication)
          .withFormUrlEncodedBody(
          "job.jobTitle" -> "Doctor",
          "job.payrollNumber" -> "123456",
          "job.govDepartment" -> "Fake Department")
      )

      status(result) should be(SEE_OTHER)
      redirectLocation(result) should be(Some("/register-to-vote/crown/nino"))
    }
  }

  it should "display any errors on unsuccessful bind" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(POST, "/register-to-vote/crown/job-title").withIerSession()
      )

      status(result) should be(OK)
      contentAsString(result) should include("What is your role?")
      contentAsString(result) should include("Please answer this question")
      contentAsString(result) should include("<form action=\"/register-to-vote/crown/job-title\"")
    }
  }

  behavior of "JobController.editGet"
  it should "display the page" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(GET, "/register-to-vote/crown/edit/job-title").withIerSession()
      )

      status(result) should be(OK)
      contentType(result) should be(Some("text/html"))
      contentAsString(result) should include("What is your role?")
      contentAsString(result) should include("<form action=\"/register-to-vote/crown/edit/job-title\"")
    }
  }

  behavior of "JobController.editPost"
  it should "bind successfully and redirect to the next step" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(POST, "/register-to-vote/crown/edit/job-title")
          .withIerSession()
          .withFormUrlEncodedBody(
          "job.jobTitle" -> "Doctor",
          "job.payrollNumber" -> "123456",
          "job.govDepartment" -> "Fake Department")
      )

      status(result) should be(SEE_OTHER)
      redirectLocation(result) should be(Some("/register-to-vote/crown/nino"))
    }
  }

  it should "bind successfully and redirect to the nino step" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(POST, "/register-to-vote/crown/edit/job-title")
          .withIerSession()
          .withApplication(completeCrownApplication)
          .withFormUrlEncodedBody(
          "job.jobTitle" -> "Doctor",
          "job.payrollNumber" -> "123456",
          "job.govDepartment" -> "Fake Department")
      )

      status(result) should be(SEE_OTHER)
      redirectLocation(result) should be(Some("/register-to-vote/crown/nino"))
    }
  }

  it should "display any errors on unsuccessful bind" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(POST, "/register-to-vote/crown/edit/job-title").withIerSession()
      )

      status(result) should be(OK)
      contentAsString(result) should include("What is your role?")
      contentAsString(result) should include("Please answer this question")
      contentAsString(result) should include("<form action=\"/register-to-vote/crown/edit/job-title\"")
    }
  }
}
