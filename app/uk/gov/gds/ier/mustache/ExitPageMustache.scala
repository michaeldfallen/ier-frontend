package uk.gov.gds.ier.mustache

import uk.gov.gds.ier.guice.{WithRemoteAssets, WithConfig}
import uk.gov.gds.ier.langs.Messages

trait ExitPageMustache extends InheritedGovukMustache {
  self: WithRemoteAssets
    with WithConfig =>

  object ExitPages {

    case class BritishIslands() (
        implicit override val lang: Lang
    ) extends ArticleMustachio("exit/britishIslands")

    case class DontKnow() (
        implicit override val lang: Lang
    ) extends ArticleMustachio("exit/dontKnow")

    case class NoFranchise() (
      implicit override val lang: Lang
    ) extends ArticleMustachio("exit/noFranchise")

    case class NorthernIreland() (
        implicit override val lang: Lang
    ) extends ArticleMustachio("exit/northernIreland")

    case class Scotland() (
      implicit override val lang: Lang
    ) extends ArticleMustachio("exit/scotland")

    case class TooYoung () (
        implicit override val lang: Lang
    ) extends ArticleMustachio("exit/tooYoung")

    case class Under18 () (
        implicit override val lang: Lang
    ) extends ArticleMustachio("exit/under18")

    case class LeftService () extends ArticleMustachio("exit/leftService")

    case class LeftUk () extends ArticleMustachio("exit/leftUk")

    case class TooOldWhenLeft () extends ArticleMustachio("exit/tooOldWhenLeft")
  }
}
