package uk.gov.gds.ier.feedback

/**
 * Full request spec on:
 * http://developer.zendesk.com/documentation/rest_api/tickets.html#creating-tickets
 */
case class FeedbackSubmissionData(
  ticket: FeedbackTicket
)

case class FeedbackTicket(
  subject: String,
  comment: FeedbackComment,
  requester: FeedbackRequester
)

case class FeedbackComment(
  body: String
)

case class FeedbackRequester(
  name: String,
  email: String
)

object FeedbackSubmissionData {
  def apply(
      subject: String,
      text: String,
      requester: FeedbackRequester
    ): FeedbackSubmissionData = {
    FeedbackSubmissionData(FeedbackTicket(
      subject = subject,
      comment = FeedbackComment(
        body = text
      ),
      requester = requester
    ))
  }
}
