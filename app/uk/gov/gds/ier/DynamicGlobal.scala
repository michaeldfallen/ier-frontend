package uk.gov.gds.ier

import uk.gov.gds.ier.guice.GuiceContainer
import com.google.inject.Binder
import uk.gov.gds.ier.logging.Logging
import uk.gov.gds.ier.mustache.ErrorPageMustache
import uk.gov.gds.ier.config.Config
import scala.concurrent.Future
import play.api._
import play.api.mvc._
import play.api.mvc.Results._
import org.slf4j.MDC
import uk.gov.gds.ier.assets.RemoteAssets
import uk.gov.gds.ier.guice.{WithRemoteAssets, WithConfig}

trait DynamicGlobal
    extends GlobalSettings
    with Logging
    with ErrorPageMustache
    with WithRemoteAssets
    with WithConfig {

  def bindings: Binder => Unit = { binder => }

  lazy val guice = GuiceContainer(bindings)

  lazy val remoteAssets = guice.dependency[RemoteAssets]
  lazy val config = guice.dependency[Config]

  override def onRouteRequest(request: RequestHeader): Option[Handler] = {
    logger.debug(s"routing request ${request.method} ${request.path}")
    MDC.put("clientip", request.headers.get("X-Real-IP").getOrElse("N/A"))
    val strippedRequest = remoteAssets.stripGitSha(request)
    super.onRouteRequest(strippedRequest)
  }

  override def onStart(app: Application) {
    super.onStart(app)
    val initGuice = guice
  }

  override def onHandlerNotFound(request: RequestHeader) = {
    Future.successful {
      NotFound(ErrorPage.NotFound(request.path))
    }
  }

  override def onError(request: RequestHeader, ex: Throwable) = {
    logger.error(s"uncaught exception when routing request ${request.method} ${request.path}", ex)
    Future.successful {
      InternalServerError(ErrorPage.ServerError())
    }
  }

  override def getControllerInstance[A](controllerClass: Class[A]): A = {
    guice.dependency(controllerClass)
  }
}
