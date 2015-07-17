package uk.gov.gds.ier.filter

import play.api.mvc._
import scala.concurrent.Future
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import uk.gov.gds.ier.logging.Logging
import uk.gov.gds.ier.assets.RemoteAssets
import com.google.inject.Inject

class AssetsCacheFilter @Inject()(remoteAssets: RemoteAssets) extends Filter with Logging {
  def apply(nextFilter: (RequestHeader) => Future[SimpleResult])
           (requestHeader: RequestHeader): Future[SimpleResult] = {
    nextFilter(requestHeader).map { result =>
      if(remoteAssets.shouldSetNoCache(requestHeader)){
        logger.error(s"request with unrecognised sha: ${requestHeader.method} ${requestHeader.path}")
        result.withHeaders("Pragma" -> "no-cache")
      } else {
        result
      }
    }
  }
}
