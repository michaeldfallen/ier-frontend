package uk.gov.gds.ier.transaction.forces.nino

import uk.gov.gds.ier.transaction.forces.ForcesControllers
import com.google.inject.{Inject, Singleton}
import uk.gov.gds.ier.serialiser.JsonSerialiser
import uk.gov.gds.ier.validation._

import uk.gov.gds.ier.config.Config
import uk.gov.gds.ier.security.EncryptionService
import uk.gov.gds.ier.step.{ForcesStep, Routes}
import uk.gov.gds.ier.transaction.forces.InprogressForces
import uk.gov.gds.ier.assets.RemoteAssets

@Singleton
class NinoStep @Inject ()(
    val serialiser: JsonSerialiser,
    val config: Config,
    val encryptionService : EncryptionService,
    val remoteAssets: RemoteAssets,
    val forces: ForcesControllers
) extends ForcesStep
  with NinoForms
  with NinoMustache {

  val validation = ninoForm

  val routing = Routes(
    get = routes.NinoStep.get,
    post = routes.NinoStep.post,
    editGet = routes.NinoStep.editGet,
    editPost = routes.NinoStep.editPost
  )

  def nextStep(currentState: InprogressForces) = {
    forces.ServiceStep
  }
}

