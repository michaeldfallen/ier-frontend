package uk.gov.gds.ier.transaction.crown.statement

import uk.gov.gds.ier.transaction.crown.CrownControllers
import com.google.inject.{Inject, Singleton}
import uk.gov.gds.ier.step.{CrownStep, Routes}
import uk.gov.gds.ier.config.Config
import uk.gov.gds.ier.security.EncryptionService
import uk.gov.gds.ier.serialiser.JsonSerialiser
import uk.gov.gds.ier.validation.ErrorTransformForm
import uk.gov.gds.ier.transaction.crown.InprogressCrown
import uk.gov.gds.ier.assets.RemoteAssets

@Singleton
class StatementStep @Inject ()(
    val serialiser:JsonSerialiser,
    val config: Config,
    val encryptionService: EncryptionService,
    val remoteAssets: RemoteAssets,
    val crown: CrownControllers
) extends CrownStep
    with StatementForms
    with StatementMustache {

  val validation = statementForm

  val routing = Routes(
    get = routes.StatementStep.get,
    post = routes.StatementStep.post,
    editGet = routes.StatementStep.editGet,
    editPost = routes.StatementStep.editPost
  )

  def nextStep(currentState: InprogressCrown) = {
    crown.AddressFirstStep
  }
}
