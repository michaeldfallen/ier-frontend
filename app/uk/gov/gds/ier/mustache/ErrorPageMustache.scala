package uk.gov.gds.ier.mustache

import uk.gov.gds.ier.guice.{WithRemoteAssets, WithConfig}

trait ErrorPageMustache extends InheritedGovukMustache {
  self: WithRemoteAssets
    with WithConfig =>

  object ErrorPage {

    case class ServerError() extends ArticleMustachio ("error/serverError")

    case class NotFound(url: String) extends ArticleMustachio ("error/notFound")

    case class Timeout(
        timeout: Int,
        override val startUrl: String
    ) extends ArticleMustachio ("error/timeout")
  }
}
