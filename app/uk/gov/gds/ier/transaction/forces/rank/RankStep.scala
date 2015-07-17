package uk.gov.gds.ier.transaction.forces.rank

import uk.gov.gds.ier.transaction.forces.ForcesControllers
import com.google.inject.{Inject, Singleton}
import uk.gov.gds.ier.serialiser.JsonSerialiser
import uk.gov.gds.ier.config.Config
import uk.gov.gds.ier.security.EncryptionService
import uk.gov.gds.ier.step.{ForcesStep, Routes}
import uk.gov.gds.ier.transaction.forces.InprogressForces
import uk.gov.gds.ier.assets.RemoteAssets

@Singleton
class RankStep @Inject ()(
    val serialiser: JsonSerialiser,
    val config: Config,
    val encryptionService : EncryptionService,
    val remoteAssets: RemoteAssets,
    val forces: ForcesControllers
) extends ForcesStep
  with RankForms
  with RankMustache {

  val validation = rankForm

  val routing = Routes(
    get = routes.RankStep.get,
    post = routes.RankStep.post,
    editGet = routes.RankStep.editGet,
    editPost = routes.RankStep.editPost
  )

  def nextStep(currentState: InprogressForces) = {
    forces.ContactAddressStep
  }
}
