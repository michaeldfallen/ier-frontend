package uk.gov.gds.ier.feedback

import com.google.inject.Inject
import uk.gov.gds.ier.client.ApiClient
import uk.gov.gds.ier.serialiser.JsonSerialiser
import uk.gov.gds.ier.config.Config
import uk.gov.gds.ier.logging.Logging
import uk.gov.gds.ier.guice.WithConfig

trait FeedbackClient {
  def submit(feedbackData: FeedbackSubmissionData)
}

/**
 * See: http://developer.zendesk.com/documentation/rest_api/tickets.html#creating-tickets
 */
class FeedbackClientImpl @Inject() (serialiser: JsonSerialiser, configuration: Config)
  extends FeedbackClient
  with ApiClient
  with WithConfig
  with Logging {

  val config = configuration

  def submit(feedbackData: FeedbackSubmissionData) = {
    postAsync(
      url = config.zendeskUrl,
      content = serialiser.toJson(feedbackData),
      username = config.zendeskUsername,
      password = config.zendeskPassword)
  }
}
