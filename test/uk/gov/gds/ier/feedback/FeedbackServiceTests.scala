package uk.gov.gds.ier.feedback

import uk.gov.gds.ier.test.MockingTestSuite
import uk.gov.gds.ier.stubs.FeedbackStubClient
import uk.gov.gds.ier.config.Config

class FeedbackServiceTests extends MockingTestSuite {

  val serialiser = jsonSerialiser
  val config = mock[Config]

  val feedbackClient = new FeedbackClientImpl(serialiser, config) {
    override def postAsync(
        url: String,
        content: String,
        username: String,
        password: String,
        headers: (String, String)*
      ): Unit = {
        content should be("{\"ticket\":" +
          "{\"subject\":\"/register-to-vote/previous-name\"," +
          "\"comment\":{" +
          "\"body\":\"Middle name limit is too short for me!\\n" +
          "\\n\\n" +
          "Contact name: James O'Connor\\n" +
          "Contact email: mr-james.o'connor_home@somewhere.somewhere.ie\\n" +
          "Browser details: cool web browser 2.3.5\"}," +
          "\"requester\":{\"name\":\"James O'Connor\"," +
          "\"email\":\"mr-james.o'connor_home@somewhere.somewhere.ie" +
          "\"}}}")
    }
  }
  val feedbackService = new FeedbackService(feedbackClient)

  val anonymous = FeedbackRequester(
    feedbackService.anonymousContactName,
    feedbackService.anonymousContactEmail
  )

  behavior of "FeedbackService#submit"
  it should "" in {
    val request = FeedbackRequest(
      comment = "Middle name limit is too short for me!",
      contactName = Some("James O'Connor"),
      contactEmail = Some("mr-james.o'connor_home@somewhere.somewhere.ie"))

    feedbackService.submit(
      request,
      Some("/register-to-vote/previous-name"),
      Some("cool web browser 2.3.5")
    )
  }


  behavior of "FeedbackService#verifyOrNullifyEmail"
  it should "be None for None" in {
    feedbackService.verifyOrNullifyEmail(None) should be(None)
  }
  it should "be OK for correct address" in {
    feedbackService.verifyOrNullifyEmail(Some("aaa@bbb.com")) should be(Some("aaa@bbb.com"))
  }
  it should "be None for incorrect address" in {
    feedbackService.verifyOrNullifyEmail(Some("aaa")) should be(None)
  }


  behavior of "FeedbackService#fixContactDetails"
  it should "return name and email for valid email" in {
    feedbackService.fixContactDetails(Some("aaa"), Some("aaa@bbb.com")) should
      be(FeedbackRequester(name="aaa", email="aaa@bbb.com"))
  }
  it should "return Anonymous user for invalid email" in {
    feedbackService.fixContactDetails(Some("aaa"), Some("aaa")) should be(anonymous)
  }
  it should "return Anonymous user for missing email" in {
    feedbackService.fixContactDetails(Some("aaa"), None) should be(anonymous)
    feedbackService.fixContactDetails(None, None) should be(anonymous)
  }
}
