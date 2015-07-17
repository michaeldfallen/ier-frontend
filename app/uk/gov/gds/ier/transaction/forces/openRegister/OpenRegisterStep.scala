package uk.gov.gds.ier.transaction.forces.openRegister

import uk.gov.gds.ier.transaction.forces.ForcesControllers
import com.google.inject.{Inject, Singleton}
import uk.gov.gds.ier.serialiser.JsonSerialiser
import uk.gov.gds.ier.config.Config
import uk.gov.gds.ier.security.EncryptionService
import uk.gov.gds.ier.step.{ForcesStep, Routes}
import uk.gov.gds.ier.transaction.forces.InprogressForces
import uk.gov.gds.ier.assets.RemoteAssets

@Singleton
class OpenRegisterStep @Inject ()(
    val serialiser: JsonSerialiser,
    val config: Config,
    val encryptionService : EncryptionService,
    val remoteAssets: RemoteAssets,
    val forces: ForcesControllers
) extends ForcesStep
  with OpenRegisterForms
  with OpenRegisterMustache {

  val validation = openRegisterForm

  val routing = Routes(
    get = routes.OpenRegisterStep.get,
    post = routes.OpenRegisterStep.post,
    editGet = routes.OpenRegisterStep.editGet,
    editPost = routes.OpenRegisterStep.editPost
  )

  def nextStep(currentState: InprogressForces) = {
    forces.WaysToVoteStep
  }
  override def isStepComplete(currentState: InprogressForces) = {
    currentState.openRegisterOptin.isDefined
  }
}
