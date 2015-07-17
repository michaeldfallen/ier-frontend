package assets.mustache.crown

import uk.gov.gds.ier.transaction.crown.confirmation.ConfirmationMustache
import uk.gov.gds.ier.transaction.shared.BlockContent
import uk.gov.gds.ier.test._

class ConfirmationTemplateTests
  extends TemplateTestSuite
  with ConfirmationMustache
  with WithMockCrownControllers
  with WithMockAddressService {

  it should "not render the partners details block if displayPartnerBlock = false" in {
    running(FakeApplication()) {
      val model = ConfirmationModel(
        question = Question(
          title = "Foo",
          postUrl = "http://postUrl"
        ),
        applicantDetails = List(
          ConfirmationQuestion(
            content = BlockContent("Some applicant details"),
            title = "Applicant Details",
            editLink = "http://applicantDetails",
            changeName = "applicant details"
          )
        ),
        partnerDetails = List(
          ConfirmationQuestion(
            content = BlockContent("Some applicant details"),
            title = "Applicant Details",
            editLink = "http://applicantDetails",
            changeName = "applicant details"
          )
        ),
        displayPartnerBlock = false
      )

      val html = Mustache.render("crown/confirmation", model)
      val doc = Jsoup.parse(html.toString)

      doc.html should not include("Your partner's details")
    }
  }


  it should "render the partners details block if displayPartnerBlock = true" in {
    running(FakeApplication()) {
      val model = ConfirmationModel(
        question = Question(
          title = "Foo",
          postUrl = "http://postUrl"
        ),
        applicantDetails = List(
          ConfirmationQuestion(
            content = BlockContent("Some applicant details"),
            title = "Applicant Details",
            editLink = "http://applicantDetails",
            changeName = "applicant details"
          )
        ),
        partnerDetails = List(
          ConfirmationQuestion(
            content = BlockContent("Some applicant details"),
            title = "Applicant Details",
            editLink = "http://applicantDetails",
            changeName = "applicant details"
          )
        ),
        displayPartnerBlock = true
      )

      val html = Mustache.render("crown/confirmation", model)
      val doc = Jsoup.parse(html.toString)

      val partnerH2 = doc.select("h2").first()
      partnerH2 should not be(null)
      partnerH2.html should be("Your partner's details")

      val applicantH2 = doc.select("h2").get(1)
      applicantH2 should not be(null)
      applicantH2.html should be("Your details")
    }
  }
}
