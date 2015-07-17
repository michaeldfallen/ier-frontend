package uk.gov.gds.ier.transaction.crown.openRegister

import uk.gov.gds.ier.transaction.crown.CrownControllers
import com.google.inject.{Inject, Singleton}
import uk.gov.gds.ier.serialiser.JsonSerialiser
import uk.gov.gds.ier.validation._
import uk.gov.gds.ier.config.Config
import uk.gov.gds.ier.security.EncryptionService
import uk.gov.gds.ier.step.{CrownStep, Routes}
import uk.gov.gds.ier.transaction.crown.InprogressCrown
import uk.gov.gds.ier.assets.RemoteAssets

@Singleton
class OpenRegisterStep @Inject ()(
    val serialiser: JsonSerialiser,
    val config: Config,
    val encryptionService : EncryptionService,
    val remoteAssets: RemoteAssets,
    val crown: CrownControllers
) extends CrownStep
  with OpenRegisterForms
  with OpenRegisterMustache {

  val validation = openRegisterForm

  val routing = Routes(
    get = routes.OpenRegisterStep.get,
    post = routes.OpenRegisterStep.post,
    editGet = routes.OpenRegisterStep.editGet,
    editPost = routes.OpenRegisterStep.editPost
  )

  def nextStep(currentState: InprogressCrown) = {
    crown.WaysToVoteStep
  }
  override def isStepComplete(currentState: InprogressCrown) = {
    currentState.openRegisterOptin.isDefined
  }
}
