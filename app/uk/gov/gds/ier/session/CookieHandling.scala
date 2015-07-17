package uk.gov.gds.ier.session

import play.api.mvc.{Cookie, DiscardingCookie}
import uk.gov.gds.ier.guice.{WithEncryption, WithConfig}
import uk.gov.gds.ier.serialiser.WithSerialiser
import uk.gov.gds.ier.step.InprogressApplication
import uk.gov.gds.ier.transaction.complete.CompleteCookie

trait CookieHandling extends SessionKeys {
  self: WithSerialiser
    with WithEncryption
    with WithConfig =>

  def payloadCookies[A <: InprogressApplication[A]](
    application: A,
    domain: Option[String]
  ) = {
    val payloadString = serialiser.toJson(application)
    val (payloadHash, payloadIV) = encryptionService.encrypt(payloadString)
    Seq(
      secureCookie(sessionPayloadKey, payloadHash, domain),
      secureCookie(sessionPayloadKeyIV, payloadIV, domain)
    )
  }

  def discardPayloadCookies(
    domain: Option[String]
  ) = Seq(
    discard(sessionPayloadKey, domain),
    discard(sessionPayloadKeyIV, domain)
  )

  def completeCookies(
    completePayload: CompleteCookie,
    domain: Option[String]
  ) = {
    val payloadString = serialiser.toJson(completePayload)
    val (payloadHash, payloadIV) = encryptionService.encrypt(payloadString)
    Seq(
      secureCookie(completeCookieKey, payloadHash, domain),
      secureCookie(completeCookieKeyIV, payloadIV, domain)
    )
  }

  def discardCompleteCookies(
    domain: Option[String]
  ) = Seq(
    discard(completeCookieKey, domain),
    discard(completeCookieKeyIV, domain)
  )

  def tokenCookies(
    token: SessionToken,
    domain: Option[String]
  ) = {
    val tokenString = serialiser.toJson(token)
    val (tokenHash, tokenIV) = encryptionService.encrypt(tokenString)
    Seq(
      secureCookie(sessionTokenKey, tokenHash, domain),
      secureCookie(sessionTokenKeyIV, tokenIV, domain)
    )
  }

  def discardTokenCookies(
    domain: Option[String]
  ) = Seq(
    discard(sessionTokenKey, domain),
    discard(sessionTokenKeyIV, domain)
  )

  def discard(
    name: String,
    domain: Option[String]
  ): DiscardingCookie = {
    DiscardingCookie(
      name = name,
      domain = domain,
      secure = config.cookiesSecured
    )
  }

  def secureCookie(
    name: String,
    value: String,
    domain: Option[String]
  ): Cookie = {
    Cookie (name, value, None, "/", domain, config.cookiesSecured, true)
  }
}
