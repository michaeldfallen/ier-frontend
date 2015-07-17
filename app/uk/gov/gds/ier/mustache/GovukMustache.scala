package uk.gov.gds.ier.mustache

import uk.gov.gds.ier.controller.routes.RegisterToVoteController
import uk.gov.gds.ier.guice.{WithRemoteAssets, WithConfig}
import play.api.templates.Html

trait GovukMustache extends InheritedGovukMustache {
  self: WithRemoteAssets
    with WithConfig =>

  object Govuk {

    abstract class StartPage(
        templatePath: String
    ) extends InheritedMustachio(templatePath) {
      override val headerClass = ""

      val mainstream = remoteAssets.getAssetPath("stylesheets/mainstream.css").url
      val print = remoteAssets.getAssetPath("stylesheets/print.css").url
      val ie8 = remoteAssets.getAssetPath("stylesheets/application-ie8.css").url
      val ie7 = remoteAssets.getAssetPath("stylesheets/application-ie7.css").url
      val ie6 = remoteAssets.getAssetPath("stylesheets/application-ie6.css").url
      val application = remoteAssets.getAssetPath("stylesheets/application.css").url
      val jquery = remoteAssets.getAssetPath("javascripts/vendor/jquery/jquery-1.10.1.min.js").url
      val core = remoteAssets.getAssetPath("javascripts/core.js").url
    }
  }

  object RegisterToVote {
    trait GovukUrls {
      val startUrl:String
      val registerToVoteUrl:String = config.ordinaryStartUrl
      val registerArmedForcesUrl:String = config.forcesStartUrl
      val registerCrownServantUrl:String = config.crownStartUrl
    }

    case class ForcesStartPage(
        override val startUrl:String = RegisterToVoteController.registerToVoteForcesStart.url
    ) extends Govuk.StartPage("govuk/registerToVoteForces")
      with GovukUrls

    case class CrownStartPage(
        override val startUrl:String = RegisterToVoteController.registerToVoteCrownStart.url
    ) extends Govuk.StartPage("govuk/registerToVoteCrown")
      with GovukUrls

    case class OrdinaryStartPage(
        override val startUrl: String = RegisterToVoteController.registerToVoteStart.url
    ) extends Govuk.StartPage("govuk/registerToVoteOrdinary")
      with GovukUrls

    case class PrivacyPage() extends ArticleMustachio("govuk/privacy")

    case class CookiePage() extends ArticleMustachio("govuk/cookies")
  }
}
