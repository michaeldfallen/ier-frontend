package uk.gov.gds.ier.transaction.crown.job

import uk.gov.gds.ier.test._
import uk.gov.gds.ier.model.Job
import uk.gov.gds.ier.transaction.crown.InprogressCrown

class JobMustacheTest
  extends MustacheTestSuite
  with JobForms
  with JobMustache {

  it should "empty progress form should produce empty Model" in {
    val emptyApplicationForm = jobForm
    val emptyApplication = InprogressCrown()
    val jobModel = mustache.data(
      emptyApplicationForm,
      Call("POST", "/register-to-vote/crown/job-title"),
      emptyApplication
    ).asInstanceOf[JobModel]

    jobModel.question.title should be("What is your role?")
    jobModel.question.postUrl should be("/register-to-vote/crown/job-title")

    jobModel.jobTitle.value should be("")
    jobModel.payrollNumber.value should be("")
    jobModel.govDepartment.value should be("")

  }

  it should "progress form with filled applicant name should produce Mustache Model with name values present" in {

    val partiallyFilledApplication = InprogressCrown(
      job = Some(Job(
        jobTitle = Some("Doctor"),
        payrollNumber = Some("123456"),
        govDepartment = Some("Fake Dept")
      ))
    )

    val partiallyFilledApplicationForm = jobForm.fill(partiallyFilledApplication)

    val jobModel = mustache.data(
      partiallyFilledApplicationForm,
      Call("POST", "/register-to-vote/crown/job-title"),
      partiallyFilledApplication
    ).asInstanceOf[JobModel]

    jobModel.question.title should be("What is your role?")
    jobModel.question.postUrl should be("/register-to-vote/crown/job-title")

    jobModel.jobTitle.value should be("Doctor")
    jobModel.payrollNumber.value should be("123456")
    jobModel.govDepartment.value should be("Fake Dept")
  }
}
