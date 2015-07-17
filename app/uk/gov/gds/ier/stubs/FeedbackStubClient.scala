package uk.gov.gds.ier.stubs

import uk.gov.gds.ier.feedback.{FeedbackSubmissionData, FeedbackClient}
import uk.gov.gds.ier.logging.Logging

class FeedbackStubClient extends FeedbackClient with Logging {
  def submit(feedbackData: FeedbackSubmissionData) = {
    logger.debug(feedbackData.toString)
  }
}
