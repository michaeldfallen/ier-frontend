package uk.gov.gds.ier.feedback

case class FeedbackRequest(
  comment: String = "",
  contactName: Option[String] = None,
  contactEmail: Option[String] = None
)
