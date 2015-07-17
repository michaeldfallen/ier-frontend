package uk.gov.gds.ier.transaction.complete

import play.api.mvc._
import com.google.inject.Inject
import uk.gov.gds.ier.serialiser.{WithSerialiser, JsonSerialiser}
import uk.gov.gds.ier.guice.{WithRemoteAssets, WithEncryption, WithConfig}
import uk.gov.gds.ier.config.Config
import uk.gov.gds.ier.security.EncryptionService
import uk.gov.gds.ier.assets.RemoteAssets
import uk.gov.gds.ier.logging.Logging
import uk.gov.gds.ier.session.{RequestHandling, ResultHandling}

class CompleteStep @Inject() (
    val serialiser: JsonSerialiser,
    val config: Config,
    val encryptionService: EncryptionService,
    val remoteAssets: RemoteAssets
  ) extends Controller
  with ResultHandling
  with RequestHandling
  with WithSerialiser
  with WithConfig
  with WithEncryption
  with WithRemoteAssets
  with Logging
  with CompleteMustache {

  def overseasComplete = complete

  def complete = Action {
    implicit request =>
      request.getCompleteCookie match {
        case Some(confirmationData) => {
          Ok(Complete.CompletePage(
            confirmationData.authority,
            confirmationData.refNum,
            confirmationData.hasOtherAddress,
            confirmationData.backToStartUrl,
            confirmationData.showEmailConfirmation,
            confirmationData.showBirthdayBunting,
            config.completeSurveyLink
          ))
        }
        case None => {
          logger.debug(s"Validate session - Request has no token, refreshing and redirecting to govuk start page")
          Redirect(config.ordinaryStartUrl).emptySession()
        }
      }
  }
}
