package uk.gov.gds.ier.feedback

import uk.gov.gds.ier.validation.FormKeys
import play.api.data.Forms._
import play.api.data.validation._
import play.api.data.Form

trait FeedbackForm {
  self: FormKeys =>

  // this is out limit, it works well with Zendesk, not sure what is the real limit
  // also count with some space for appended contact and browser details
  val maxFeedbackCommentLength = 1200

  // another arbitrary limit
  val maxFeedbackNameLength = 100

  // see: http://stackoverflow.com/questions/386294/what-is-the-maximum-length-of-a-valid-email-address
  val maxFeedbackEmailLength = 254

  val feedbackForm = Form(
    mapping(
      keys.feedback.feedbackText.key -> text(0, maxFeedbackCommentLength),
      keys.feedback.contactName.key -> optional(text(0, maxFeedbackNameLength)),
      keys.feedback.contactEmail.key -> optional(text(0, maxFeedbackEmailLength))
    ) (
      FeedbackRequest.apply
    ) (
      FeedbackRequest.unapply
    ).verifying(feedbackTextCannotBeEmpty)
  )

  lazy val feedbackTextCannotBeEmpty = Constraint[FeedbackRequest] {
    feedbackRequest: FeedbackRequest =>
      if (feedbackRequest.comment.trim.isEmpty) Invalid("Feedback text cannot be empty")
      else Valid
  }
}
