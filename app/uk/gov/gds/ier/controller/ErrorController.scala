package uk.gov.gds.ier.controller

import play.api.mvc._
import uk.gov.gds.ier.config.Config
import com.google.inject.Inject
import uk.gov.gds.ier.guice.{WithRemoteAssets, WithEncryption, WithConfig}
import uk.gov.gds.ier.serialiser.{WithSerialiser, JsonSerialiser}
import uk.gov.gds.ier.security.EncryptionService
import uk.gov.gds.ier.session.SessionCleaner
import uk.gov.gds.ier.logging.Logging
import uk.gov.gds.ier.mustache.ErrorPageMustache
import uk.gov.gds.ier.assets.RemoteAssets
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import com.google.inject.Singleton

@Singleton
class ErrorController @Inject ()(
    val serialiser: JsonSerialiser,
    val config: Config,
    val encryptionService : EncryptionService,
    val remoteAssets: RemoteAssets
) extends Controller
  with SessionCleaner
  with ErrorPageMustache
  with WithSerialiser
  with WithRemoteAssets
  with Logging
  with WithConfig
  with WithEncryption {

  def ordinaryTimeout = ClearSession in Action { request =>
    Ok(ErrorPage.Timeout(
      config.sessionTimeout,
      config.ordinaryStartUrl
    ))
  }

  def forcesTimeout = ClearSession in Action { request =>
    Ok(ErrorPage.Timeout(
      config.sessionTimeout,
      config.forcesStartUrl
    ))
  }

  def crownTimeout = ClearSession in Action { request =>
    Ok(ErrorPage.Timeout(
      config.sessionTimeout,
      config.crownStartUrl
    ))
  }

  def serverError = ClearSession in Action {
    request =>
      InternalServerError(ErrorPage.ServerError())
  }

  def notFound = ClearSession in Action {
    request =>
      NotFound(ErrorPage.NotFound(""))
  }
}
