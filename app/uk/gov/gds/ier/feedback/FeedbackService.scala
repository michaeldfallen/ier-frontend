package uk.gov.gds.ier.feedback

import uk.gov.gds.ier.validation.EmailValidator
import com.google.inject.Inject
import play.api.mvc.Request

class FeedbackService @Inject() (
    feedbackClient: FeedbackClient
) {

  def submit(
    request: FeedbackRequest,
    page: Option[String],
    browserDetails: Option[String]
  ) {
    feedbackClient.submit(
      FeedbackSubmissionData(
        ticketSubject(page),
        fudgeTicketBodyText(request, browserDetails),
        fixContactDetails(request.contactName, request.contactEmail)
      )
    )
  }

  def ticketSubject(sourcePath: Option[String]) = sourcePath match {
    case Some(sourcePath) => sourcePath
    case None => "ier-frontend feedback"
  }

  def submit(
    feedback: FeedbackRequest,
    page: Option[String]
  ) (
    implicit request: Request[Any]
  ) {
    this.submit(
      request = feedback,
      page = page,
      browserDetails = request.headers.get("user-agent")
    )
  }

  val separatorBetweenCommentAndAppendedFields = "\n"

  val anonymousContactName  = "anonymous"
  val anonymousContactEmail = "anonymous@anonymous.anonymous"

  /**
   * Append contact and browser details to a ticket body text as there are no proper fields for
   * them in Zendesk API and it is common practice
   */
  private[feedback] def fudgeTicketBodyText(
    request: FeedbackRequest,
    browserDetails: Option[String]) = {
    List(
      request.comment,
      separatorBetweenCommentAndAppendedFields,
      request.contactName map {
        name => s"Contact name: ${name}"} getOrElse("No contact name was provided"),
      request.contactEmail map {
        email => s"Contact email: ${email}"} getOrElse("No contact email was provided"),
      browserDetails map {
        details => s"Browser details: ${details}"} getOrElse("No browser details were provided")
    ).mkString("\n")
  }

  private[feedback] def fixContactDetails(
      contactName: Option[String],
      contactEmail: Option[String]) : FeedbackRequester = {
    val vonContactEmail = verifyOrNullifyEmail(contactEmail)
    (contactName, vonContactEmail) match {
      case (Some(contactName), Some(contactEmail)) => FeedbackRequester(contactName, contactEmail)
      case (None, Some(contactEmail)) => FeedbackRequester(contactEmail, contactEmail)
      case (None, None) => FeedbackRequester(anonymousContactName, anonymousContactEmail)
      case (Some(contactName), None) => FeedbackRequester(anonymousContactName, anonymousContactEmail)
    }
  }

  private[feedback] def verifyOrNullifyEmail(email: Option[String]): Option[String] = {
    email.filter(EmailValidator.isValid(_))
  }
}
