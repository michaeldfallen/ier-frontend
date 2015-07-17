package uk.gov.gds.ier.transaction.crown.declaration

import uk.gov.gds.ier.transaction.crown.CrownControllers
import com.google.inject.{Inject, Singleton}
import uk.gov.gds.ier.serialiser.JsonSerialiser
import uk.gov.gds.ier.config.Config
import uk.gov.gds.ier.security.EncryptionService
import uk.gov.gds.ier.step.{Routes, CrownStep}
import uk.gov.gds.ier.transaction.crown.InprogressCrown
import uk.gov.gds.ier.service.{DeclarationPdfDownloadService, WithDeclarationPdfDownloadService}
import uk.gov.gds.ier.assets.RemoteAssets

@Singleton
class DeclarationPdfStep @Inject ()(
    val serialiser: JsonSerialiser,
    val config: Config,
    val encryptionService : EncryptionService,
    val declarationPdfDownloadService: DeclarationPdfDownloadService,
    val remoteAssets: RemoteAssets,
    val crown: CrownControllers
) extends CrownStep
  with WithDeclarationPdfDownloadService
  with DeclarationPdfForms
  with DeclarationPdfMustache {

  val validation = declarationPdfForm

  val routing = Routes(
    get = routes.DeclarationPdfStep.get,
    post = routes.DeclarationPdfStep.post,
    editGet = routes.DeclarationPdfStep.editGet,
    editPost = routes.DeclarationPdfStep.editPost
  )

  def nextStep(currentState: InprogressCrown) = {
    crown.NinoStep
  }
}

