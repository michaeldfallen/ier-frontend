package uk.gov.gds.ier.session

import uk.gov.gds.ier.serialiser.WithSerialiser
import play.api.mvc._
import uk.gov.gds.ier.logging.Logging
import scala.Some
import uk.gov.gds.ier.guice.{WithEncryption, WithConfig}
import uk.gov.gds.ier.step.InprogressApplication
import scala.concurrent.{ExecutionContext, Future}

abstract class SessionHandling[T <: InprogressApplication[T]]
  extends RequestHandling
  with ResultHandling
  with SessionTokenValidator {
  self: WithSerialiser
    with Controller
    with Logging
    with WithConfig
    with WithEncryption =>

  implicit class ResultWithRefreshing(result: SimpleResult) {
     def refreshToken()(implicit request: Request[_]) = {
      val token = request.getToken
      token match {
        case Some(session) => result storeToken session.refreshToken
        case None => result
      }
    }
  }

  def application(
      implicit request:Request[_],
      manifest: Manifest[T]
  ): T = {
    request.getApplication getOrElse factoryOfT()
  }

  def factoryOfT():T

  def timeoutPage(): Call

  object ValidSession {

    def in[A](
        block: Action[A]
    ) (
        implicit context: ExecutionContext
    ) = Action.async(block.parser) { implicit request =>
      logger.debug(s"REQUEST ${request.method} ${request.path} - Valid Session needed")
      request.getToken match {
        case Some(token) => {
          token.isValid match {
            case true => {
              logger.debug(s"Validate session - token is valid")
              block(request)
            }
            case false => {
              logger.debug(s"Validate session - token is not valid ${serialiser.toJson(token)}")
              Future { Redirect(timeoutPage()).emptySession() }
            }
          }
        }
        case None => {
          logger.debug(s"Validate session - Request has no token, redirecting to govuk start page")
          Future { Redirect(config.ordinaryStartUrl).emptySession() }
        }
      }
    }
  }
}
