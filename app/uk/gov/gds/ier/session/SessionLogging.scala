package uk.gov.gds.ier.session

import uk.gov.gds.ier.logging.Logging
import play.api.mvc.Request
import org.joda.time.DateTime

trait SessionLogging extends Logging {
  self: SessionHandling[_] =>

  def logSession()(implicit request: Request[Any]) {
    val url = request.path
    val timestamp = request.getToken.map(_.latest).getOrElse(DateTime.now)
    val history = request.getToken.map(_.history).getOrElse(List.empty)
    logger.info(
      s"Logging session on page $url at $timestamp. Session history: $history"
    )
  }

}
