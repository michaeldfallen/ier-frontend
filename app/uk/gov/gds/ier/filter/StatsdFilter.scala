package uk.gov.gds.ier.filter

import play.api.mvc._
import scala.concurrent.Future
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import uk.gov.gds.ier.logging.Logging
import uk.gov.gds.ier.client.StatsdClient

object StatsdFilter extends Filter with Logging {
  def apply(nextFilter: (RequestHeader) => Future[SimpleResult])
           (requestHeader: RequestHeader): Future[SimpleResult] = {
    val startTime = System.currentTimeMillis
    nextFilter(requestHeader).map { result =>
      val endTime = System.currentTimeMillis
      val requestTime = endTime - startTime
      logger.info(
        s"${requestHeader.method} ${requestHeader.uri} " +
        s"took ${requestTime}ms and returned ${result.header.status}")
      val metricPageName =
        requestHeader.path.substring(1).replace('/','.') + "." + requestHeader.method
      StatsdClient.timing(metricPageName, requestTime)
      result
    }
  }
}
