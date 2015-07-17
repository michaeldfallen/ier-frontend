package uk.gov.gds.ier.session

import uk.gov.gds.ier.guice.{WithEncryption, WithConfig}
import uk.gov.gds.ier.serialiser.WithSerialiser
import uk.gov.gds.ier.step.InprogressApplication
import play.api.mvc.{SimpleResult, Result, Request}
import uk.gov.gds.ier.transaction.complete.CompleteCookie
import scala.concurrent.Future
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import uk.gov.gds.ier.model.StartupApplication

trait ResultHandling extends CookieHandling {
  self: WithConfig
    with WithEncryption
    with WithSerialiser =>

  implicit class ResultWithCookieOps(result:SimpleResult) extends SessionKeys {

    def storeInSession[B <: InprogressApplication[B]](
      application:B
    ) (
      implicit request: Request[_]
    ) = {
      val domain = getDomain(request)
      result.withCookies(payloadCookies(application, domain):_*)
    }

    def storeToken(
      token: SessionToken
    ) (
      implicit request: Request[_]
    ) = {
      val domain = getDomain(request)
      result.withCookies(tokenCookies(token, domain):_*)
    }

    def storeCompleteCookie(
      token: CompleteCookie
    ) (
      implicit request: Request[_]
    ) = {
      val domain = getDomain(request)
      result.withCookies(completeCookies(token, domain):_*)
    }

    def emptySession()(implicit request: Request[_]) = {
      val domain = getDomain(request)
      result.discardingCookies(
        discardPayloadCookies(domain) ++ discardTokenCookies(domain) ++ discardCompleteCookies(domain):_*
      )
    }

    def withFreshSession()(implicit request: Request[_]) = {
      val domain = getDomain(request)
      val sessionId = Some(java.util.UUID.randomUUID.toString)
      val resultWithToken = result storeToken SessionToken()
      val newApplication = new StartupApplication(sessionId = sessionId)
      resultWithToken.withCookies(payloadCookies(newApplication, domain):_*)
    }
  }

  def getDomain(request: Request[_]) = {
    request.headers.get("host") filterNot {
      _ startsWith "localhost"
    } filterNot {
      _ == ""
    } map {
      _.split(":").head
    }
  }
}
