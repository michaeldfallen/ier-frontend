package uk.gov.gds.ier.transaction.complete

import uk.gov.gds.ier.mustache.InheritedGovukMustache
import uk.gov.gds.ier.guice.{WithRemoteAssets, WithConfig}
import uk.gov.gds.ier.service.apiservice.EroAuthorityDetails
import uk.gov.gds.ier.langs.Messages
import play.api.i18n.Lang

trait CompleteMustache {
  self: WithRemoteAssets
    with WithConfig =>

  val _config = config
  val _remoteAssets = remoteAssets

  object Complete
    extends InheritedGovukMustache
    with WithRemoteAssets
    with WithConfig {

    val config = _config
    val remoteAssets = _remoteAssets

    case class CompletePage (
        authority: Option[EroAuthorityDetails],
        refNumber: String,
        hasOtherAddress: Boolean,
        backToStartUrl: String,
        showEmailConfirmation: Boolean,
        showBirthdayBunting: Boolean,
        surveyLink: String
    ) (
        implicit override val lang: Lang
    ) extends InheritedMustachio("complete") {

      override val contentClasses = "complete"
      override val pageTitle = Messages("complete_step_title")
      override val sourcePath = routes.CompleteStep.complete.url

      val authorityUrl = authority flatMap {
        auth => auth.urls.headOption
      }

      val authorityName = authority map {
        auth => auth.name + " " + Messages("complete_electoralRegistrationOffice")
      } getOrElse Messages("complete_unspecificElectoralRegistrationOffice")
    }
  }
}
