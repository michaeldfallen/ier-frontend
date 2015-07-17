package uk.gov.gds.ier.filter

import play.api.mvc._
import scala.concurrent.Future
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import uk.gov.gds.ier.logging.Logging

object ResultFilter extends Filter with Logging {
  def apply(nextFilter: (RequestHeader) => Future[SimpleResult])
           (requestHeader: RequestHeader): Future[SimpleResult] = {
    nextFilter(requestHeader).map { result =>
      result.withHeaders("X-Frame-Options" -> "deny")
    }
  }
}
