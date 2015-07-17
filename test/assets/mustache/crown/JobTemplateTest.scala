package assets.mustache.crown

import uk.gov.gds.ier.transaction.crown.job.JobMustache
import uk.gov.gds.ier.test._

class JobTemplateTest
  extends TemplateTestSuite
  with JobMustache {

  it should "properly render all properties from the model" in {
    running(FakeApplication()) {
      val data = new JobModel(
        question = Question(),
        jobTitle = Field(
          id = "jobTitleId",
          name = "jobTitleName",
          classes = "jobTitleClass",
          value = "jobTitleValue"
        ),
        payrollNumber = Field(
          id = "payrollNumberId",
          name = "payrollNumberName",
          classes = "payrollNumberClass",
          value = "payrollNumberValue"
        ),
        govDepartment = Field(
          id = "govDepartmentId",
          name = "govDepartmentName",
          classes = "govDepartmentClass",
          value = "govDepartmentValue"
        )
      )

      val html = Mustache.render("crown/job", data)
      val doc = Jsoup.parse(html.toString)

      val jobTitleLabel = doc.select("label[for=jobTitleId]").first()
      jobTitleLabel should not be(null)
      jobTitleLabel.attr("for") should be("jobTitleId")

      val jobTitleDiv = doc.select("div[class*=jobTitleClass]").first()
      jobTitleDiv.attr("class") should include("jobTitleClass")
      val jobTitleInput = jobTitleDiv.select("input").first()
      jobTitleInput.attr("id") should be("jobTitleId")
      jobTitleInput.attr("name") should be("jobTitleName")
      jobTitleInput.attr("value") should be("jobTitleValue")
      jobTitleInput.attr("class") should include("jobTitleClass")

      val payrollNumberLabel = doc.select("label[for=payrollNumberId]").first()
      payrollNumberLabel should not be(null)
      payrollNumberLabel.attr("for") should be("payrollNumberId")

      val payrollNumberDiv = doc.select("div[class*=payrollNumberClass]").first()
      payrollNumberDiv.attr("class") should include("payrollNumberClass")
      val payrollNumberInput = payrollNumberDiv.select("input").first()
      payrollNumberInput.attr("id") should be("payrollNumberId")
      payrollNumberInput.attr("name") should be("payrollNumberName")
      payrollNumberInput.attr("value") should be("payrollNumberValue")
      payrollNumberInput.attr("class") should include("payrollNumberClass")


      val govDepartmentLabel = doc.select("label[for=govDepartmentId]").first()
      govDepartmentLabel should not be(null)
      govDepartmentLabel.attr("for") should be("govDepartmentId")

      val govDepartmentDiv = doc.select("div[class*=govDepartmentClass]").first()
      govDepartmentDiv.attr("class") should include("govDepartmentClass")
      val govDepartmentInput = govDepartmentDiv.select("input").first()
      govDepartmentInput.attr("id") should be("govDepartmentId")
      govDepartmentInput.attr("name") should be("govDepartmentName")
      govDepartmentInput.attr("value") should be("govDepartmentValue")
      govDepartmentInput.attr("class") should include("govDepartmentClass")

    }
  }
}
